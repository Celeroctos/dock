/* 
 * File:   ASTM_E1381_INT.h
 * Author: ASmirnov
 *
 * Created on 20 марта 2015 г., 16:01
 */

#ifndef ASTM_E1381_INT_H
#define	ASTM_E1381_INT_H

//typedef unsigned char       BYTE;
// ASTM specification base encoding.
char *ENCODING="latin-1";

//Start of header (Ќачало заголовка) #запрещен в тексте сообщени€
const char STH=0x01;
//Start of text (Ќачало текста) #запрещен в тексте сообщени€
const char STX=0x02;
//End of text ( онец текста) #запрещен в тексте сообщени€
const char ETX=0x03;
//End of transmission ( онец передачи) #запрещен в тексте сообщени€
const char EOT=0x04;
//Inquiry («апрос) #запрещен в тексте сообщени€
const char ENQ=0x05;
//Acknowledgment (ѕодтверждение) #запрещен в тексте сообщени€
const char ACK=0x06;
//Data link escape (¬ыход из канала передачи данных) #запрещен в тексте сообщени€
const char DLE=0x10;
//Negative acknowledgment (ќтрицательное подтверждение) #запрещен в тексте сообщени€
const char NAK=0x15;
//Synchronous idle (—инхронный простой) #запрещен в тексте сообщени€
const char SYN=0x16;
//End of transmission block ( онец блока передачи) #запрещен в тексте сообщени€
const char ETB=0x17;
//Line feed (ѕеревод строки) #запрещен в тексте сообщени€
const char LF=0x0A;
//Carriage Return (¬озврат каретки)
const char CR=0x0D;
const char CRLF[2]={'\0D', '\0A'};
//Device control 1 (”правление устройством 1) #запрещен в тексте сообщени€
const char DC1=0x11;
//Device control 2 (”правление устройством 2) #запрещен в тексте сообщени€
const char DC2=0x12;
//Device control 3 (”правление устройством 3) #запрещен в тексте сообщени€
const char DC3=0x13;
//Device control 4 (”правление устройством 4) #запрещен в тексте сообщени€
const char DC4=0x14;
//Vertical Tab
//char VT=0x0B;
//File Separator
//char FS=0x1C;
//using namespace std;
//list<string> incoming_records, outgoing_records;



#define MAX_PACKET_SIZE		65535
char		buff [MAX_PACKET_SIZE];
#define FRAME_MAX_TEXT 240
char frame_buf[1 + 1 + FRAME_MAX_TEXT + 1 + 2 + 2 + 1];
int res;
int len;

enum Phase {Idle=0, Establishment, Transfer, Termination} phase;
SOCKET ConnectSocket;
int NAK_count;
int NAK_sleep;
int ENQ_sleep;

int Frame_Number;
#endif	/* ASTM_E1381_INT_H */
