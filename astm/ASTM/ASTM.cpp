// ASTM.cpp: определяет точку входа для консольного приложения.
//

#include "stdafx.h"
#include "ASTM_E1384_INT.h"
#include "pg_lis.h"

//Message records delimiter.
char RECORD_SEP = 0x0D; // <CR>
//Record fields delimiter.
char FIELD_SEP = 0x7C; // '|'
//Delimeter for repeated fields.
char REPEAT_SEP = 0x5C; // '\'
//Field components delimiter.
char COMPONENT_SEP = 0x5E; // '^'
//Date escape token.
char ESCAPE_SEP = 0x26; // '&'

// Date format
//char *date_format = "%Y%m%d";
// Time format
//char *time_format = "%H%M%S";
// Full date and time format
//char *date_time_format = "%Y%m%d%H%M%S";

void db_connect();
int LogLevel = 1;
string LogFileName = "ASTM.log";

char message[65365];
char answer[65365];

void WriteLogFile(const char* szString)
{
	if (LogLevel >= 1) {
		struct tm *current;
		time_t now;
		char buf[100];

		time(&now);
		current = localtime(&now);

		strftime(buf,sizeof(buf), "%Y-%m-%d %H:%M:%S", current);

		FILE* pFile = fopen(LogFileName.c_str(), "a");
		fprintf(pFile, "%s: 1384: %s\n",buf, szString);
		fclose(pFile);
	}
}

void ParseHeader(char *req, struct Header *Hdr) {
	char *p_field, *n_field;
	char field[1024];
	p_field = req+1;
	FIELD_SEP = req[1];
	n_field = strchr(req+2,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->DelimiterDefinition = field; 
			return;
		};
	};
	memset(field,0,sizeof(field));
	strncpy(field, p_field, n_field-p_field) ;
	Hdr->DelimiterDefinition = field;
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->MessageControlID = field; 
			return;
		};
	};
	if (((n_field-p_field) > 0)) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->MessageControlID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->AccessPassword = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->AccessPassword = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->SenderName = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->SenderName = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->SenderStreetAddress = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->SenderStreetAddress = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->MessageControlID = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->Reserved = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->SenderTelephoneNumber = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->SenderTelephoneNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->CharacteristicsofSender = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->CharacteristicsofSender = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->ReceiverID = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->ReceiverID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->CommentsorSpecialInstructions = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->CommentsorSpecialInstructions = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->ProcessingID = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->ProcessingID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->VersionNumber = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->VersionNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Hdr->DateandTimeofMessage = field; 
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Hdr->DateandTimeofMessage = field;
	}
}

void ParsePatient(char *req, struct Patient *P) {
	char *p_field, *n_field;
	char field[1024];
	p_field = req+2;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->SequenceNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->SequenceNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PracticeAssignedPatientID = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PracticeAssignedPatientID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->LaboratoryAssignedPatientID = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->LaboratoryAssignedPatientID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PatientIDNo3 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PatientIDNo3 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PatientName = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PatientName = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->MothersMaidenName = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->MothersMaidenName = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->DateofBirth = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->DateofBirth = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PatientSex = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PatientSex = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PatientRace = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PatientRace = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PatientAddress = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PatientAddress = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->Reserved = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->Reserved = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PatientTelephoneNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PatientTelephoneNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->AttendingPhysicianID = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->AttendingPhysicianID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->SpecialField1 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->SpecialField1 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->SpecialField2 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->SpecialField2 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PatientHeight = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PatientHeight = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PatientWeight = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PatientWeight = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->Diagnosis = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->Diagnosis = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->Medication = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->Medication = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->Diet = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->Diet = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PracticeFieldNo1 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PracticeFieldNo1 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->PracticeFieldNo2 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->PracticeFieldNo2 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->AdmissionDate = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->AdmissionDate = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->AdmissionStatus = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->AdmissionStatus = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			P->Location = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		P->Location = field;
	}
}

void ParseOrder(char *req, struct Order *Ord) {
	char *p_field, *n_field;
	char field[1024];
	p_field = req+2;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->SequenceNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->SequenceNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->SpecimenIDAccessionNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->SpecimenIDAccessionNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->InstrumentSpecimenIDsamplenumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->InstrumentSpecimenIDsamplenumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->UniversalTestID = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->UniversalTestID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->PriorityCode = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->PriorityCode = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->OrderedDateTime = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->OrderedDateTime = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->SampleDrawTime = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->SampleDrawTime = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->CollectionEndTime = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->CollectionEndTime = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->CollectionVolume = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->CollectionVolume = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->CollectorID = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->CollectorID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->ActionCode = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->ActionCode = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->DangerCode = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->DangerCode = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->RelevantClinicalInformation = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->RelevantClinicalInformation = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->DateTimeSpecimenReceived = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->DateTimeSpecimenReceived = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->SpecimenDescriptor = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->SpecimenDescriptor = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->OrderingPhysician = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->OrderingPhysician = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->PhysiciansTelephoneNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->PhysiciansTelephoneNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->UserfieldNo1 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->UserfieldNo1 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->UserfieldNo2 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->UserfieldNo2 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->LaboratoryFieldNo1 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->LaboratoryFieldNo1 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->LaboratoryFieldNo2 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->LaboratoryFieldNo2 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->DateTimeResultreportedorLastModified = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->DateTimeResultreportedorLastModified = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->InstrumentChargetoComputerSystem = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->InstrumentChargetoComputerSystem = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->InstrumentSectionID = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->InstrumentSectionID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Ord->ReportTypes = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Ord->ReportTypes = field;
	}
}

