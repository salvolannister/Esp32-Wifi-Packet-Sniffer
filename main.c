/**
 * Copyright (c) 2017, Łukasz Marcin Podkalicki <lpodkalicki@gmail.com>
 * ESP32/016
 * WiFi Sniffer.
 */

#include "freertos/FreeRTOS.h"
#include "freertos/event_groups.h"
#include "freertos/task.h"
#include <sys/param.h>
#include "esp_wifi.h"
#include "esp_wifi_types.h"
#include "esp_system.h"
#include "esp_event.h"
#include "esp_event_loop.h"
#include "nvs_flash.h"
#include "driver/gpio.h"
#include <string.h>
#include "mbedtls/md5.h"
#include "esp_log.h"
#include "apps/sntp/sntp.h"
#include <stdlib.h>
//tcp connection
#include "lwip/err.h"
#include "lwip/sockets.h"
#include "lwip/sys.h"
#include <lwip/netdb.h>

#define	LED_GPIO_PIN			GPIO_NUM_4
#define	WIFI_CHANNEL_MAX		(13)
#define	WIFI_CHANNEL_SWITCH_INTERVAL	(500)
#define NO_SSID "no ssid"
#define SNIFFING_TIME 30 // tempo di sniffamento in secondi
#define PORT "8080" //Server port


// SALVA AP configuration
/*#define EXAMPLE_WIFI_SSID "cacau"
#define EXAMPLE_WIFI_PASS "cacauthimth"
#define HOST_IP_ADDR "192.168.0.10" //SERVER IP ADDRES
*/

//DAVIDE AP configuration
#define EXAMPLE_WIFI_SSID "Ntani"
#define EXAMPLE_WIFI_PASS "davidedavide"
//#define HOST_IP_ADDR "192.168.43.7" //Server ip addres

//SERVER ADDRESS UMBERTO!!
#define HOST_IP_ADDR "192.168.43.26" //Server ip addres


static bool FIRST = true; /* Only used in startup: if obtain_time() can't set current time for the first time -> reboot() */
//connection variables
static wifi_country_t wifi_country = {.cc="CN", .schan=1, .nchan=13, .policy=WIFI_COUNTRY_POLICY_AUTO};
static const char *TAG = "ESP32-SniffingProject"; //used to log function
const int IPV4_GOTIP_BIT = BIT0;
static const char *payload = "Message from ESP32 \n";
bool IsConnected = false;

/* FreeRTOS event group to signal when we are connected & ready to make a request */
static EventGroupHandle_t wifi_event_group;

typedef struct {
	unsigned frame_ctrl:16;
	unsigned duration_id:16;
	uint8_t addr1[6]; /* receiver address */
	uint8_t addr2[6]; /* sender address */
	uint8_t addr3[6]; /* filtering address */
	unsigned sequence_ctrl:16;
	uint8_t addr4[6]; /* optional */
} wifi_ieee80211_mac_hdr_t;

typedef struct {
	wifi_ieee80211_mac_hdr_t hdr;
	uint8_t payload[0]; /* network data ended with 4 bytes csum (CRC32) */
} wifi_ieee80211_packet_t;

typedef struct reduced_wifi_pkt_rx_ctrl_t{
    signed rssi:8;
    unsigned channel:4;
    uint8_t mac_src[6];
    char *ssid;
    uint8_t length_ssid;
    int time;
}reduced_info;

//dynamic data structure to contain sniffed packets
typedef struct P_array{
	reduced_info* array; /*array of  packets*/
	int count;    /* number of actual packet -> position is count-1 */
	int dim; /* dimension of array */
}P_array;

//connecting function
static void wait_for_ip();
static void tcp_sendPacket();
static struct sockaddr_in tcp_init();
void startSniffingPacket();
static int tcp_hello();


static esp_err_t event_handler(void *ctx, system_event_t *event);
static void wifi_sniffer_init(void);
static void wifi_sniffer_set_channel(uint8_t channel);
static const char *wifi_sniffer_packet_type2str(wifi_promiscuous_pkt_type_t type);
static void wifi_sniffer_packet_handler(void *buff, wifi_promiscuous_pkt_type_t type);

