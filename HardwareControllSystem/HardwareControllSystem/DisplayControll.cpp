#include "DisplayControll.h"
#include <Arduino.h>
#include <Wire.h>

DisplayControll::DisplayControll()
{
	Wire.begin(m_thisAdress);
	Wire.onReceive(reciveListener);
}
DisplayControll *DisplayControll::getInstance()
{
	//singelton instance
	static DisplayControll *instance = nullptr;
	if (instance == nullptr)
		instance = new DisplayControll();
	return instance;
}
DisplayControll::~DisplayControll()
{
}
void DisplayControll::addReciveListener(reciveType type, void(*callback)(String))
{
	m_listenerVector.push_back(reciveListener_s(type, callback));
}

void DisplayControll::updateTime(int hour, int minutes, int secounds)
{
	//formats the time into readable string for the display
	String data = 
		(abs(hour) < 10 ? "0" : "") + String(hour) + ":" +
		(abs(minutes) < 10 ? "0" : "") + String(minutes) + ":" +
		(abs(secounds) < 10 ? "0" : "") + String(secounds);

	sendToDisplay(cmd_timeChange, data);
}

void DisplayControll::updateRawRadiation(float radiation)
{
	sendToDisplay(cmd_radChange, String(radiation));
}

void DisplayControll::displayMessage(String message)
{
	sendToDisplay(cmd_message, message);
}

void DisplayControll::displayWarning(String message)
{
	sendToDisplay(cmd_warning, message);
}


void DisplayControll::sendToDisplay(cmdType_e type, String data)
{
	//formats the command into a readable string and sends it 
	String message = String(type) + ';' + data;
	Wire.beginTransmission(DisplayControll::m_externalAdress);
	for (int i = 0; i < message.length(); i++)
	{
		Wire.write(message[i]);
	}
	Wire.endTransmission();
}
void DisplayControll::onRecive(reciveType type, String data)
{
	//only call callbacks intressted in that type
	for (int i = 0; i < m_listenerVector.size(); i++)
	{
		if (m_listenerVector[i].type == type)
			m_listenerVector[i].callback(data);
	}
}
