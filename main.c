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

//tcp connection
#include "lwip/err.h"
#include "lwip/sockets.h"
#include "lwip/sys.h"
#include <lwip/netdb.h>

#define	LED_GPIO_PIN			GPIO_NUM_4
#define	WIFI_CHANNEL_MAX		(13)
#define	WIFI_CHANNEL_SWITCH_INTERVAL	(500)
#define NO_SSID "no ssid"

#define EXAMPLE_WIFI_SSID "Ntani"
#define EXAMPLE_WIFI_PASS "davidedavide"

static wifi_country_t wifi_country = {.cc="CN", .schan=1, .nchan=13, .policy=WIFI_COUNTRY_POLICY_AUTO};
static const char *TAG = "example"; //used to log functions
const int IPV4_GOTIP_BIT = BIT0;
const int IPV6_GOTIP_BIT = BIT1;
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
}reduced_info;

//dynamic data structure to contain sniffed packets
typedef struct P_array{
	reduced_info* array; /*array of  packets*/
	int count;    /* number of actual packet -> position is count-1 */
	int dim; /* dimension of array */
}P_array;

//connecting function
static void wait_for_ip();

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

void
app_main(void)
{
	uint8_t channel = 1;
    Sniffed_packet=P_allocate(2);
	ComputHashMD5();
	/* setup */

	wifi_sniffer_init();


	/* loop */
	while (true) {

		vTaskDelay(WIFI_CHANNEL_SWITCH_INTERVAL / portTICK_PERIOD_MS);
		wifi_sniffer_set_channel(channel);
		channel = (channel % WIFI_CHANNEL_MAX) + 1;
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
		printf(" MAC_SRC=%02x:%02x:%02x:%02x:%02x:%02x\n",
			x.mac_src[0], x.mac_src[1], x.mac_src[2],
			x.mac_src[3], x.mac_src[4], x.mac_src[5]);
	}
}

void ComputHashMD5() {
	
	struct mbedtls_md5_context contextMD5; //MD5 context structure. Data fields: Total, state, buffer
	
	const unsigned char* string = (const unsigned char*) "Testo di prova";
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

	printf("digest of the string: 'Testo di prova': \n");
	for (int i = 0; i < sizeof(data); i++)
		printf(" %02x", data[i]);
	printf("\n");
}

static esp_err_t event_handler(void *ctx, system_event_t *event)
{
	switch(event->event_id) {
    case SYSTEM_EVENT_STA_START:
		esp_wifi_connect();
		ESP_LOGI(TAG, "SYSTEM_EVENT_STA_START");
		break;
	case SYSTEM_EVENT_STA_CONNECTED:
		/* enable ipv6 */
		tcpip_adapter_create_ip6_linklocal(TCPIP_ADAPTER_IF_STA);
		break;
	case SYSTEM_EVENT_STA_GOT_IP:
		xEventGroupSetBits(wifi_event_group, IPV4_GOTIP_BIT);
		ESP_LOGI(TAG, "SYSTEM_EVENT_STA_GOT_IP");
		break;
	case SYSTEM_EVENT_STA_DISCONNECTED:
		/* This is a workaround as ESP32 WiFi libs don't currently auto-reassociate. */
		esp_wifi_connect();
		xEventGroupClearBits(wifi_event_group, IPV4_GOTIP_BIT);
		xEventGroupClearBits(wifi_event_group, IPV6_GOTIP_BIT);
		break;
	case SYSTEM_EVENT_AP_STA_GOT_IP6:
		xEventGroupSetBits(wifi_event_group, IPV6_GOTIP_BIT);
		ESP_LOGI(TAG, "SYSTEM_EVENT_STA_GOT_IP6");

		char *ip6 = ip6addr_ntoa(&event->event_info.got_ip6.ip6_info.ip);
		ESP_LOGI(TAG, "IPv6: %s", ip6);
	default:
		break;
	}
	return ESP_OK;
}

void
wifi_sniffer_init(void)
{

	nvs_flash_init();
    tcpip_adapter_init();
	
	wifi_event_group = xEventGroupCreate();

    ESP_ERROR_CHECK( esp_event_loop_init(event_handler, NULL) );
    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
	ESP_ERROR_CHECK( esp_wifi_init(&cfg) );
	ESP_ERROR_CHECK( esp_wifi_set_country(&wifi_country) ); /* set country for channel range [1, 13] */
	ESP_ERROR_CHECK( esp_wifi_set_storage(WIFI_STORAGE_RAM) );
    //ESP_ERROR_CHECK( esp_wifi_set_mode(WIFI_MODE_NULL) );
    //ESP_ERROR_CHECK( esp_wifi_start() );
	
	wifi_config_t wifi_config = {
		.sta = {
		.ssid = EXAMPLE_WIFI_SSID,
		.password = EXAMPLE_WIFI_PASS,
	},
	};
	
	ESP_LOGI(TAG, "Setting WiFi configuration SSID %s...", wifi_config.sta.ssid);
	ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
	ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));
	ESP_ERROR_CHECK(esp_wifi_start());

	esp_wifi_set_promiscuous(true);
	esp_wifi_set_promiscuous_rx_cb(&wifi_sniffer_packet_handler);

	wait_for_ip();
}

static void wait_for_ip()
{
	uint32_t bits = IPV4_GOTIP_BIT | IPV6_GOTIP_BIT;

	ESP_LOGI(TAG, "Waiting for AP connection...");
	xEventGroupWaitBits(wifi_event_group, bits, false, true, portMAX_DELAY);
	/*
	Read bits within an RTOS event group, optionally entering the Blocked state (with a timeout)
	to wait for a bit or group of bits to become set.
	*/
	ESP_LOGI(TAG, "Connected to AP");
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

	const wifi_promiscuous_pkt_t *ppkt = (wifi_promiscuous_pkt_t *)buff;
	const wifi_ieee80211_packet_t *ipkt = (wifi_ieee80211_packet_t *)ppkt->payload;
	const wifi_ieee80211_mac_hdr_t *hdr = &ipkt->hdr;
    reduced_info x;


	uint16_t frame = hdr->frame_ctrl;


    uint16_t a, b=64, mask=0xF0;
    a= frame & mask;
    if (a!=b)
    {
		return;
    }


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
      if(Sniffed_packet.count == 5){
        P_printer(Sniffed_packet);
        P_free(&Sniffed_packet);
		Sniffed_packet = P_allocate(40);
        //exit(0);
      }

}

