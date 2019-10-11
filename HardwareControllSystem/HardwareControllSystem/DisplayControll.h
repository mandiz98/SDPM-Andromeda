#pragma once
#include <ArduinoSTL.h>
#include <Arduino.h>
#include <string>
#include <vector>
#include <queue>
#include <stdlib.h>
#include <Wire.h>

using namespace std;
class DisplayControll
{
public:
	//returnes a singelton instance 
	static DisplayControll* getInstance();
	~DisplayControll();

	void run();

	//types if data that can be recived from the display
	enum reciveType
	{
		room = 10,
		radiation = 11,
		hazmatsuit = 12,
	};
	//add an callback function that listen for a specific recived data type
	void addReciveListener(reciveType type,void(*callback)(String));

	//updates the displayed time on the display
	//NOTE: the display automaticly counts down every secound
	void updateTime(String data);

	//updates the raw radiation (before exposure calculations) 
	void updateRawRadiation(float radiation);

	//sends a message for the display. the message automaticle disapear after a while
	//NOTE: messages containing rowes longer then 16 charachter will be cut of
	void displayMessage(String message);

	//sends a warning for the display. the message must manualy closed on the display for it to go away (this is done by the user)
	void displayWarning(String message);

private:
	DisplayControll();

	//types of command to send to the display
	enum cmdType_e
	{
		cmd_warning = 1,
		cmd_message = 2,
		cmd_timeChange = 3,
		cmd_radChange = 4,
	};
	struct reciveData_s
	{
		reciveType type;
		String data;
		reciveData_s(reciveType type, String data)
		{
			this->type = type;
			this->data = data;
		}
	};

	//callback function that handles messages recived from the display
	static void reciveListener(int size)
	{
		//decoding the message into type and data
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

		//sends the message to all listners
		reciveType type = (reciveType)subMessage[0].toInt();
		String data = subMessage[1];
		getInstance()->reciveQueue.push(reciveData_s(type, data));
		//getInstance()->onRecive(type, data);
	}

	void runReciver();
	void runTrancmiter();

	//format a command and sends it to the display
	void sendToDisplay(cmdType_e type,String data);

	//callback function that relay a message to external callbacks
	void onRecive(reciveType type,String data);

	//support struct for external callback functions
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

	
	//i2c adress for the external adress
 	const int m_externalAdress = 2;
	//i2s adress for this device
	const int m_thisAdress = 1;

	queue<String> transmitQueue;
	queue<reciveData_s> reciveQueue;
	//container for external callbacks
	vector<reciveListener_s> m_listenerVector;
};

