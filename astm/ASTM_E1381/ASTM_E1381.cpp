//#include "stdafx.h"
#include <string.h>
#include <time.h>
#include "winsock2.h"
#include "stdio.h"
#include "ASTM_E1381_INT.h"
#include "../ASTM_E1381_EXT.h"

int l_LogLevel;
char *l_LogFile;

char buf[1024];

void WriteLogFile_1381(const char* szString)
{
	if (l_LogLevel == 2) {
		struct tm *current;
		time_t now;
		char buf[100];

		time(&now);
		current = localtime(&now);

		strftime(buf,sizeof(buf), "%F %T", current);

		FILE* pFile = fopen(l_LogFile, "a");
		fprintf(pFile, "%s: 1381: %s\n",buf, szString);
		fclose(pFile);
	}
}

int CheckFrame(char * buff, char *message)
{	
	char frame_buf1[1 + FRAME_MAX_TEXT + 1];
	char buf[1 + FRAME_MAX_TEXT + 1 + 50];
	int sum = 0,  mod_sum;
	char s_mod[3], sum_f[3], *ind1;
	if ((ind1 = strstr(buff, CRLF)) == 0) return 0;
	memset(s_mod,0,sizeof(s_mod));
	memset(sum_f,0,sizeof(sum_f));
	strncpy(s_mod, ind1-2, 2);
	memset(frame_buf1,0,sizeof(frame_buf1));
	strncpy(frame_buf1, buff+1, ind1-buff-1-2);
	for (size_t i=0; i < strlen(frame_buf1); i++)
		sum += frame_buf1[i];
	mod_sum = sum%256;
	sprintf(sum_f, "%X", mod_sum);
	if (strcmp(s_mod, sum_f) == 0) {
		strncat(message, frame_buf1+1, (strlen(frame_buf1)-2));
		sprintf(buf, "Received correct frame: %s", frame_buf1);
		WriteLogFile_1381(buf);
		return 1;
	}
	else
	{
		sprintf(buf, "Received error frame: %s", frame_buf1);
		WriteLogFile_1381(buf);
	}
	return 0;
}

int FormFrame(char *frame, char *buff, int number, int last)
{
	char frame_buf1[1 + FRAME_MAX_TEXT + 1];
	int sum = 0,  mod_sum;
	char sum_f[3];
	if ((number < 0) || (number > 7)) return 0;
	if (strlen(buff) > FRAME_MAX_TEXT) return 0;
	memset(frame_buf1,0,sizeof(frame_buf1));
	sprintf(frame_buf1, "%1d%s%c", number, buff, last ? ETX : ETB); 
	for (size_t i=0; i < strlen(frame_buf1); i++)
		sum += frame_buf1[i];
	mod_sum = sum%256;
	sprintf(sum_f, "%X", mod_sum);
	sprintf(frame, "%c%s%s%s", STX, frame_buf1, sum_f, CRLF);
	return 1;
}

int ASTM_E1381_init(char *IP, int Port, int LogLevel, char *LogFile) {
	l_LogFile = LogFile;
	l_LogLevel = LogLevel;
	WSADATA wsaData;
	res = WSAStartup(MAKEWORD(2,2), &wsaData);
	ConnectSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (ConnectSocket == INVALID_SOCKET) {
		    sprintf(buf, "Error at socket(): %ld\n", WSAGetLastError());
				WriteLogFile_1381(buf);
		WSACleanup();
		return 0;
	}
	//----------------------
	// The sockaddr_in structure specifies the address family,
	// IP address, and port of the server to be connected to.
	sockaddr_in clientService; 
	clientService.sin_family = AF_INET;
	clientService.sin_addr.s_addr = inet_addr(IP);
	clientService.sin_port = htons(Port);

	//----------------------
	// Connect to server.
	if ( connect( ConnectSocket, (SOCKADDR*) &clientService, sizeof(clientService) ) == SOCKET_ERROR) {
		printf( "Failed to connect.\n" );
		WSACleanup();
		return 0;
	}
	phase = Idle;
	return 1;
}