/*function to manage data storage*/
P_array P_allocate(int dim);
void P_push(P_array* sniffed_packet, reduced_info x);
void P_free(P_array* sniffed_packet);
void P_resize(P_array* sniffed_packet);
void P_printer(P_array sniffed_packet);
//hashing function
void ComputHashMD5();

P_array Sniffed_packet;
bool stopSniffing = false;
int rebooted = 1;
//mac address
uint8_t espMac[6];

 /*struttura che viene aggiornata con time,
 mostra il tempo passato da una det. data*/



static void initialize_sntp();
static int get_start_timestamp();
static int set_waiting_time();
static void obtain_time();
static void reboot(char *msg_err);

void
app_main(void)
{
    int start_time;
    time_t now; /*struttura che viene aggiornata con time, mostra il tempo passato da una det. data*/
   // struct tm timeinfo;/*struttura per accedere ai campi di now*//*struttura per accedere ai campi di now*/
    char buffer[100];

	//uint8_t channel = 1;
    Sniffed_packet=P_allocate(40);

	/* setup wifi*/
	wifi_sniffer_init();
	wait_for_ip();

    obtain_time();
    time(&now);
    start_time =(int) now;
    printf("START TIME IS : %d\n",start_time);

	//init MAC
	esp_efuse_mac_get_default(espMac); //get mac address

	int waitingTime = tcp_hello(); //TODO: CALL STARTSNIFFINGPACKET AFTER RECEIVING THE STARTING TIME FROM THE SERVER AND WAITING UNTIL THIS TIME!!!!!!!!!!!!!!!!!!!!!!!!!
	//wait
	printf("--------------- %d SECONDS WAITING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!------------------ \n", waitingTime);
	vTaskDelay(waitingTime*1000 / portTICK_PERIOD_MS);
	printf("---------------START SNIFFING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!------------------ \n");

	/* starting promiscue mode*/
    startSniffingPacket();

	/* loop */
	while (true) {

        int sleep_time = SNIFFING_TIME*1000;
		//after tot seconds stop sniffing packets, print the result and send message to server. Then, restart sniffing packets.
		vTaskDelay(sleep_time/portTICK_PERIOD_MS);
		printf("--------------- %d SEC PASSED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!------------------ \n", (int)SNIFFING_TIME);
		stopSniffing = true;
		if (Sniffed_packet.dim > 0)
		{
			P_printer(Sniffed_packet);
			tcp_sendPacket();
			P_free(&Sniffed_packet);
			Sniffed_packet = P_allocate(40);
		}
		else
			printf("no packet sniffed");

		waitingTime = tcp_hello(); //send mac address to server and ask for waiting time
		if (waitingTime <= 0)
			reboot("error in tcp_hello function!");
		printf("--------------- %d SECONDS WAITING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!------------------ \n", waitingTime);
		vTaskDelay(waitingTime * 1000 / portTICK_PERIOD_MS);
		printf("--------------- RESTART SNIFFING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!------------------ \n");

		stopSniffing = false;

		/*vTaskDelay(WIFI_CHANNEL_SWITCH_INTERVAL / portTICK_PERIOD_MS);
		wifi_sniffer_set_channel(channel);
		channel = (channel % WIFI_CHANNEL_MAX) + 1;
		if (CaptureFinish) {
			printf("capture finished!!! \n")
			esp_wifi_set_promiscuous(false);
			tcp_sendPacket();
			CaptureFinish = false;
		}*/
    }


}

P_array P_allocate(int dim){

    dim = dim+1;
	P_array parray;
	parray.array = (reduced_info* )malloc(dim*sizeof(reduced_info));
	if(parray.array==NULL){
        printf("Not enough memory at initialating \n");
        exit(-1);
	};

    parray.count=0;
	parray.dim=dim;

	return parray;
}

