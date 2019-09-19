/*
 Name:		HardwareControllSystem.ino
 Created:	9/13/2019 8:53:20 AM
 Author:	Pontus
*/

// the setup function runs once when you press reset or power the board

//#include "SerialComManager.h"
#include "RFID.h"

#define RFID_PIN_SS 10
#define RFID_PIN_RST 9

int first_LED = 8; 
int second_LED = 9;
int third_LED = 10;
int state = 0;

int buzzer_Pin = 6;

//SerialComManager serialManager(9600);
RFID rfid(RFID_PIN_SS, RFID_PIN_RST);


void OnRFID_Recive(String message)
{
	Serial.println(message);
	tone(buzzer_Pin, 2000, 250);
	if (message.substring(1) == "A6 53 9F F7")
	{
		Serial.println("Authorized access\n");
		delay(250);
		tone(buzzer_Pin, 2500, 250);
	}
	else
	{
		Serial.println(" Access denied\n");
		delay(250);
		tone(buzzer_Pin, 1500, 250);
	}
}

void setup() 
{
	Serial.begin(9600);
	rfid.Init();
	rfid.SetOnReciveEvent(OnRFID_Recive);
	Serial.println("---Ready---");
}

void loop()
{
	rfid.Run();

}