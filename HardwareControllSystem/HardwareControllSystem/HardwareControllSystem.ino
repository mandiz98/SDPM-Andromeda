/*
 Name:		HardwareControllSystem.ino
 Created:	9/13/2019 8:53:20 AM
 Author:	Pontus
*/

// the setup function runs once when you press reset or power the board

//#include "SerialComManager.h"
#include "RFID.h"
#include "BluetoothInterface.h"

#define RFID_PIN_SS 10
#define RFID_PIN_RST 9

int first_LED = 8; 
int second_LED = 9;
int third_LED = 10;
int state = 0;

int buzzer_Pin = 6;
RFID rfid(RFID_PIN_SS, RFID_PIN_RST);
BluetoothInterface bluetooth;


void OnRFID_Recive(String message)
{
	bluetooth.sendData(BluetoothInterface::TrancmitType::RFID, message);
	tone(buzzer_Pin, 500, 250);

}
void reciveSuccessListner(String data)
{
	tone(buzzer_Pin, 1500, 250);
	delay(250);
	tone(buzzer_Pin, 2500, 500);
}
void reciveFailListner(String data)
{
	tone(buzzer_Pin, 1000);
	delay(250);
	tone(buzzer_Pin, 750);
	delay(250);
	tone(buzzer_Pin, 500);
	delay(500);
	noTone(buzzer_Pin);
}
void setup() 
{
	bluetooth.init(9600);
	rfid.init();

	rfid.setOnReciveEvent(OnRFID_Recive);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundFail, reciveFailListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundSuccess, reciveSuccessListner);

	Serial.println("---Ready---");
}

void loop()
{
	rfid.run();
	bluetooth.run();
}