void P_resize(P_array* sniffed_packet){
    int new_dim = sniffed_packet->dim*2;
    /* new_dim+= 40; */
    printf("Sto eseguendo la resize \n");
        sniffed_packet->array=(reduced_info *) realloc(sniffed_packet->array,new_dim*sizeof(reduced_info));
        if(sniffed_packet->array == NULL){
            printf("No more space to realloc\n");
            exit(-1);
        }

        sniffed_packet->dim=new_dim;

}

void P_push(P_array* sniffed_packet, reduced_info p){
  int n_packets = sniffed_packet->count;
  printf("Adding captured packet. Buffer dim = %d ; num elements = %d\n", sniffed_packet->dim, n_packets);

  if((sniffed_packet->dim) <= n_packets)
        P_resize(sniffed_packet);

  sniffed_packet->array[n_packets]=p;
  sniffed_packet->count++;
}

void P_free(P_array* sniffed_packet){

    free(sniffed_packet->array);
    sniffed_packet->array=NULL;
    sniffed_packet->count=0;
    sniffed_packet->dim=0;
    //free(sniffed_packet);
}

void P_printer(P_array sniffed_packet) {
	reduced_info x;
	int i = 0;

	for (i = 0; i< sniffed_packet.count; i++) {
		x = sniffed_packet.array[i];
		printf("CHAN=%02d, RSSI=%02d ", x.channel, x.rssi);
		if (x.length_ssid != 0) {
			printf(" SSID_length=%d SSID_%s", x.length_ssid, x.ssid);
		}
		printf(" MAC_SRC=%02x:%02x:%02x:%02x:%02x:%02x",
			x.mac_src[0], x.mac_src[1], x.mac_src[2],
			x.mac_src[3], x.mac_src[4], x.mac_src[5]);
        printf(" t: %d\n",x.time);
	}
}

/*
compute the MD5 of the string passaed as parameter. The result is in a string format
and returned by setting the content of buf variable passed as parameter.
*/
void ComputHashMD5(const unsigned char* string, char* buf) {

	struct mbedtls_md5_context contextMD5; //MD5 context structure. Data fields: Total, state, buffer

	//const unsigned char* string = (const unsigned char*) "Testo di prova";
	unsigned char data[16]; //it will contain the final output -> digest. MD5 return 128bit = 16byte.

	//testing: we fill myContext memory space with 0x00 repeated until sizeof(myContext). Similarly for data memory space
	memset(&contextMD5, 0x00, sizeof(contextMD5));
	memset(data, 0x00, 16);

	mbedtls_md5_init(&contextMD5); //init the context
	mbedtls_md5_starts_ret(&contextMD5); //setup the context
	if(mbedtls_md5_update_ret(&contextMD5, (const unsigned char*) string, strlen((const char*) string))) //params: MD5 context, buffer holding the data, length of the input data
	{
		printf("error in digest computetion \n");
	}
	mbedtls_md5_finish_ret(&contextMD5, data); //the final digest. Params: MD5 context, MD5 checksum result.
	mbedtls_md5_free(&contextMD5); //free MD5 context

	/*printf("printf of the digest  :\n");
	for (int i = 0; i < sizeof(data); i++)
		printf("%02x", data[i]);
	printf("\n");*/

	/*char output[128];
	sprintf(output, "%02x", data[0]);
	for (int i = 1; i < sizeof(data); i++) {
		char temp[2];
		sprintf(temp, "%02x", data[i]); //temp contain the 2 characters that rappresent the byte (2 exadecimal number)
		strcat(output, temp);
	}
	printf("sprint function output: %s \n", output);*/
	//char buf[128];


	// STAMPA
	sprintf(buf, "%02x", data[0]);
	for (int i = 1; i < sizeof(data); i++) {
		char temp[2];
		sprintf(temp, "%02x", data[i]); //temp contain the 2 characters that rappresent the byte (2 exadecimal number)
		strcat(buf, temp);
	}
	/*
	//pritn source string and digesto in console
	printf("string to compute: %s\n", string);
	printf("sprint function output: %s \n", buf);
	*/
}

