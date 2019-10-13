#pragma once


#include <stdlib.h>
#include <WString.h>
#include <ArduinoSTL.h>

using namespace std;
class BluetoothInterface
{
public:
	BluetoothInterface();
	~BluetoothInterface();
	
	enum TrancmitType
	{
		RFID = 4010,
		radiationChange = 5000,
		roomChange = 2000,
		hazmatsuit = 2001,
	};
	enum ReciveType
	{
		soundSuccess = 3000,
		soundFail = 3001,
		warning = 3002,
		timeChange = 3003,
		message=3004,
		dataDump = 3005,
	};

	void init(unsigned long baud);

	//main state machine for handeling bluetooth messegas sent to the device
	void run();

	//sends data to bluetooth connected android application
	void sendData(TrancmitType type,String data);
	//adds a listener callback function thad will lisen for specifik data tranmitions
	void addOnCommandReciveEvent(ReciveType type,void(*onCommandEvent)(String data));

private:
	//handels raw data trancmitions
	void onCommandRecive(String message);
	//calles all listener that lisen for type
	void sendToCallback(ReciveType type,String data);

	struct OnCommand
	{
		OnCommand(ReciveType type, void(*onCommandEvent)(String data))
		{
			this->onType = type;
			this->onCommandEvent = onCommandEvent;
		}

		ReciveType onType;
		void(*onCommandEvent)(String data);
	};


	const char m_messageSeperator = '\n';
	const char m_dataSeperator = ';';

	vector<OnCommand> m_onCommandVector;
};

