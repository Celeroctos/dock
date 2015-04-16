#include "stdafx.h"
#include <stdio.h>
#include <stdlib.h>

#if 0
#  include <libpq-fe.h>
#else
#  include "../include/libpq-fe.h"
#endif

#include "ASTM_E1384_INT.h"
#include "pg_lis.h"

using namespace std;

string ConnInfo = "host=localhost port=5432 dbname=postgres user=postgres password='12345'";
PGconn* conn;
PGresult *pg_res;

map<string,string> err_defs;

char err_buf[65365];

void read_err_defs() {
	char *s, *s1;

	FILE * pFile;
	char buffer [100];

	pFile = fopen ("error_codes.txt" , "r");
	if (pFile == NULL) perror ("Error opening file");
	else
	{
		while ( ! feof (pFile) )
		{
			if ( fgets (buffer , 100 , pFile) == NULL ) break;
			s = strtok(buffer, "\t");
			s1 = strtok(NULL, "\t");
			if (strcmp(s, "ErrorCode"))
				if (s1)
					err_defs[s]=s1;
				else
					err_defs[s]="";
		}
		fclose (pFile);
	}
}

static void
exit_nicely(PGconn *conn)
{
	PQfinish(conn);
	exit(1);
}

void db_connect() {
	conn = PQconnectdb(ConnInfo.c_str());
	if (PQstatus(conn) != CONNECTION_OK)
	{
		sprintf(err_buf, "Connection to database failed: %s",
			PQerrorMessage(conn));
		WriteLogFile(err_buf);
		fprintf(stderr, "%s", err_buf);
		exit_nicely(conn);
	}
}

int InsertAnalisisResult(struct Msg *Message) {
//	int res = 0;
	string lis_Patient_ID;
	string a_registration_date, as_sample_id, a_comment;
	int directon_barcode;
	struct Comment *C;
	struct Result *R;
	string err_code;
	size_t index;
	string err_str, err_def;
	const char *analisis_params[4];

	const char *result_params[8];

	string analisis_insert, result_analisis_insert;


	lis_Patient_ID = Message->Patient.LaboratoryAssignedPatientID;
	as_sample_id = Message->Ord.SpecimenIDAccessionNumber;
	a_registration_date = Message->Hdr.DateandTimeofMessage;
	C = Message->Comment_msg;
	while (C) {
		index = C->CommentText.find("^");
		if (index != string::npos ) {
			err_str = C->CommentText;
		}
		else
		{
			err_code = C->CommentText;  
			err_def = err_defs[err_code];
			if (err_def != "")
				err_str = C->CommentText + "^" + err_def;
		}
		a_comment.append(err_str + ";");
		C = C->next;
	}

	analisis_insert = "INSERT INTO LIS.ANALYSIS (registration_time, direction_id, analyzer_id) values(to_timestamp($1,'YYYYMMDDHH24MISS'), $2, $3) RETURNING id";
	analisis_params[0] = a_registration_date.c_str();
	analisis_params[1] = "1";
	analisis_params[2] = "1";
//	analisis_params[3] = "1";
	pg_res = PQexecParams(conn,
		analisis_insert.c_str(),
		3,       /* one param */
		NULL,    /* let the backend deduce param type */
		analisis_params,
		NULL,    /* don't need param lengths since text */
		NULL,    /* default to all text params */
		0);      /* ask for text results */

	if (PQresultStatus(pg_res) != PGRES_TUPLES_OK)
	{
		sprintf(err_buf, "INSERT INTO LIS.ANALYSIS failed: %s", PQerrorMessage(conn));
		WriteLogFile(err_buf);
		PQclear(pg_res);
		return 0;
	}
	else
	{
		as_sample_id = PQgetvalue(pg_res, 0,0);
				PQclear(pg_res);
	}

	R = Message->Res;
	while (R) {
		string seq_n = R->SequenceNumber;
		string TestID = R->UniversalTestID.substr(3);
		string Val = R->MeasurementValue;
		string Units = R->Units;
		string Flag = R->ResultAbnormalFlag;
		string Status = R->ResultStatus;

		string r_comment = "";
		C = R->C;
		while (C) {
			index = C->CommentText.find("^");
			if (index != string::npos ) {
				err_str = C->CommentText;
			}
			else
			{
				err_code = C->CommentText;  
				err_def = err_defs[err_code];
				if (err_def != "")
					err_str = C->CommentText + "^" + err_def;
			}
			r_comment.append(err_str + ";");
			C = C->next;
		}

#if 1
		result_analisis_insert = "INSERT INTO LIS.ANALYSIS_RESULTS (analysis_id, seq_number, testid, val, units, resultflag, resultstatus, comment) values($1, $2, $3, $4, $5, $6, $7, $8)";
		result_params[0] = as_sample_id.c_str();
		result_params[1] = seq_n.c_str();
		result_params[2] = TestID.c_str();
		result_params[3] = Val.c_str();
		result_params[4] = Units.c_str();
		result_params[5] = Flag.c_str();
		result_params[6] = Status.c_str();
		result_params[7] = r_comment.c_str();

		pg_res = PQexecParams(conn,
		result_analisis_insert.c_str(),
		8,       /* one param */
		NULL,    /* let the backend deduce param type */
		result_params,
		NULL,    /* don't need param lengths since text */
		NULL,    /* default to all text params */
		0);      /* ask for text results */
#else
		result_analisis_insert = "INSERT INTO LIS.ANALYSIS_RESULTS (analysis_id, seq_number, testid, val, units, resultflag, resultstatus, comment) values('"+as_sample_id+"','"+seq_n+"','"+TestID+"','"+Val+"','"+Units+"','"+Flag+"','"+Status+"','"+r_comment+"')";
		pg_res=PQexec(conn,result_analisis_insert.c_str());
#endif
	if (PQresultStatus(pg_res) != PGRES_COMMAND_OK)
	{
		sprintf(err_buf, "INSERT INTO LIS.ANALYSIS_RESULTS failed: %s", PQerrorMessage(conn));
		WriteLogFile(err_buf);
		PQclear(pg_res);
	}
		PQclear(pg_res);
		R = R->next;
	}



	return 1;	
};
