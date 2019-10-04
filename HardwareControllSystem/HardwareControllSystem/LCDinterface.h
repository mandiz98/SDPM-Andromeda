#pragma once

#include <Arduino.h>
#include <vector>

class LCDinterface
{
public:
	LCDinterface();
	~LCDinterface();

	void init();

	void updateTimeLeft();
	void sendWarning();
	void sendClockEvent();



private:

	void sendString(String message);
	void recive(int size);

	void onRoomChangeEvent();


	const byte m_ExternalLCDadress = 2;
	const byte m_ThisAdress = 1;

};

