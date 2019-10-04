#pragma once
#include <ArduinoSTL.h>
#include <Arduino.h>
#include <string>
#include <vector>
#include <stdlib.h>
#include <Wire.h>

using namespace std;
class DisplayControll
{
public:
	static DisplayControll* getInstance();
	~DisplayControll();

	enum reciveType
	{
		room = 10,
		radiation = 11,
		hazmatsuit = 12,
	};

	void run();

	void addReciveListener(reciveType type,void(*callback)(String));

	//void updateTime(int hour, int minutes, int secounds);
	void displayClockIn(String message);
	//void displayClockOut(String message);
	//void displayWarning(String message);

private:
	DisplayControll();


	enum cmdType_e
	{
		cmd_warning = 1,
		cmd_clockIn = 2,
		cmd_clockOut = 3,
		cmd_timeChange = 4,
	};

	static void reciveListener(int size)
	{
		String subMessage[2];
		int index = 0;
		while (Wire.available())
		{
			char c = Wire.read();
			if (c == ';')
			{
				index++;
				continue;
			}
			subMessage[index] += c;
		}

		reciveType type = (reciveType)subMessage[0].toInt();
		String data = subMessage[1];
		getInstance()->onRecive(type, data);
	}
	void sendToDisplay(cmdType_e type,String data);
	void onRecive(reciveType type,String data);

	struct reciveListener_s
	{
		reciveListener_s(){}
		reciveListener_s(reciveType type, void(*callback)(String))
		{
			this->type = type;
			this->callback = callback;
		}
		void(*callback)(String);
		reciveType type;
	};

 	static const int m_externalAdress = 2;
	static const int m_thisAdress = 1;

	vector<reciveListener_s> m_listenerVector;
};

