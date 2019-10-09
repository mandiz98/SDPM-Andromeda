/*
 Name:		HardwareControllSystem.ino
 Created:	9/13/2019 8:53:20 AM
 Author:	Pontus
*/

#include <ShiftRegister74HC595.h>
#include <ArduinoSTL.h>
#include <require_cpp11.h>
#include <MFRC522Extended.h>
#include <MFRC522.h>
#include <deprecated.h>
#include <SPI.h>
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
	

void OnRFID_Recive(String message)
{
	bluetooth.sendData(BluetoothInterface::TrancmitType::RFID, message);
	display->displayMessage("RFID scann: \n"+message);
	tone(6, 200, 500);
}
void onRawRadiationChange(float rad)
{
	display->updateRawRadiation(rad);
}

//recivers from bluetooth
void reciveSuccessListner(String data)
{//TODO implement
}
void reciveFailListner(String data)
{//TODO implement
}
void reciveAlarmListner(String data)
{//TODO implement
}

//bluetooth messages to relay to display
void reciveWarningListner(String data)
{
	display->displayWarning(data);
}
void reciveTimeListner(String data)
{
	String subData[3];
	int index = 0;

	for (int i = 0; i < data.length(); i++)
	{
		if (data[i] == ':')
			index++;
		else subData[index] += data[i];
	}
	int hours = subData[0].toInt();
	int min = subData[1].toInt();
	int sec = subData[2].toInt();
	display->updateTime(hours, min, sec);
}
void reciveMessageListner(String data)
{
	display->displayMessage(data);
}

//recivers from the external lcd
void reciveRoom(String data)
{
	bluetooth.sendData(BluetoothInterface::TrancmitType::roomChange, data);
}
void reciveRadiation(String data)
{
	bluetooth.sendData(BluetoothInterface::TrancmitType::radiationChange, data);
}
void reciveHazmatsuit(String data)
{
	bluetooth.sendData(BluetoothInterface::TrancmitType::hazmatsuit, data);
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
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::message, reciveMessageListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::timeChange, reciveTimeListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::warning, reciveWarningListner);
	//bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundAlarm, reciveAlarmListner);

	display = DisplayControll::getInstance();
	display->addReciveListener(DisplayControll::reciveType::hazmatsuit, reciveHazmatsuit);
	display->addReciveListener(DisplayControll::reciveType::radiation, reciveRadiation);
	display->addReciveListener(DisplayControll::reciveType::room, reciveRoom);
}

void loop()
{
	cirCtrl.run();
	rfid.run();
	bluetooth.run();
}