int StartDataTransfer() {

	if (phase != Idle) return 0;
	phase = Establishment;
	WriteLogFile_1381("Send ENQ");
	send(ConnectSocket, &ENQ, 1, 0);
	fd_set read_s; // Множество
	timeval time_out; // Таймаут

	FD_ZERO (&read_s); // Обнуляем мнодество
	FD_SET (ConnectSocket, &read_s); // Заносим в него наш сокет 
	do {
		time_out.tv_sec = 5;time_out.tv_usec = 0; //Таймаут 5 секунды.
		if (ENQ_sleep > 0) time_out.tv_sec = ENQ_sleep;
		if (SOCKET_ERROR == (res = select (0, &read_s, NULL, NULL, &time_out) ) ) return -1;

		if ((res!=0) && (FD_ISSET (ConnectSocket, &read_s)) ) // Использую FD_ISSET только для примера! :)
		{
			// Получаю данные
			if (SOCKET_ERROR == (len = recv (ConnectSocket, (char *) &buff, 1, 0) ) )
			{
				res = WSAGetLastError ();
				return -1;
			}
			switch (buff [0]) {
			case ENQ:
				WriteLogFile_1381("Received ENQ");
				ENQ_sleep = 20;
				NAK_sleep = 0;
				break;
			case ACK:
				WriteLogFile_1381("Received ACK");
				phase = Transfer;
				Frame_Number = 1;
				ENQ_sleep = 0;
				NAK_sleep = 0;
				break;
			case NAK:
				WriteLogFile_1381("Received NAK");
				ENQ_sleep = 0;
				if ((NAK_sleep == 10) && (phase == Establishment))
				{
					NAK_sleep = 0;
					WriteLogFile_1381("Send EOT");
					send(ConnectSocket, &EOT, 1, 0);
					phase = Idle;
				};
				NAK_sleep = 10;
				Sleep(NAK_sleep*1000);
				WriteLogFile_1381("Send ENQ");
				send(ConnectSocket, &ENQ, 1, 0);
				break;
			default:
				WriteLogFile_1381("Received Unknown");
				ENQ_sleep = 0;
				NAK_sleep = 0;
				phase = Idle;
				break;
			}
		}
	} while ((ENQ_sleep > 0) || (NAK_sleep > 0));
	return 1;
}

int SendRecord(char *Rec, int end_message) {
	char *b_index = NULL, *frame = NULL;
	char buf[FRAME_MAX_TEXT+1];
	int res;

	b_index = Rec;
	do {
		memset(buf, 0, sizeof(buf));
		strncpy(buf, b_index, (strlen(b_index) >= FRAME_MAX_TEXT) ? FRAME_MAX_TEXT : strlen(b_index));
		for (int i = 0; (i<7) && (res=0); i++) {
			res = FormFrame(frame, buf, Frame_Number, (strlen(b_index) >= FRAME_MAX_TEXT) ? 0 : 1);
			sprintf(buf, "Send frame: %s", frame);
			WriteLogFile_1381(buf);
			send(ConnectSocket, frame, strlen(frame), 0);
			fd_set read_s; // Множество
			timeval time_out; // Таймаут

			FD_ZERO (&read_s); // Обнуляем множество
			FD_SET (ConnectSocket, &read_s); // Заносим в него наш сокет 
			time_out.tv_sec = 15;time_out.tv_usec = 0; //Таймаут 15 секунды.
			if (SOCKET_ERROR == (res = select (0, &read_s, NULL, NULL, &time_out) ) ) return 0;
			if (SOCKET_ERROR == (len = recv (ConnectSocket, (char *) &buff, 1, 0) ) )
			{
				res = WSAGetLastError ();
				return 0;
			}
			switch (buff [0]) {
				case ACK:
					WriteLogFile_1381("Received ACK");
					Frame_Number++;
					if (Frame_Number > 7) Frame_Number=0;
					res = 1;
					break;
				case NAK:
					WriteLogFile_1381("Received NAK");
					res = 0;
					break;
				case EOT:
					phase = Idle;
					return 1;
			}
		}
		if (res==0)
			return 0;
		b_index += (strlen(b_index) >= FRAME_MAX_TEXT) ? FRAME_MAX_TEXT : strlen(b_index);
	} while (strlen(b_index) >= FRAME_MAX_TEXT);
	return 1;
}

