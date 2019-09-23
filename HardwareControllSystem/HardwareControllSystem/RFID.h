#pragma once

#include <stdlib.h>

#include <SPI.h>
#include <MFRC522.h>

using namespace std;
class RFID
{
public:
	RFID(byte pinSS,byte pinRST);
	~RFID();
	void init();

	//main machine for handeling rfid scan
	void run();

	//set a callback function that will be called in the event of a rfid scan.
	//NOTE: only one callback can de set at any given time, new callbacks will overide old ones
	void setOnReciveEvent(void(*onRecive)(String));

private:
	byte _pinSS;
	byte _pinRST;
	MFRC522 _mfrc522;  // Create MFRC522 instance

	void(*_onRecive)(String);
};