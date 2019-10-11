/*
 Name:		HardwareControllSystem.ino
 Created:	9/13/2019 8:53:20 AM
 Author:	Pontus
*/

#include <ShiftRegister74HC595.h>
#include <ArduinoSTL.h>
#include <Arduino.h>
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
#include "RadiationPotentiometer.h"

#define RFID_PIN_SS 10
#define RFID_PIN_RST 9

CircuitControll cirCtrl = CircuitControll();
RadiationPotentiometer rad = RadiationPotentiometer();

RFID rfid(RFID_PIN_SS, RFID_PIN_RST);
BluetoothInterface bluetooth;
DisplayControll *display;
	

void OnRFID_Recive(String message)
{
	bluetooth.sendData(BluetoothInterface::TrancmitType::RFID, message);
	cirCtrl.addLedCmd(CircuitControll::led_e::blue, CircuitControll::onOff_e::on, 400);
}
void onRawRadiationChange(float rad)
{
	display->updateRawRadiation(rad);
}

//recivers from bluetooth
void reciveFailListner(String data)
{
	cirCtrl.soundLogout();
	cirCtrl.addLedCmd(CircuitControll::led_e::red, CircuitControll::onOff_e::on, 1000);
	cirCtrl.addLedCmd(CircuitControll::led_e::green, CircuitControll::onOff_e::on, 1000);

}
void reciveSuccessListner(String data)
{
	cirCtrl.soundLogin();
	cirCtrl.addLedCmd(CircuitControll::led_e::green , CircuitControll::onOff_e::on, 1000);
}

//bluetooth messages to send to display
void reciveWarningListner(String data)
{
	display->displayWarning(data);
	cirCtrl.soundVarning();
	cirCtrl.blinkWarning();
}
void reciveTimeListner(String data)
{
	display->updateTime(data);
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
	pinMode(PIN_ANALOGRADREAD, INPUT);

	bluetooth.init(9600);
	rfid.init();

	//----comunication mapping-----
	rfid.setOnReciveEvent(OnRFID_Recive);

	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundFail,	reciveFailListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundSuccess, reciveSuccessListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::message,		reciveMessageListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::timeChange,	reciveTimeListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::warning,		reciveWarningListner);

	display = DisplayControll::getInstance();
	display->addReciveListener(DisplayControll::reciveType::hazmatsuit, reciveHazmatsuit);
	display->addReciveListener(DisplayControll::reciveType::radiation, reciveRadiation);
	display->addReciveListener(DisplayControll::reciveType::room, reciveRoom);

	rad.setValueChangeCallback(onRawRadiationChange);
	//--------------------------------------
}

void loop()
{
	cirCtrl.run();
	rfid.run();
	bluetooth.run();
	rad.run();
	display->run();
}

