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
	void Init();

	void Run();

	void SetOnReciveEvent(void(*onRecive)(String));

private:
	byte _pinSS;
	byte _pinRST;
	MFRC522 _mfrc522;  // Create MFRC522 instance

	void(*_onRecive)(String);
};

