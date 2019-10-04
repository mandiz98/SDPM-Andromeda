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
	static DisplayControll *instance = nullptr;
	if (instance == nullptr)
		instance = new DisplayControll();
	return instance;
}
DisplayControll::~DisplayControll()
{
}
void DisplayControll::run()
{
}

void DisplayControll::addReciveListener(reciveType type, void(*callback)(String))
{
	m_listenerVector.push_back(reciveListener_s(type, callback));
}

void DisplayControll::displayClockIn(String message)
{
	sendToDisplay(cmd_clockIn, message);
}



void DisplayControll::sendToDisplay(cmdType_e type, String data)
{
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
	for (int i = 0; i < m_listenerVector.size(); i++)
	{
		if (m_listenerVector[i].type == type)
			m_listenerVector[i].callback(data);
	}
}
