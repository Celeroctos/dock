/* 
 * File:   ASTM_E1381_INT.h
 * Author: ASmirnov
 *
 * Created on 20 ����� 2015 �., 16:01
 */

#ifndef ASTM_E1381_INT_H
#define	ASTM_E1381_INT_H

//typedef unsigned char       BYTE;
// ASTM specification base encoding.
char *ENCODING="latin-1";

//Start of header (������ ���������) #�������� � ������ ���������
const char STH=0x01;
//Start of text (������ ������) #�������� � ������ ���������
const char STX=0x02;
//End of text (����� ������) #�������� � ������ ���������
const char ETX=0x03;
//End of transmission (����� ��������) #�������� � ������ ���������
const char EOT=0x04;
//Inquiry (������) #�������� � ������ ���������
const char ENQ=0x05;
//Acknowledgment (�������������) #�������� � ������ ���������
const char ACK=0x06;
//Data link escape (����� �� ������ �������� ������) #�������� � ������ ���������
const char DLE=0x10;
//Negative acknowledgment (������������� �������������) #�������� � ������ ���������
const char NAK=0x15;
//Synchronous idle (���������� �������) #�������� � ������ ���������
const char SYN=0x16;
//End of transmission block (����� ����� ��������) #�������� � ������ ���������
const char ETB=0x17;
//Line feed (������� ������) #�������� � ������ ���������
const char LF=0x0A;
//Carriage Return (������� �������)
const char CR=0x0D;
const char CRLF[2]={'\0D', '\0A'};
//Device control 1 (���������� ����������� 1) #�������� � ������ ���������
const char DC1=0x11;
//Device control 2 (���������� ����������� 2) #�������� � ������ ���������
const char DC2=0x12;
//Device control 3 (���������� ����������� 3) #�������� � ������ ���������
const char DC3=0x13;
//Device control 4 (���������� ����������� 4) #�������� � ������ ���������
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