/*
Event handler is used to tie events from WiFi/Ethernet/LwIP stacks into application logic.
*/
// Wifi event handler
static esp_err_t event_handler(void *ctx, system_event_t *event)
{
	switch (event->event_id) {

	/*
	after having completed its internal tasks, the driver notifies that it has successfully started triggering the event SYSTEM_EVENT_STA_START
	*/
	case SYSTEM_EVENT_STA_START:
		ESP_LOGI(TAG, "SYSTEM_EVENT_STA_START");
		//the event handler, once received that event, can call the esp_wifi_connect() API to ask the driver to connect to the network specified during the configuration phase
		printf("try to connect at the AP \n");
		esp_wifi_connect(); 
		break;

	/*
	when the connection to the AP is completed and after having obtained a valid IP address, the driver triggers the event SYSTEM_EVENT_STA_GOT_IP	
	*/
	case SYSTEM_EVENT_STA_GOT_IP:
		xEventGroupSetBits(wifi_event_group, IPV4_GOTIP_BIT); //now the event handler can inform the main program that the connection has been completed
		printf("esp32 is connectd and has an IP address \n");
		ESP_LOGI(TAG, "SYSTEM_EVENT_STA_GOT_IP");
		break;

	case SYSTEM_EVENT_STA_DISCONNECTED:
		printf("esp32 has been disconnected \n");
		xEventGroupClearBits(wifi_event_group, IPV4_GOTIP_BIT);
		esp_wifi_connect(); //retry to connect
		break;

	default:
		break;
	}

	return ESP_OK;
}

void
wifi_sniffer_init(void)
{

	nvs_flash_init();

	/*
	 the command tcpip_adapter_init() that initializes the lwIP library (that we use in order to menage the tcp connection)
	*/
    tcpip_adapter_init();

	// disable the default wifi logging
	esp_log_level_set("wifi", ESP_LOG_NONE);

	/*
	Firslty we have to indtroduce what is an event bit:
	Event bits are used to indicate if an event has occurred or not. Event bits are often referred to as event flags.
	For example:
	"A message has been received and is ready for processing" when it is set to 1, and "there are no messages waiting to be processed" when it is set to 0.

	THEN:
	An event group is a set of event bits. Individual event bits within an event group are referenced by a bit number.
	Expanding the example provided above:
	The event bit that means "A message has been received and is ready for processing" might be bit number 0 within an event group.
	*/
	wifi_event_group = xEventGroupCreate(); //create the event group.

    ESP_ERROR_CHECK( esp_event_loop_init(event_handler, NULL) ); //function that will be called when there is an event

	wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
	ESP_ERROR_CHECK( esp_wifi_init(&cfg) );
	ESP_ERROR_CHECK( esp_wifi_set_country(&wifi_country) ); /* set country for channel range [1, 13] */
	ESP_ERROR_CHECK( esp_wifi_set_storage(WIFI_STORAGE_RAM) );
	ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));

    //ESP_ERROR_CHECK( esp_wifi_set_mode(WIFI_MODE_NULL) );
	//ESP_ERROR_CHECK( esp_wifi_start() );

	wifi_config_t wifi_config = {
		.sta = {
		.ssid = EXAMPLE_WIFI_SSID,
		.password = EXAMPLE_WIFI_PASS,
	},
	};

	ESP_LOGI(TAG, "Setting WiFi configuration SSID %s...", wifi_config.sta.ssid);

	ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config)); //Set the configuration of the ESP32 STA or AP.

	/*
	Start WiFi according to current configuration If mode is WIFI_MODE_STA,
	it create station control block and start station If mode is WIFI_MODE_AP,
	it create soft-AP control block and start soft-AP If mode is WIFI_MODE_APSTA,
	it create soft-AP and station control block and start soft-AP and station.
	*/
	ESP_ERROR_CHECK(esp_wifi_start());


	/*
	Create a new task and add it to the list of tasks that are ready to run.
	Parameters:
	pvTaskCode: Pointer to the task entry function.
	pcName: A descriptive name for the task.
	usStackDepth: The number of words (not bytes!) to allocate for use as the task's stack
	pvParameters: A value that will passed into the created task as the task's parameter
	uxPriority: The priority at which the created task will execute
	pxCreatedTask: Used to pass a handle to the created task out of the xTaskCreate() function. pxCreatedTask is optional and can be set to NULL.

	Returns:
	If the task was created successfully then pdPASS is returned. Otherwise errCOULD_NOT_ALLOCATE_REQUIRED_MEMORY is returned.
	*/
	//xTaskCreate(tcp_sendPacket, "tcp_client", 4096, NULL, 5, NULL); //try to connect with server socket!!

}