void ParseResult(char *req, struct Result *R) {
	char *p_field, *n_field;
	char field[1024];
	p_field = req+2;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->SequenceNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->SequenceNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->UniversalTestID = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->UniversalTestID = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->MeasurementValue = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->MeasurementValue = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->Units = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->Units = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->ReferenceRanges = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->ReferenceRanges = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->ResultAbnormalFlag = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->ResultAbnormalFlag = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->NatureofAbnormalityTesting = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->NatureofAbnormalityTesting = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->ResultStatus = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->ResultStatus = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->DateofChangeinInstrumentNormativeValuesorUnits = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->DateofChangeinInstrumentNormativeValuesorUnits = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->OperatorIdentification = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->OperatorIdentification = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->DateTimeTestStarted = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->DateTimeTestStarted = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			R->Datetimetestcompleted = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		R->Datetimetestcompleted = field;
	}
}

void ParseQuery(char *req, struct Query *Qry) {
	char *p_field, *n_field;
	char field[1024];
	p_field = req+2;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->SequenceNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->SequenceNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->QueryText = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->QueryText = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->Reserved4 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->Reserved4 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->Reserved5 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->Reserved5 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->Reserved6 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->Reserved6 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->Reserved7 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->Reserved7 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->Reserved8 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->Reserved8 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->Reserved9 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->Reserved9 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->Reserved10 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->Reserved10 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->QuerytextforPatientbyDepartmentquery = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->QuerytextforPatientbyDepartmentquery = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->Reserved12 = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->Reserved12 = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Qry->RequestInformationStatusCode = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Qry->RequestInformationStatusCode = field;
	}
}

void ParseComment(char *req, struct Comment *С) {
	char *p_field, *n_field;
	char field[1024];
	p_field = req+2;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->SequenceNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->SequenceNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->CommentSource = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->CommentSource = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->CommentText = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->CommentText = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->CommentType = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->CommentType = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->ReferenceRanges = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->ReferenceRanges = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->ResultAbnormalFlag = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->ResultAbnormalFlag = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->NatureofAbnormalityTesting = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->NatureofAbnormalityTesting = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->ResultStatus = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->ResultStatus = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->DateofChangeinInstrumentNormativeValuesorUnits = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->DateofChangeinInstrumentNormativeValuesorUnits = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->OperatorIdentification = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->OperatorIdentification = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->DateTimeTestStarted = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->DateTimeTestStarted = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			С->Datetimetestcompleted = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		С->Datetimetestcompleted = field;
	}
}

void ParseTerminator(char *req, struct Terminator *Term) {
	char *p_field, *n_field;
	char field[1024];
	p_field = req+2;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Term->SequenceNumber = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Term->SequenceNumber = field;
	}
	p_field = n_field+1;
	n_field = strchr(p_field,FIELD_SEP);
	if (n_field == NULL) {
		if (strlen(p_field) == 0) return; 
		else { 
			memset(field,0,sizeof(field)); 
			strcpy(field, p_field);
			Term->TerminationCode = field;
			return;
		};
	};
	if ((n_field-p_field) > 0) {
		memset(field,0,sizeof(field));
		strncpy(field, p_field, n_field-p_field) ;
		Term->TerminationCode = field;
	}
}
int ParseMessage(struct Msg *Message, char *message/*, char *answer*/)
{
	int res = 0;
	char *msg = message;
	char *req = NULL;
	char *pch = strchr (msg,RECORD_SEP); 
	struct Result *pR, *R = NULL;
	struct Comment *pC, *C = NULL;

	while ((pch != NULL) || (strlen(msg)))
	{
		if (pch != NULL) {
			req = (char*)realloc(req, pch-msg+1);
			memset(req, 0, pch-msg+1);
			strncpy(req, msg, pch-msg);
		}
		else
		{
			req = (char*)realloc(req, strlen(msg)+1);
			memset(req, 0, strlen(msg)+1);
			strcpy(req, msg);
		}

		switch (*req) {
	case 'H': // Header record
		ParseHeader(req, &(Message->Hdr));
		break;
	case 'P': // Patient information record
		ParsePatient(req, &(Message->Patient));
		break;
	case 'O': // Test order record
		ParseOrder(req, &(Message->Ord));
		break;
	case 'R': // Result record
		C = NULL;
		if (R == NULL) // First result
		{
			R = new Result;
			R->next = NULL;
			R->C = NULL;
			Message->Res = R;
		}
		else // Next result
		{
			pR = R;
			R = new Result;
			R->next = NULL;
			R->C = NULL;
			pR->next = R;
		}
		ParseResult(req, R);
		break;
	case 'C': // Comment record
		if (R == NULL) // Comment for Message
		{ 
			if (C != NULL) 
			{
				pC = C;
				C = new Comment;
				C->next = NULL;
				pC->next = C;
			}
			ParseComment(req, C);
		}
		else // Comment for Result
		{
			if (C == NULL)
			{
				C = new Comment;
				C->next = NULL;
				R->C = C;
			}
			else
			{
				pC = C;
				C = new Comment;
				C->next = NULL;
				pC->next = C;
			}
			ParseComment(req, C);
		}
		break;
		//		  case 'M': // Manufacturer information record
		//			  break;
	case 'Q': // Query record
		ParseQuery(req, &(Message->Qry));
		break;
	case 'L': // Message terminator record
		ParseTerminator(req, &(Message->Term));
		res = 1;
		break;
	default:
		break;
		}
		msg = pch+1;
		pch = strchr (msg,RECORD_SEP);
	}
	free(req);
	return res;
}


