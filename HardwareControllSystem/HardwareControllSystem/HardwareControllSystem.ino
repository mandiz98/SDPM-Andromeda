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

#define RFID_PIN_SS 10
#define RFID_PIN_RST 9

int first_LED = 8; 
int second_LED = 9;
int third_LED = 10;
int state = 0;

CircuitControll cirCtrl = CircuitControll();

SerialComManager serialManager(9600);
RFID rfid(RFID_PIN_SS, RFID_PIN_RST);


void OnRFID_Recive(String message)
{
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(2500, 250));
	Serial.println(message);
	if (message.substring(1) == "A6 53 9F F7")
	{
		Serial.println("Authorized access\n");
		delay(250);
		cirCtrl.addToneToQueue(CircuitControll::toneCmd(2500, 250));
		//tone(PIN_BUZZER, 2500, 250);
		
	}
	else
	{
		Serial.println(" Access denied\n");
		delay(250);
		cirCtrl.addToneToQueue(CircuitControll::toneCmd(1500, 250));
		tone(PIN_BUZZER, 1500, 250);
	}
}

void setup() 
{	
	
	Serial.begin(9600);
	rfid.Init();
	rfid.SetOnReciveEvent(OnRFID_Recive);

	cirCtrl.addToneToQueue(CircuitControll::toneCmd(1500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(2500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(3500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(1500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(2500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(3500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(1500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(2500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(3500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(1500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(2500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(3500, 250));
	cirCtrl.addToneToQueue(CircuitControll::toneCmd(500, 250));

	pinMode(PIN_LATCH, OUTPUT);
	pinMode(PIN_DATA, OUTPUT);
	pinMode(PIN_CLOCK, OUTPUT);


	Serial.println("start leds");
	
	cirCtrl.addLedCmd(CircuitControll::led_e::one, CircuitControll::onOff_e::on, 500);
	cirCtrl.addLedCmd(CircuitControll::led_e::one, CircuitControll::onOff_e::off, 500);

	cirCtrl.addLedCmd(CircuitControll::led_e::two, CircuitControll::onOff_e::on, 300);
	cirCtrl.addLedCmd(CircuitControll::led_e::two, CircuitControll::onOff_e::off, 300);

	cirCtrl.addLedCmd(CircuitControll::led_e::three, CircuitControll::onOff_e::on, 480);
	cirCtrl.addLedCmd(CircuitControll::led_e::three, CircuitControll::onOff_e::off, 480);

	cirCtrl.addLedCmd(CircuitControll::led_e::four, CircuitControll::onOff_e::on, 470);
	cirCtrl.addLedCmd(CircuitControll::led_e::four, CircuitControll::onOff_e::off);

	cirCtrl.addLedCmd(CircuitControll::led_e::five, CircuitControll::onOff_e::on, 500);
	cirCtrl.addLedCmd(CircuitControll::led_e::five, CircuitControll::onOff_e::off);

	cirCtrl.addLedCmd(CircuitControll::led_e::six, CircuitControll::onOff_e::on, 500);
	cirCtrl.addLedCmd(CircuitControll::led_e::six, CircuitControll::onOff_e::off);

	cirCtrl.addLedCmd(CircuitControll::led_e::seven, CircuitControll::onOff_e::on, 500);
	cirCtrl.addLedCmd(CircuitControll::led_e::seven, CircuitControll::onOff_e::off);

	cirCtrl.addLedCmd(CircuitControll::led_e::eight, CircuitControll::onOff_e::on, 500);
	cirCtrl.addLedCmd(CircuitControll::led_e::eight, CircuitControll::onOff_e::off);
	

	Serial.println("Leds done");

	
	Serial.println("---Ready---");


}

void loop()
{
	//rfid.Run();
	cirCtrl.run();


}