void startSniffingPacket() {
	esp_wifi_set_promiscuous(true);
	esp_wifi_set_promiscuous_rx_cb(&wifi_sniffer_packet_handler);
}

/*
waiting Ip configuration from the AP
*/
static void wait_for_ip()
{
	//uint32_t bits = IPV4_GOTIP_BIT | IPV6_GOTIP_BIT;
	uint32_t bits = IPV4_GOTIP_BIT; //we will wait that it will be setted

	ESP_LOGI(TAG, "Waiting IP from AP...");
	/*
	pauses its execution until the connection to the wifi network is perfomed, waiting for the IPV4_GOTIP_BIT bit to be set.
	The portMAX_DELAY constant will cause the task to block indefinitely (without a timeout).
	*/
	xEventGroupWaitBits(wifi_event_group, bits, false, true, portMAX_DELAY); 
	/*
	Read bits within an RTOS event group, optionally entering the Blocked state (with a timeout)
	to wait for a bit or group of bits to become set.
	*/
	ESP_LOGI(TAG, "Connected to AP");
}

static struct sockaddr_in tcp_init() {
	// wait for connection
	xEventGroupWaitBits(wifi_event_group, IPV4_GOTIP_BIT, false, true, portMAX_DELAY);

	// define connection parameters
	struct sockaddr_in destAddr;
	destAddr.sin_addr.s_addr = inet_addr(HOST_IP_ADDR); //setting the Server IP address
	destAddr.sin_family = AF_INET;
	destAddr.sin_port = htons(8080); //8080 listening server port
	return destAddr;
}

/*
Return socket struct only if connection to server works. If it fail 5 times will reboot the system.
*/
int getSocket() {
	//create connection parameters and return only after ip address configuration!!
	struct sockaddr_in destAddr = tcp_init();

	// try to connect to the specified server
	int attempNum = 5;
	int result = 1;
	int s = 0;
	while (attempNum != 0 && result != 0) //try until result != 0 (connection extablished) or trynumber reached
	{
		// create a new socket
		s = socket(AF_INET, SOCK_STREAM, 0);
		if (s < 0) {
			printf("Unable to allocate a new socket\n");
			//while (1) vTaskDelay(1000 / portTICK_RATE_MS);
		}
		else
			printf("Socket allocated, id=%d\n", s);

		result = connect(s, (struct sockaddr *)&destAddr, sizeof(destAddr));
		if (result != 0) {
			printf("connection to server: attemp remaining %d failed\n", attempNum-1);
			close(s);
			attempNum--;
			//while (1) vTaskDelay(1000 / portTICK_RATE_MS);
		}
		else
			printf("connectd to the server\n");
	}
	if (attempNum == 0) {
		reboot("unable to connect with server");
	}
	return s;

}

