/*
 Name:		HardwareControllSystem.ino
 Created:	9/13/2019 8:53:20 AM
 Author:	Pontus
*/

// the setup function runs once when you press reset or power the board

//#include "SerialComManager.h"
#include <Wire.h>
#include "RFID.h"
#include "BluetoothInterface.h"
#include "DisplayControll.h"
#include <Arduino.h>

#define RFID_PIN_SS 10
#define RFID_PIN_RST 9

int first_LED = 8; 
int second_LED = 9;
int third_LED = 10;
int state = 0;

int buzzer_Pin = 6;
RFID rfid(RFID_PIN_SS, RFID_PIN_RST);
BluetoothInterface bluetooth;
DisplayControll *display;


#define EXTERNAL_LCD_ADRESS 2
#define THIS_ADRESS 1

	
void OnRFID_Recive(String message)
{
	bluetooth.sendData(BluetoothInterface::TrancmitType::RFID, message);
	display->displayClockIn(message);
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
void reciveAlarmListner(String data)
{
	for (int i = 0; i < 360*5; i++)
	{
		tone(buzzer_Pin, 1000 + 500 * sin(2*i* PI / 180));
		delay(1);
	}
	/*for (int i = 0; i < 3; i++)
	{
		tone(buzzer_Pin, 3000);
		delay(500);
		tone(buzzer_Pin, 1000);
		delay(500);
	}*/
	noTone(buzzer_Pin);
	/*tone(buzzer_Pin, 4000);
	delay(250);
	noTone(buzzer_Pin);
	delay(100);
	tone(buzzer_Pin, 4000);
	delay(250);
	noTone(buzzer_Pin);
	delay(100);
	tone(buzzer_Pin, 4000);
	delay(250);
	noTone(buzzer_Pin);*/
}
void reciveRoom(String data)
{
	//TODO implement
	Serial.println("Room: " + data);

}
void reciveRadiation(String data)
{
	//TODO implement
	Serial.println("Radiation: " + data);

}
void reciveHazmatsuit(String data)
{
	//TODO implement
	Serial.print("Hazmat: ");
	Serial.print((data == "0") ? "off" : "on");
	Serial.print("\n");
}
void setup() 
{
	bluetooth.init(9600);
	rfid.init();

	rfid.setOnReciveEvent(OnRFID_Recive);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundFail, reciveFailListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundSuccess, reciveSuccessListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundAlarm, reciveAlarmListner);

	display = DisplayControll::getInstance();
	display->addReciveListener(DisplayControll::reciveType::hazmatsuit, reciveHazmatsuit);
	display->addReciveListener(DisplayControll::reciveType::radiation, reciveRadiation);
	display->addReciveListener(DisplayControll::reciveType::room, reciveRoom);

	Serial.println("---Ready---");
}

void loop()
{
	rfid.run();
	bluetooth.run();
	display->run();
}