///////////////////////////////////////////////////////////////
static void Usage(const char *pProgName)
{ 
	fprintf(stderr, "Usage:\n");
	fprintf(stderr, "    ASTM [options] <IP-address> <port>\n");
	fprintf(stderr, "\n");
	fprintf(stderr, "Where:\n");
	fprintf(stderr, " <IP-address>          - IP-address of Analyser server.\n");
	fprintf(stderr, " <port>                - Port of Analyser server.\n");
	fprintf(stderr, "Options:\n");
	fprintf(stderr, " -ll <0-2>             - log level. Default - 1.\n");
	fprintf(stderr, "                          0 - nolog. \n");
	fprintf(stderr, "                          1 - log only ASTM_E1284 protocol event's.\n");
	fprintf(stderr, "                          2 - log ASTM_E1284 and ASTM_E1281 protocol event's.\n");
	fprintf(stderr, " -lf <file name>       - use <file name> for log event's.\n");
	fprintf(stderr, "                          Default - 'ASTM.log'\n");
	fprintf(stderr, " -db <connect info>    - use <connect info> for database connect string.\n");
	fprintf(stderr, "                          Use \" for multiword string.\n");
	fprintf(stderr, " -h                    - show this help.\n");
	fprintf(stderr, "\n");
	exit(1);
}


int main(int argc, char* argv[])
{
	struct Msg Message;
	char buf[65365];
	char **pArgs = &argv[1];
	int res;
	while (argc > 1) {
		if (**pArgs != '-')
			break;

		if (!strcmp(*pArgs, "-h")) {
			Usage(argv[0]);
		} else if (!strcmp(*pArgs, "-ll")) {
			pArgs++;
			argc--;
			LogLevel = atoi(*pArgs);
			pArgs++;
			argc--;
		} else if (!strcmp(*pArgs, "-lf")) {
			pArgs++;
			argc--;
			LogFileName = *pArgs;
			pArgs++;
			argc--;
		} else if (!strcmp(*pArgs, "-db")) {
			pArgs++;
			argc--;
			ConnInfo = *pArgs;
			pArgs++;
			argc--;
		}
		else {
			Usage(argv[0]);
		}
	}
	read_err_defs();
#ifndef TEST
	if ((LogLevel < 0) || (LogLevel > 2) || (LogFileName == "") || (argc != 3)) Usage(argv[0]);

	res = ASTM_E1381_init(pArgs[0], atoi(pArgs[1]), LogLevel, (char*)LogFileName.c_str());
	if (res=0) return -1;

	do {
#endif
		db_connect();
		res = ASTM_E1381_ReceiveData(message);
		if (res=1) {
			sprintf(buf, "Received message: %s", message);
			WriteLogFile(buf);
			res = ParseMessage(&Message, message/*, answer*/);
			if (res = 1) {
				if (Message.Ord.InstrumentSpecimenIDsamplenumber.find("Sample") != string::npos) // patient sample.
				{
					res = InsertAnalisisResult(&Message);
				}
				else if (Message.Ord.InstrumentSpecimenIDsamplenumber.find("Cal") != string::npos) // calibration.
				{
				}
				else if (Message.Ord.InstrumentSpecimenIDsamplenumber.find("QC") != string::npos) // quality control.
				{
				}
				else if (atoi(Message.Qry.SequenceNumber.c_str()) > 0) // Query record.
				{
				};
			}


			/*			if (res=2) {
			sprintf(buf, "Send answer: %s", answer);
			WriteLogFile(buf);
			res = ASTM_E1381_SendData(answer, 1); 
			if(res==1) WriteLogFile("The answer is successfully sent");
			else
			WriteLogFile("Error when sending the Answer");
			}
			*/
		}
#ifndef TEST
	} while (1);
#endif

	return 0;
}