static int tcp_hello() {

	printf("sending hello to server... \n");
	char str_hello[200];
	sprintf(str_hello, "Hello, booted = %d and My Mac is: %02x:%02x:%02x:%02x:%02x:%02x\n", rebooted, espMac[0], espMac[1], espMac[2], espMac[3], espMac[4], espMac[5]);
	//printf("Hello, My Mac is: %02x:%02x:%02x:%02x:%02x:%02x\n", espMac[0], espMac[1], espMac[2], espMac[3], espMac[4], espMac[5]);

	char recv_buf[11];
	int attemptNum = 5;
	char timeRec[20];

	while (attemptNum>0)  //try until we have perfomed all send/receive messages
	{
		printf("attemp number: %d", (5-attemptNum));
		int s = getSocket();

		//send hello to server
		int result = write(s, str_hello, strlen(str_hello));
		if (result < 0) {
			printf("Unable to send data\n");
			close(s);
			attemptNum--;
			vTaskDelay(2000 / portTICK_RATE_MS);
			continue; //retry!
		}
		vTaskDelay(2000 / portTICK_PERIOD_MS);

		int r;
		bzero(timeRec, sizeof(timeRec));
		int bytenum = 0;
		do {
			bzero(recv_buf, sizeof(recv_buf));
			r = read(s, recv_buf, sizeof(recv_buf) - 1); //read return the number of bytes recived!!
			for (int i = 0; i < r; i++) {
				printf("data coming from server\n");
				printf(" %c\n", recv_buf[i]);
				timeRec[bytenum+i] = recv_buf[i];
			}
			bytenum += r;
			
		} while (r > 0);

		if (r < 0) {
			printf("Unable to receive data\n");
			close(s);
			attemptNum--;
			bzero(timeRec, sizeof(timeRec));
			continue; //retry!
		}
		//r = read(s, recv_buf, sizeof(recv_buf) - 1); //read return the number of bytes recived!!
		printf("Starting time received from server: %s\n", timeRec);
		

		//close socket
		close(s);
		printf("Socket closed\n");

		break;	//exit from while
	}

	time_t ts;
	time(&ts);
	int startTime = atoi(timeRec);
	printf("starting time: %d", startTime);
	printf("My time: %d \n", (int)ts);

	int waitingtime = startTime - (int)ts;
	printf("waiting time: %d\n", waitingtime);

	//set rebooted to 0. If the system reboot reset rebooted to 1
	//so, we can check in the server if the system has rebooted. 
	//this is possible because it's written in hello message!
	//ONLY IN FIRST MESSAGE REBOOTED VARIABLE SHOULD BE SET TO 1.
	rebooted = 0;
	return waitingtime;
	
}

