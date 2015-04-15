#include "stdafx.h"
/* 
 * File:   ASTM_E1284.h
 * Author: ASmirnov
 *
 * Created on 20 марта 2015 г., 16:35
 */

#ifndef ASTM_E1284_INT_H
#define	ASTM_E1284_INT_H


using namespace std;

struct Header {
	string DelimiterDefinition;
	string MessageControlID;
	string AccessPassword;
	string SenderName;
	string SenderStreetAddress;
	string Reserved;
	string SenderTelephoneNumber;
	string CharacteristicsofSender;
	string ReceiverID;
	string CommentsorSpecialInstructions;
	string ProcessingID;
	string VersionNumber;
	string DateandTimeofMessage;
};

struct Patient {
	struct Patient *next;
	string SequenceNumber;
	string PracticeAssignedPatientID;
	string LaboratoryAssignedPatientID;
	string PatientIDNo3;
	string PatientName;
	string MothersMaidenName;
	string DateofBirth;
	string PatientSex;
	string PatientRace;
	string PatientAddress;
	string Reserved;
	string PatientTelephoneNumber;
	string AttendingPhysicianID;
	string SpecialField1;
	string SpecialField2;
	string PatientHeight;
	string PatientWeight;
	string Diagnosis;
	string Medication;
	string Diet;
	string PracticeFieldNo1;
	string PracticeFieldNo2;
	string AdmissionDate;
	string AdmissionStatus;
	string Location;
};

struct Order {
	string SequenceNumber;
	string SpecimenIDAccessionNumber;
	string InstrumentSpecimenIDsamplenumber;
	string UniversalTestID;
	string PriorityCode;
	string OrderedDateTime;
	string SampleDrawTime;
	string CollectionEndTime;
	string CollectionVolume;
	string CollectorID;
	string ActionCode;
	string DangerCode;
	string RelevantClinicalInformation;
	string DateTimeSpecimenReceived;
	string SpecimenDescriptor;
	string OrderingPhysician;
	string PhysiciansTelephoneNumber;
	string UserfieldNo1;
	string UserfieldNo2;
	string LaboratoryFieldNo1;
	string LaboratoryFieldNo2;
	string DateTimeResultreportedorLastModified;
	string InstrumentChargetoComputerSystem;
	string InstrumentSectionID;
	string ReportTypes;
};

struct Comment {
	struct Comment *next;
	string SequenceNumber;
	string CommentSource;
	string CommentText;
	string CommentType;
	string ReferenceRanges;
	string ResultAbnormalFlag;
	string NatureofAbnormalityTesting;
	string ResultStatus;
	string DateofChangeinInstrumentNormativeValuesorUnits;
	string OperatorIdentification;
	string DateTimeTestStarted;
	string Datetimetestcompleted;
};

struct Result {
	struct Result *next;
	struct Comment *C;
	string SequenceNumber;
	string UniversalTestID;
	string MeasurementValue;
	string Units;
	string ReferenceRanges;
	string ResultAbnormalFlag;
	string NatureofAbnormalityTesting;
	string ResultStatus;
	string DateofChangeinInstrumentNormativeValuesorUnits;
	string OperatorIdentification;
	string DateTimeTestStarted;
	string Datetimetestcompleted;
};


struct Query {
	string SequenceNumber;
	string QueryText;
	string Reserved4;
	string Reserved5;
	string Reserved6;
	string Reserved7;
	string Reserved8;
	string Reserved9;
	string Reserved10;
	string QuerytextforPatientbyDepartmentquery;
	string Reserved12;
	string RequestInformationStatusCode;

};

struct Terminator {
	string SequenceNumber;
	string TerminationCode;
};

struct Msg {
	struct Header Hdr;
	struct Patient Patient;
	struct Order Ord;
	struct Comment *Comment_msg;
	struct Result *Res;
	struct Query Qry;
	struct Terminator Term;
};

void WriteLogFile(const char* szString);

extern string ConnInfo;
extern string LogFileName;

#endif	/* ASTM_E1284_INT_H */

