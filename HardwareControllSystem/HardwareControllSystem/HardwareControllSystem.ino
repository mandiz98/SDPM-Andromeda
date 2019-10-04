/*
 Name:		HardwareControllSystem.ino
 Created:	9/13/2019 8:53:20 AM
 Author:	Pontus
*/

// the setup function runs once when you press reset or power the board

#include <ShiftRegister74HC595.h>
#include <ArduinoSTL.h>
#include <require_cpp11.h>
#include <MFRC522Extended.h>
#include <MFRC522.h>
#include <deprecated.h>
#include <SPI.h>
#include "SerialComManager.h"
//#include "SerialComManager.h"
#include <Wire.h>
#include "RFID.h"
#include "CircuitControll.h"
#include "BluetoothInterface.h"
#include "DisplayControll.h"
#include <Arduino.h>

#define RFID_PIN_SS 10
#define RFID_PIN_RST 9

int first_LED = 8; 
int second_LED = 9;
int third_LED = 10;
int state = 0;

CircuitControll cirCtrl = CircuitControll();

RFID rfid(RFID_PIN_SS, RFID_PIN_RST);
BluetoothInterface bluetooth;
DisplayControll *display;


#define EXTERNAL_LCD_ADRESS 2
#define THIS_ADRESS 1

	
void OnRFID_Recive(String message)
{

	bluetooth.sendData(BluetoothInterface::TrancmitType::RFID, message);
	display->displayClockIn(message);
}
void reciveSuccessListner(String data)
{
}
void reciveFailListner(String data)
{
}
void reciveAlarmListner(String data)
{
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

	pinMode(PIN_LATCH, OUTPUT);
	pinMode(PIN_DATA, OUTPUT);
	pinMode(PIN_CLOCK, OUTPUT);

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
	cirCtrl.run();
	rfid.run();
	bluetooth.run();
	display->run();
}