static void tcp_sendPacket()
{   
	int start_time, ora, sleep_time;
    time_t now;
    struct tm timeinfo;
    char buffer[100];
	printf("tcp task started \n");

	/*
	// wait for connection
	xEventGroupWaitBits(wifi_event_group, IPV4_GOTIP_BIT, false, true, portMAX_DELAY);

	// define connection parameters
	struct sockaddr_in destAddr;
	destAddr.sin_addr.s_addr = inet_addr(HOST_IP_ADDR); //setting the Server IP address
	destAddr.sin_family = AF_INET;
	destAddr.sin_port = htons(8080); //8080 listening server port
	*/

    /*ask for the time */
    start_time=(int)get_start_timestamp();
    time(&now);
    ora=(int) now;
	localtime_r(&now, &timeinfo);
    strftime(buffer, sizeof(buffer), "%d/%m/%Y %H:%M:%S", &timeinfo);
    printf("TEMPO in italia:%s agora=%d st=%d\n",buffer,ora,start_time);
	
	int s = getSocket();

	reduced_info x;
	int i;
	int NumPacketSent = 0;
	int result;

	for (i = 0; i < Sniffed_packet.count; i++)
	{
		char temp[1000];
		char string_to_send[1000];
		x = Sniffed_packet.array[i];
		sprintf(string_to_send, "CHAN=%02d/RSSI=%02d", x.channel, x.rssi); //final string = CHAN+RSSI

		if (x.length_ssid != 0) {
			sprintf(temp, "/SSID_length=%d/SSID_%s", x.length_ssid, x.ssid);
			strcat(string_to_send, temp);
		}
		else
		{
			sprintf(temp, "/SSID_length=0");
			strcat(string_to_send, temp);
		}
		//final string = CHAN+RSSI+SSID_lenght+[SSID]

		sprintf(temp, "/ESP_MAC=%02x:%02x:%02x:%02x:%02x:%02x\n", espMac[0], espMac[1], espMac[2], espMac[3], espMac[4], espMac[5]);
		strcat(string_to_send, temp);  //final string = CHAN+RSSI+SSID_lenght+[SSID]+ESP_MAC

		sprintf(temp, "/MAC_SRC=%02x:%02x:%02x:%02x:%02x:%02x/",
			x.mac_src[0], x.mac_src[1], x.mac_src[2],
			x.mac_src[3], x.mac_src[4], x.mac_src[5]);

		char time[500];
		sprintf(time, " TimeStamp=%d/", x.time);

		strcat(temp, time); //now temp is the mac address + timestamp -> values that have to be hashed

		strcat(string_to_send, temp); //now string to send have all data except digest

		/*
		compute hash of the digest of MAC+TS
		*/
		char digest[128];
		ComputHashMD5((unsigned char *)temp, digest); //compute digest of the string temp (=mac + temp) and put the result in digest array
		//sprintf(temp, digest);

		strcat(string_to_send, "Digest=");
		strcat(string_to_send, digest);
		strcat(string_to_send, "/\n");

		printf("Stringa finale: %s /n", string_to_send);


		result = write(s, string_to_send, strlen(string_to_send));
		if (result < 0) {
			printf("Unable to send data\n");
			close(s);
		}
		else
			NumPacketSent++;
	}
	//stop message
	char *stop = "STOP";
	vTaskDelay(2000 / portTICK_PERIOD_MS);

	result = write(s, stop, strlen(stop));
	if (result < 0) {
		printf("Unable to send data\n");
		close(s);
	}

	printf("Num packet sent: %d\n", NumPacketSent);
	close(s);
	printf("Socket closed\n");

}

void
wifi_sniffer_set_channel(uint8_t channel)
{

	esp_wifi_set_channel(channel, WIFI_SECOND_CHAN_NONE);
}

const char *
wifi_sniffer_packet_type2str(wifi_promiscuous_pkt_type_t type)
{
	switch(type) {
	case WIFI_PKT_MGMT: return "MGMT";
	case WIFI_PKT_DATA: return "DATA";
	default:
	case WIFI_PKT_MISC: return "MISC";
	}
}

