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
#include "RFID.h"
#include "CircuitControll.h"
#include "BluetoothInterface.h"

#define RFID_PIN_SS 10
#define RFID_PIN_RST 9

int first_LED = 8; 
int second_LED = 9;
int third_LED = 10;
int state = 0;

CircuitControll cirCtrl = CircuitControll();

SerialComManager serialManager(9600);
RFID rfid(RFID_PIN_SS, RFID_PIN_RST);
BluetoothInterface bluetooth;


void OnRFID_Recive(String message)
{

	bluetooth.sendData(BluetoothInterface::TrancmitType::RFID, message);

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

	pinMode(PIN_LATCH, OUTPUT);
	pinMode(PIN_DATA, OUTPUT);
	pinMode(PIN_CLOCK, OUTPUT);

	bluetooth.init(9600);
	rfid.init();

	rfid.setOnReciveEvent(OnRFID_Recive);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundFail, reciveFailListner);
	bluetooth.addOnCommandReciveEvent(BluetoothInterface::ReciveType::soundSuccess, reciveSuccessListner);
  
	Serial.println("---Ready---");

}

void loop()
{
	cirCtrl.run();
	rfid.run();
	bluetooth.run();
}