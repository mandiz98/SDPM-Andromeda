
#include "BluetoothInterface.h"
#include <Arduino.h>



BluetoothInterface::BluetoothInterface()
{
	m_onCommandVector = vector<OnCommand>();
}


BluetoothInterface::~BluetoothInterface()
{
}

void BluetoothInterface::init(unsigned long baud)
{
	Serial.begin(baud);
}

void BluetoothInterface::run()
{
	if (!Serial.available())
		return;
	

	onCommandRecive(Serial.readString());
}

void BluetoothInterface::sendData(TrancmitType type, String data)
{
	//formats the data before sending 
	String message = 
		String(type) + m_dataSeperator +
		String(millis()) + m_dataSeperator +
		data + m_messageSeperator;

	Serial.print(message);
}


void BluetoothInterface::addOnCommandReciveEvent(ReciveType type, void(*onCommandEvent)(String data))
{
	m_onCommandVector.push_back(OnCommand(type, onCommandEvent));
}

void BluetoothInterface::onCommandRecive(String message)
{ 

	String subMessage[3] = {"","",""};
	bool fail = false;
	
	//breaking upp the raw message into usable substrings
	int subStringIndex = 0;
	for (int i = 0; i < message.length(); i++)
	{
		if (message.charAt(i) == m_messageSeperator)
			break;
		else if (message.charAt(i) == m_dataSeperator)
		{
			subStringIndex++;
			continue;
		}
		if (subStringIndex > 2)
			fail = true;
		else subMessage[subStringIndex] += message.charAt(i);
	}
	
	//mappes the substrings into there proper roles
	if (!fail)
	{
		ReciveType type = (ReciveType)subMessage[0].toInt();
		String data = subMessage[2];
		sendToCallback(type, data);
	}
}

void BluetoothInterface::sendToCallback(ReciveType type, String data)
{
	//calles every listener that is lisening to this datatype
	for (size_t i = 0; i < m_onCommandVector.size(); i++)
	{
		OnCommand command = m_onCommandVector.at(i);
		if (command.onType == type && command.onCommandEvent != nullptr)
			command.onCommandEvent(data);
	}
}
