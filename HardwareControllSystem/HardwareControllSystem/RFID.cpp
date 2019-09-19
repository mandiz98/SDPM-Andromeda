#include "RFID.h"

#include <SPI.h>
#include <MFRC522.h>

RFID::RFID(byte pinSS, byte pinRST)
{
	_pinSS = pinSS;
	_pinRST = pinRST;

}

RFID::~RFID()
{
}

void RFID::Init()
{	
	_mfrc522 = MFRC522(_pinSS, _pinRST);
	SPI.begin();			// Init SPI bus
	_mfrc522.PCD_Init();	// Init MFRC522
}

String oldMessage = "";
int absentsCounter = 0;
void RFID::Run()
{
	if (!_mfrc522.PICC_IsNewCardPresent()  || !_mfrc522.PICC_ReadCardSerial())
	{
		absentsCounter++;
		if (absentsCounter > 2)
			oldMessage = "";
		return;
	}
	absentsCounter = 0;

	String message = "";
	for (byte i = 0; i < _mfrc522.uid.size; i++)
	{
		message.concat(String(_mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "));
		message.concat(String(_mfrc522.uid.uidByte[i], HEX));
	}
	message.toUpperCase();
	
	if (message == oldMessage)
		return;

	oldMessage = message;
	_onRecive(message);
}

void RFID::SetOnReciveEvent(void(*onRecive)(String))
{
	_onRecive = onRecive;
}
