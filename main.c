/**
 * Copyright (c) 2017, Łukasz Marcin Podkalicki <lpodkalicki@gmail.com>
 * ESP32/016
 * WiFi Sniffer.
 */

#include "freertos/FreeRTOS.h"
#include "esp_wifi.h"
#include "esp_wifi_types.h"
#include "esp_system.h"
#include "esp_event.h"
#include "esp_event_loop.h"
#include "nvs_flash.h"
#include "driver/gpio.h"
#include <string.h>

#define	LED_GPIO_PIN			GPIO_NUM_4
#define	WIFI_CHANNEL_MAX		(13)
#define	WIFI_CHANNEL_SWITCH_INTERVAL	(500)

static wifi_country_t wifi_country = {.cc="CN", .schan=1, .nchan=13, .policy=WIFI_COUNTRY_POLICY_AUTO};

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

//dynamic data structure to contain sniffed packets
typedef struct P_array{
	wifi_ieee80211_packet_t* array; /*array of  packets*/
	int count;    /* number of actual packet -> position is count-1 */
	int dim; /* dimension of array */
}P_array;


static esp_err_t event_handler(void *ctx, system_event_t *event);
static void wifi_sniffer_init(void);
static void wifi_sniffer_set_channel(uint8_t channel);
static const char *wifi_sniffer_packet_type2str(wifi_promiscuous_pkt_type_t type);
static void wifi_sniffer_packet_handler(void *buff, wifi_promiscuous_pkt_type_t type);

/*function to manage data storage*/
P_array P_allocate(int dim);
void P_push(P_array* sniffed_packet, wifi_ieee80211_packet_t x);
void P_free(P_array* sniffed_packet);
void P_resize(P_array* sniffed_packet);

P_array Sniffed_packet;

void
app_main(void)
{
	uint8_t level = 0, channel = 1;

	/* setup */

	wifi_sniffer_init();
	gpio_set_direction(LED_GPIO_PIN, GPIO_MODE_OUTPUT);

	/* loop */
	while (true) {
		gpio_set_level(LED_GPIO_PIN, level ^= 1);
		vTaskDelay(WIFI_CHANNEL_SWITCH_INTERVAL / portTICK_PERIOD_MS);
		wifi_sniffer_set_channel(channel);
		channel = (channel % WIFI_CHANNEL_MAX) + 1;
    	}
}

P_array P_allocate(int dim){

    dim = dim+1;
	P_array parray;
	parray.array = (wifi_ieee80211_packet_t* )malloc(dim*sizeof(wifi_ieee80211_packet_t));
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
        sniffed_packet->array=(wifi_ieee80211_packet_t *) realloc(sniffed_packet->array,new_dim*sizeof(wifi_ieee80211_packet_t));
        if(sniffed_packet->array == NULL){
            printf("No more space to realloc\n");
            exit(-1);
        }

        sniffed_packet->dim=new_dim;

}

void P_push(P_array* sniffed_packet, wifi_ieee80211_packet_t p){
  int n_packets = sniffed_packet->count;

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
    free(sniffed_packet);
}

esp_err_t
event_handler(void *ctx, system_event_t *event)
{

	return ESP_OK;
}

void
wifi_sniffer_init(void)
{

	nvs_flash_init();
    	tcpip_adapter_init();
    	ESP_ERROR_CHECK( esp_event_loop_init(event_handler, NULL) );
    	wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
	ESP_ERROR_CHECK( esp_wifi_init(&cfg) );
	ESP_ERROR_CHECK( esp_wifi_set_country(&wifi_country) ); /* set country for channel range [1, 13] */
	ESP_ERROR_CHECK( esp_wifi_set_storage(WIFI_STORAGE_RAM) );
    	ESP_ERROR_CHECK( esp_wifi_set_mode(WIFI_MODE_NULL) );
    	ESP_ERROR_CHECK( esp_wifi_start() );
	esp_wifi_set_promiscuous(true);
	esp_wifi_set_promiscuous_rx_cb(&wifi_sniffer_packet_handler);
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


	uint16_t frame = hdr->frame_ctrl;
	printf("frame = %x\n", frame);
	printf("frame_ctrl = %x\n", hdr->frame_ctrl);

    uint16_t a, b=64, mask=0xF0;
    a= frame & mask;
    if (a!=b)
    {
		return;
    }

	char *ssid, stringa[32];
	uint8_t *data=&ppkt->payload;/*new*/
	uint8_t len=data[25], i;
	ssid=(char *) malloc((len+1)*sizeof(char));
	for(i=0; i< len; i++){
		ssid[i]=data[26+i];
	}
	ssid[i]='\0';
	//ssid=strdup(stringa);

	printf("PACKET TYPE=%s, CHAN=%02d, RSSI=%02d, TIME=%d, SSID=%d %s"
		" ADDR1=%02x:%02x:%02x:%02x:%02x:%02x,"
		" ADDR2=%02x:%02x:%02x:%02x:%02x:%02x,"
		" ADDR3=%02x:%02x:%02x:%02x:%02x:%02x\n",
		wifi_sniffer_packet_type2str(type),
		ppkt->rx_ctrl.channel,
		ppkt->rx_ctrl.rssi,
		ppkt->rx_ctrl.timestamp,/*new*/
		len, ssid,/*new*/
		/* ADDR1 */
		hdr->addr1[0],hdr->addr1[1],hdr->addr1[2],
		hdr->addr1[3],hdr->addr1[4],hdr->addr1[5],
		/* ADDR2 */
		hdr->addr2[0],hdr->addr2[1],hdr->addr2[2],
		hdr->addr2[3],hdr->addr2[4],hdr->addr2[5],
		/* ADDR3 */
		hdr->addr3[0],hdr->addr3[1],hdr->addr3[2],
		hdr->addr3[3],hdr->addr3[4],hdr->addr3[5]
	);
}
