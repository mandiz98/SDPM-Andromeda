#include "SerialComManager.h"



SerialComManager::SerialComManager(unsigned long baud)
{
	Serial.begin(baud);		// Initialize serial communications with the PC
	while (!Serial);		// Do nothing if no serial port is opened (added for Arduinos based on ATMEGA32U4)
}


SerialComManager::~SerialComManager()
{
}

void SerialComManager::Trancmit(char * message)
{
	Serial.println(message);
}