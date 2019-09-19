#pragma once

#include <HardwareSerial.h>

//Not ready for use
class SerialComManager
{
public:
	SerialComManager(unsigned long baud);
	~SerialComManager();

	void Trancmit(char* message);

private:

};