int ASTM_E1381_SendData(char *message, int end_message) {
	char *b_index, *e_index;
	char *Rec;
	int len;
	int res;
	if ((phase != Idle) && (phase != Transfer)) return 0;
	if (phase == Idle)
		if (StartDataTransfer() == 0) return 0;
	b_index = message;
	do {
		e_index = strstr(b_index, "\r");

		if (e_index == NULL) {
			if ((len = strlen(b_index)) > 0)
			{
				Rec = (char*)calloc(1, len+1);
				strncpy(Rec, b_index, len);
				res = SendRecord(Rec, end_message);
				free(Rec);
				if (res == 0) return 0;
			}
		}
		else //e_index != NULL
		{
			Rec = (char*)calloc(1, e_index-b_index+2);
			strncpy(Rec, b_index, e_index-b_index+1);
			res = SendRecord(Rec, end_message);
			if (res == 0) return 0;
			free(Rec);
		}
		b_index = e_index+1;
	} while (e_index == NULL);


	return 1;
}
int ASTM_E1381_ReceiveData(char *message) {
#ifndef TEST
	switch (phase) {
		case Establishment:
		case Transfer:
		case Termination:
			return 0;
	}
	fd_set read_s; // Множество
	timeval time_out; // Таймаут

	FD_ZERO (&read_s); // Обнуляем мнодество
	FD_SET (ConnectSocket, &read_s); // Заносим в него наш сокет 
	time_out.tv_sec = 0;time_out.tv_usec = 500000; //Таймаут 0.5 секунды.
	if (SOCKET_ERROR == (res = select (0, &read_s, NULL, NULL, &time_out) ) ) return 0;
	if ((res!=0) && (FD_ISSET (ConnectSocket, &read_s)) ) // Использую FD_ISSET только для примера! :)
	{
		// Получаю данные
		if (SOCKET_ERROR == (len = recv (ConnectSocket, (char *) &buff, 1, 0) ) )
		{
			res = WSAGetLastError ();
			return -1;
		}
		switch (buff [0]) {
	case ENQ:
		WriteLogFile_1381("Received ENQ");
		WriteLogFile_1381("Send ACK");
		send(ConnectSocket, &ACK, 1, 0);
		phase = Transfer;
		break;
	default:
		WriteLogFile_1381("Received Unknown");
		WriteLogFile_1381("Send NAK");
		send(ConnectSocket, &NAK, 1, 0);
		return 0;
		break;
		};
	}
	NAK_count = 0;
	do
	{
		FD_ZERO (&read_s);	 // Обнуляем мнодество
		time_out.tv_sec = 30;time_out.tv_usec = 0; //Таймаут 30 секунды.
		FD_SET (ConnectSocket, &read_s); // Заносим в него наш сокет 
		if (SOCKET_ERROR == (res = select (0, &read_s, NULL, NULL, &time_out) ) )
			return 0;

		if ((res!=0) && (FD_ISSET (ConnectSocket, &read_s)) ) // Я использую здесь FD_ISSET только для примера! :)
		{
			// Данные готовы к чтению...
			if (SOCKET_ERROR == (len = recv (ConnectSocket, (char *) &buff, MAX_PACKET_SIZE, 0) ) )
			{
				int res = WSAGetLastError ();
				return 0;
			}
			switch (buff[0]) {
	case STX:
		WriteLogFile_1381("Received STX");
		if (CheckFrame(buff, message)) {
			NAK_count = 0;
			WriteLogFile_1381("Send ACK");
			send(ConnectSocket, &ACK, 1, 0);
		}
		else
		{
			if (NAK_count == 0) NAK_count = 7;
			WriteLogFile_1381("Send NAK");
			send(ConnectSocket, &NAK, 1, 0);
			if (NAK_count-- == 0) {
				*message = 0;
				phase = Idle;
				return 0;
			}
		}
		break;
	case EOT:
		WriteLogFile_1381("Received EOT");
		phase = Idle;
		return 1;
		break;
	default:
		break;
			}
		}
	}
	while (phase == Transfer);
	return 0;
#else
	sprintf(message, "H|\\^&|||ABL735^Central Lab.||||||||1|20020719164827\r\
P|1||0004||Sørensen^Susanne||19460123|F||||||^|^^^|1.37^m|55.00^kg||||||||\r\
O|1||Sample #^267|^^^|||||||||||Not specified^||||||||||C\r\
R|1|^^^pH^M|?7.412|||N||R|||20020719151122\r\
C|1|I|377^Calibration Drift 2 out of range|I\r\
R|2|^^^pH(T)^C|?7.377|||N||C|||\r\
C|1|L|CHANGE^2002-07-19 16:44:02 () pH(T): 7.412 -> 7.377|G\r\
R|3|^^^p50(act),T^E|?4.12|kPa||N||C|||\r\
C|1|L|CHANGE^2002-07-19 16:44:02 () p50(act),T: 3.47 -> 4.12|G\r\
R|4|^^^p50(act)^E|?3.47|kPa||N||R|||\r\
R|5|^^^pCO2^M|5.53|kPa||N||R|||\r\
R|6|^^^pCO2(T)^C|6.21|kPa||N||C|||\r\
C|1|L|CHANGE^2002-07-19 16:44:02 () pCO2(T): 5.53 -> 6.21|G\r\
R|7|^^^pO2^M|11.5|kPa||N||R|||\r\
R|8|^^^pO2(T)^C|?13.3|kPa||N||C|||\r\
C|1|L|CHANGE^2002-07-19 16:44:02 () pO2(T): 11.5 -> 13.3|G\r\
R|9|^^^SBE^C|?1.7|mmol/L||N||R|||\r\
R|10|^^^ABE^C|?1.6|mmol/L||N||R|||\r\
R|11|^^^Ca++^M|1.21|mmol/L||N||R|||\r\
R|12|^^^Ca(7.4)^C|?1.22|mmol/L||N||R|||\r\
R|13|^^^Cl-^M|110|mmol/L||N||R|||\r\
R|14|^^^Glu^M|8.0|mmol/L||N||R|||\r\
R|15|^^^cH+^C|?38.7|nmol/L||N||R|||\r\
R|16|^^^HCO3-^C|?25.9|mmol/L||N||R|||\r\
R|17|^^^SBC^C|?25.8|mmol/L||N||R|||\r\
R|18|^^^K+^M|3.6|mmol/L||N||R|||\r\
R|19|^^^cH+(T)^C|?42.0|nmol/L||N||C|||\r\
C|1|L|CHANGE^2002-07-19 16:44:02 () cH+(T): 38.7 -> 42.0|G\r\
R|20|^^^Lac^M|0.6|mmol/L||N||R|||\r\
R|21|^^^Na+^M|142|mmol/L||N||R|||\r\
R|22|^^^tCO2(B)^C|?50.2|Vol%||N||R|||\r\
R|23|^^^tHb^M|9.4|mmol/L||N||R|||\r\
R|24|^^^sO2^M|?0.973|||N||R|||\r\
C|1|I|377^Calibration Drift 2 out of range|I\r\
R|25|^^^COHb^M|0.013|||N||R|||\r\
R|26|^^^RHb^M|0.027|||N||R|||\r\
R|27|^^^MetHb^M|0.005|||N||R|||\r\
R|28|^^^T^I|39.4|Cel||N||C|||\r\
C|1|L|CHANGE^2002-07-19 16:44:02 () T: 37.0 -> 39.4|G\r\
R|29|^^^FIO2^I|0.800|||N||C|||\r\
C|2|L|CHANGE^2002-07-19 16:43:36 () FIO2: 0.210 -> 0.800|G\r\
L|1|N\r");
	return 1;
#endif
}