void
wifi_sniffer_packet_handler(void* buff, wifi_promiscuous_pkt_type_t type)
{
	if (type != WIFI_PKT_MGMT)
		return;

	//when time is over
	if (stopSniffing)
		return;

	const wifi_promiscuous_pkt_t *ppkt = (wifi_promiscuous_pkt_t *)buff;
	const wifi_ieee80211_packet_t *ipkt = (wifi_ieee80211_packet_t *)ppkt->payload;
	const wifi_ieee80211_mac_hdr_t *hdr = &ipkt->hdr;
    reduced_info x;
    time_t ts;

	uint16_t frame = hdr->frame_ctrl;


    uint16_t a, b=64, mask=0xF0;
    a= frame & mask;
    //only look for probe request packets
    if (a!=b)
    {
		return;
    }
    time(&ts);

	char *ssid;
	uint8_t *data=&ppkt->payload;/*new*/
	uint8_t len=data[25], i;
	ssid=(char *) malloc((len+1)*sizeof(char));
	for(i=0; i< len; i++){
		ssid[i]=data[26+i];
	}
	ssid[i]='\0';

	 /*potrebbero esserci dei problemi perché non sappiamo cos'è :8 */
        x.channel=ppkt->rx_ctrl.channel;
    x.length_ssid=len;
    for(i = 0; i < 6 ; i ++)
        x.mac_src[i] = hdr->addr2[i];
    x.rssi = ppkt->rx_ctrl.rssi;
    if(len!=0){
      x.ssid=strdup(ssid);
       if(x.ssid == NULL){
        printf(" no more memory for SSID\n");
        exit(-2);
       }
    }
    x.time=(int) ts;

    /* only probe request are memorized */
    P_push(&Sniffed_packet,x);
//
//	printf("PACKET TYPE=%s, CHAN=%02d, RSSI=%02d, TIME=%d, SSID=%d %s"
//		" ADDR1=%02x:%02x:%02x:%02x:%02x:%02x,"
//		" ADDR2=%02x:%02x:%02x:%02x:%02x:%02x,"
//		" ADDR3=%02x:%02x:%02x:%02x:%02x:%02x\n",
//		wifi_sniffer_packet_type2str(type),
//		ppkt->rx_ctrl.channel,
//		ppkt->rx_ctrl.rssi,
//		ppkt->rx_ctrl.timestamp,/*new*/
//		len, ssid,/*new*/
//		/* ADDR1 */
//		hdr->addr1[0],hdr->addr1[1],hdr->addr1[2],
//		hdr->addr1[3],hdr->addr1[4],hdr->addr1[5],
//		/* ADDR2 */
//		hdr->addr2[0],hdr->addr2[1],hdr->addr2[2],
//		hdr->addr2[3],hdr->addr2[4],hdr->addr2[5],
//		/* ADDR3 */
//		hdr->addr3[0],hdr->addr3[1],hdr->addr3[2],
//		hdr->addr3[3],hdr->addr3[4],hdr->addr3[5]
//	);
      /*if(Sniffed_packet.count == 5){
        P_printer(Sniffed_packet);
        P_free(&Sniffed_packet);
		Sniffed_packet = P_allocate(40);
        //exit(0);
      }*/

}

static int get_start_timestamp()
{
	int stime;
	time_t clk;

	time(&clk);
	stime = (int)clk - (int)clk % SNIFFING_TIME;

	return stime;
}

static void initialize_sntp()
{
    sntp_setoperatingmode(SNTP_OPMODE_POLL); //automatically request time after 1h
    //sntp_setservername(0, "pool.ntp.org");
	sntp_setservername(0, "ntp1.inrim.it");
    sntp_init();
    /* imposta l'ora legale*/
    setenv("TZ", "CET-1CEST-2,M3.5.0/02:00:00,M10.5.0/03:00:00", 1);
    tzset();
}

static int set_waiting_time()
{
	int st,sleep_time = 10;
	time_t t;

	time(&t);
	st = (sleep_time - (int)t % sleep_time) * 1000;
    /* clculatre how many seconds are left from de sleep time */
	return st;
}

static void obtain_time()
{
    time_t now = 0;
    struct tm timeinfo = { 0 };
    int attempt = 0;
    const int attempt_count = 15;

    initialize_sntp();

    //wait for time to be set
    while(timeinfo.tm_year < (2019 - 1900) && ++attempt < attempt_count) {
       printf("Waiting for system time to be set... (%d/%d)", attempt, attempt_count);
        vTaskDelay(2000 / portTICK_PERIOD_MS);
        time(&now);
        localtime_r(&now, &timeinfo);
    }

    if(attempt >= attempt_count){ //can't set time
    	if(FIRST) //if it is first time -> reboot: no reason to sniff with wrong time
    		reboot("no response from server after several time. impossible to set current time");
    }
    else{
		// print the actual time in Italy
		char buffer[100];
		printf("\nActual time in Italy:\n");
		localtime_r(&now, &timeinfo);
		strftime(buffer, sizeof(buffer), "%d/%m/%Y %H:%M:%S", &timeinfo);
		printf("%s\n", buffer);

    	FIRST = false;
    }

}

static void reboot(char *msg_err)
{
	int i;

	printf("%s\n", msg_err);
    for(i=3; i>=0; i--){
        printf("Restarting in %d seconds...\n", i);
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }
   printf("Restarting now");
    fflush(stdout);

    esp_restart();
}
