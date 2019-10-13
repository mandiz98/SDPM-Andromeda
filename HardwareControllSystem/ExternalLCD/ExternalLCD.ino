/*
 Name:		ExternalLCD.ino
 Created:	9/23/2019 10:15:48 AM
 Author:	Pontus
*/


#include <stdlib.h>
#include <ArduinoSTL.h>
#include <queue>
#include <Wire.h>
#include <LiquidCrystal.h>
#include <string.h>

using namespace std;


//LCD pin to Arduino
const int pin_RS = 8;
const int pin_EN = 9;
const int pin_d4 = 4;
const int pin_d5 = 5;
const int pin_d6 = 6;
const int pin_d7 = 7;
const int pin_BL = 10;
LiquidCrystal lcd(pin_RS, pin_EN, pin_d4, pin_d5, pin_d6, pin_d7);

#define THIS_ADRESS  2
#define MASTER_ADRESS 1

//every type of existing menu
enum menu_e
{
	menu_idle,
	menu_startup,
	menu_changeRoom,
	menu_setHazmat,
	menu_displayRoom,
	menu_warning,
	menu_message,
};
//every button the user can press
enum button_e
{
	button_none,
	button_select,
	button_up,
	button_down,
	button_left,
	button_right,
};
//command type that can be recived
enum cmdType_e
{
	cmd_none = 0,
	cmd_warning = 1,
	cmd_message = 2,
	cmd_timeChange = 3,
	cmd_radChange = 4,
	cmd_dataRequest = 5,
};
//data dype that can be sent
enum dataTransmitType_e
{
	tx_room=10,
	tx_radiation=11,
	tx_hazmatsuit=12,
};

struct command_s
{
	String data;
	cmdType_e cmd;
	command_s(){}
	command_s(String data, cmdType_e cmd)
	{
		this->data = data;
		this->cmd = cmd;
	}
};
struct room_s
{
	String name;
	float coefficient;
	room_s() {}
	room_s(String name, float coefficient)
	{
		this->name = name;
		this->coefficient = coefficient;
	}
};
struct time_s
{
	int secounds;
	int minutes;
	int houres;
	time_s(){}
	time_s(byte secounds, byte minutes,	byte houres)
	{
		this->secounds = secounds;
		this->minutes = minutes;
		this->houres = houres;
	}
	String toString()
	{
		return 
			(abs(houres   < 10) ? "0" : "") + String(houres)  + ':' +
			(abs(minutes  < 10) ? "0" : "") + String(minutes) + ':' +
			(abs(secounds < 10) ? "0" : "") + String(secounds);
	}
};

//---function decleration---
room_s getCurrentRoom();
float getCurrentRad();
float getCurrentProtectionCoeff();
time_s getTimeLeft();
bool isHazOn();
void setNewCmd(command_s cmd);
void send(dataTransmitType_e type, String data);

void changeMenu(menu_e menu);
button_e peekPuttonStack();
button_e popPuttonStack();

void changeRoom(room_s room);
void setHazmatsuit(bool isOn);
//--------------------------

//global flag for when the menu is changing
bool menuIsChanging = false;
//function pointer to the current menu in use
bool(*currentMenu)(void);
//buffer for button presses
vector<button_e> buttonStack;

//variables for the current state 
room_s currentRoom;
command_s latestCmd;
time_s timeLeft;
bool hazmatsuit = false;
float rawRadiation = 0;
queue<String> transmitQueue;
queue<command_s> reciveQueue;

//every existing room
byte roomCount = 3;
const room_s roomes[] = {
	room_s("break room",  0.1),
	room_s("control room",0.5),
	room_s("reactor room",1.6)};

//get functions
room_s getCurrentRoom()
{
	return currentRoom;
}
bool isHazOn()
{
	return hazmatsuit;
}
float getCurrentProtectionCoeff()
{
	return isHazOn() ? 5 : 1;
}
float getCurrentRad()
{
	//calculation for radiation per secound
	return (rawRadiation * currentRoom.coefficient) / getCurrentProtectionCoeff();
}
time_s getTimeLeft()
{
	return timeLeft;
}

//callback function for i2c communication with external device
void reciveEvent(int size)
{
	String subMessage[2] = { "","" };
	byte index = 0;
	while (Wire.available())
	{
		char c = Wire.read();
		Serial.print(c);
		if (c == ';')
		{
			index++;
			continue;
		}
		subMessage[index] += c;
	}
	Serial.print('\n');
	//Serial.println(subMessage[0] + ';' + subMessage[1]);
	reciveQueue.push(command_s(subMessage[1], (cmdType_e)subMessage[0].toInt()));
	//setNewCmd(command_s(subMessage[1], (cmdType_e)subMessage[0].toInt()));
}
//sendes a message to the external device
void send(dataTransmitType_e type, String data)
{
	//formats the message to a readable string and sends it 
	String message = String(type) + ';' + data;
	transmitQueue.push(message);
	//Wire.beginTransmission(MASTER_ADRESS);
	//for (size_t i = 0; i < message.length(); i++)
	//{
	//	Wire.print(message[i]);
	//}
	//Serial.println(message);
	//Wire.endTransmission();
}

//returns to latest button pressed without consuming it
button_e peekPuttonStack()
{
	button_e top = button_none;
	if (buttonStack.size() > 0)
		top = buttonStack.back();
	return top;
}
//returns the latest button press and removes it from from the buffer
button_e popPuttonStack()
{
	button_e top = button_none;

	if (buttonStack.size() > 0)
	{
		top = buttonStack.back();
		buttonStack.pop_back();
	}
	return top;
}

//----------=[ MENU STATE MACHINES ]=--------------
//	these state machines returns true if they are ready for a menu change.
//	this is to give them a chanse to reset to the print state.
bool idleMenu()
{
	enum state_e
	{
		print,
		idle,
	};
	static state_e state = print;

	if (menuIsChanging)
	{
		state = print;
		return true;
	}

	switch (state)
	{
	case print:
		lcd.clear();
		lcd.print("Wating for");
		lcd.setCursor(0, 1);
		lcd.print("clock in...");
		state = idle;
		break;
	case idle:
		break;
	default:
		break;
	}

	return false;
}
bool chageRoomMenu()
{
	enum state_e
	{
		s_print,
		s_buttonPress,
		s_wait,
		s_confirm,
		s_abort,
	};
	static state_e state = s_print;
	static int index = 0;
	static int previusIndex = 0;
	static const int timeOut_ms = 5000;
	static long waitStartTime = 0;



	if (menuIsChanging)
	{
		index = previusIndex;
		state = s_print;
		return true;
	}

	switch (state)
	{
	case s_print:
		lcd.clear();
		lcd.setCursor(0, 0);
		lcd.print(roomes[index].name);

		lcd.setCursor(0, 1);
		lcd.print((index > 0) ? "<L" : "  ");
		lcd.print("  [Select]  ");
		lcd.print((index < roomCount-1) ? "R>" : "  ");
		

		waitStartTime = millis();
		state = s_wait;
		break;
	case s_wait:
		if (waitStartTime + timeOut_ms < millis())
			state = s_abort;
		if (peekPuttonStack() != button_none)
			state = s_buttonPress;

		break;
	case s_abort:
		index = previusIndex;
		changeMenu(menu_displayRoom);
		break;
	case s_confirm:
		previusIndex = index;
		changeRoom(roomes[index]);
		changeMenu(menu_displayRoom);
		break;
	case s_buttonPress:

		button_e button = popPuttonStack();
		switch (button)
		{
		case button_select:
			state = s_confirm;
			break;
		case button_left:
			if (index > 0)
			{
				index--;
				state = s_print;
			}
			break;
		case button_right:
			if (index < roomCount - 1)
			{
				index++;
				state = s_print;
			}
			break;
		default:
			break;
		}

		break;

	default:
		Serial.println("default");
		break;
	}
	return false;
}
bool hazmatMenu()
{
	enum state_e
	{
		print,
		wait,
		change,
	};
	static state_e state = print;
	static const int waitTimeOut_ms = 5000;
	static long waitStartTime = 0;

	if (menuIsChanging)
	{
		state = print;
		return true;
	}

	switch (state)
	{
	case print:
		lcd.clear();
		lcd.print("Hazmat suit");
		lcd.setCursor(0, 1);
		lcd.print("<Select>   ");
		lcd.print(isHazOn() ? "[ON] " : "[OFF]");

		waitStartTime = millis();
		state = wait;
		break;
	case wait:
		if (peekPuttonStack() == button_select)
			state = change;
		else if (waitStartTime + waitTimeOut_ms < millis() || peekPuttonStack() == button_down || peekPuttonStack() == button_up)
			changeMenu(menu_displayRoom);

		popPuttonStack();

		break;
	case change:
		setHazmatsuit(!isHazOn());
		state = print;
		break;
	default:
		break;
	}


}
bool displayRoomMenu()
{
	enum state_e
	{
		print,
		idle,
	};
	static state_e state = print;
	static time_s oldTimeLeft = time_s();
	static float oldRad = 0;

	if (menuIsChanging)
	{
		state = print;
		return true;
	}



	switch (state)
	{
	case print:
		lcd.clear();
		lcd.setCursor(0, 0);
		lcd.print(currentRoom.name);
		lcd.setCursor(14, 0);
		lcd.print(isHazOn() ? "H" : "");
		lcd.setCursor(0, 1);
		lcd.print(String(getCurrentRad()));
		lcd.setCursor(8, 1);
		lcd.print(getTimeLeft().toString());
		oldTimeLeft = getTimeLeft();
		oldRad = getCurrentRad();
		state = idle;
		break;
	case idle:
		if (getTimeLeft().toString() != oldTimeLeft.toString() || oldRad != getCurrentRad())
			state = print;

		if (peekPuttonStack() == button_select)
			changeMenu(menu_changeRoom);

		if (peekPuttonStack() == button_up || peekPuttonStack() == button_down)
			changeMenu(menu_setHazmat);

		popPuttonStack();
		break;
	default:
		break;
	}

	return false;
}
bool warningMenu()
{
	enum state_e
	{
		print,
		wait,
	};
	static state_e state = print;

	if (menuIsChanging)
	{
		state = print;
		return true;
	}

	switch (state)
	{
	case print:		
		lcd.clear();
		lcd.print("WARNING");
		lcd.setCursor(0, 1);
		lcd.print(latestCmd.data);
		state = wait;

		break;
	case wait:
		if (popPuttonStack() == button_select)
			changeMenu(menu_displayRoom);
		break;
	default:
		break;
	}

	return false;
}
bool messageMenu()
{
	enum state_e
	{
		print,
		wait,
		end,
	};
	static state_e state = print;
	static const int timeOut_ms = 5000;
	static long waitStartTime = 0;

	if (menuIsChanging)
	{
		state = print;
		return true;
	}

	switch (state)
	{
	case print:

		lcd.clear();
		for (int i = 0; i < latestCmd.data.length(); i++)
		{
			if (latestCmd.data[i] == '\n')
				lcd.setCursor(0, 1);
			else lcd.print(latestCmd.data[i]);
		}
		waitStartTime = millis();
		state = wait;
		break;
	case wait:
		if (waitStartTime + timeOut_ms < millis() || popPuttonStack() == button_select)
			state = end;
		break;
	case end:
		state = print;
		changeMenu(menu_displayRoom);
		break;
	default:
		break;
	}
	return false;
}
//-------------------------------------------------

//sets a new command recived from external device
void setNewCmd(command_s cmd)
{
	latestCmd = cmd;
	//responce to the command
	switch (cmd.cmd)
	{
	case cmd_warning:
		changeMenu(menu_warning);
		break;
	case cmd_message:
		changeMenu(menu_message);
		break;
	case cmd_timeChange:
		timeLeft.houres   = cmd.data.substring(0, 2).toInt();
		timeLeft.minutes  = cmd.data.substring(3, 5).toInt();
		timeLeft.secounds = cmd.data.substring(6, 8).toInt();
		break;
	case cmd_radChange:
		rawRadiation = latestCmd.data.toFloat();
		send(tx_radiation, String(getCurrentRad()));
		break;
	case cmd_dataRequest:
		//send room, clothes and radiation
		send(tx_room, String(getCurrentRoom().name));
		send(tx_hazmatsuit, String(hazmatsuit));
		send(tx_radiation, String(getCurrentRad()));
	default:
		break;
	}
}
//sets a new room and notifies external device
void changeRoom(room_s room)
{
	currentRoom = room;
	send(tx_room, room.name);
	send(tx_radiation,String(getCurrentRad()));
}

void changeMenu(menu_e menu)
{
	//time limit for a menu to prepare for a menu change
	//this is to avoid infinie wating
	const int timeOutLimit_ms = 10;

	//sets flag for menu change
	menuIsChanging = true;

	//wait for the menu to accept the change
	long waitStartTime = millis();
	while (!currentMenu())
	{
		//stop wating if the time limit is reached
		if (waitStartTime + timeOutLimit_ms < millis())
		{
			Serial.println("WARNING: menu change timeout");
			break;
		}
	}

	switch (menu)
	{
	case menu_startup:
		break;
	case menu_idle:
		currentMenu = idleMenu;
		break;
	case menu_changeRoom:
		currentMenu = chageRoomMenu;
		break;
	case menu_displayRoom:
		currentMenu = displayRoomMenu;
		break;
	case menu_setHazmat:
		currentMenu = hazmatMenu;
		break;
	case menu_warning:
		currentMenu = warningMenu;
		break;
	case menu_message:
		currentMenu = messageMenu;
		break;
	default:
		break;
	}
	
	
	menuIsChanging = false;
}

void runTimer()
{
	static long time = 0;
	static const int timeIntervall = 1000;//1s

	if (millis() < time + timeIntervall)
		return;

	time = millis();
	timeLeft.secounds--;
	if (timeLeft.secounds >= 0)
		return;
	timeLeft.secounds = 59;
	timeLeft.minutes--;
	if (timeLeft.minutes >= 0)
		return;
	timeLeft.minutes = 59;
	timeLeft.houres--;
	if (timeLeft.houres < 0)
	{
		timeLeft.houres = 0;
		timeLeft.minutes = 0;
		timeLeft.secounds = 0;
	}
	
}
void runMenu()
{
	//only needs to run the menu in focus
	currentMenu();
}
void runButtonReader()
{
	enum state_s
	{
		idle,
		press,
		waitForRelese,
	};
	static state_s state = idle;
	int analogValue = analogRead(0);

	switch (state)
	{
	case idle:
		if (analogValue < 800)
			state = press;
		break;
	case press:
		if (analogValue < 60)
			buttonStack.push_back(button_e::button_right);
		else if (analogValue < 200)
			buttonStack.push_back(button_e::button_up);
		else if (analogValue < 400)
			buttonStack.push_back(button_e::button_down);
		else if (analogValue < 600)
			buttonStack.push_back(button_e::button_left);
		else if (analogValue < 800)
			buttonStack.push_back(button_e::button_select);

		state = waitForRelese;
		break;
	case waitForRelese:
		if (analogValue >= 800)
			state = idle;
		break;
	default:
		break;
	}
}
void runTransmitter()
{
	if (transmitQueue.empty())
		return;

	String message = transmitQueue.front();
	transmitQueue.pop();
	Wire.beginTransmission(MASTER_ADRESS);
	for (size_t i = 0; i < message.length(); i++)
	{
		Wire.print(message[i]);
	}
	Wire.endTransmission();
}
void runReciver()
{
	if (reciveQueue.empty())
		return;

	setNewCmd(reciveQueue.front());
	reciveQueue.pop();
}
void setHazmatsuit(bool isOn)
{
	hazmatsuit = isOn;

	send(tx_hazmatsuit, String(hazmatsuit));
	send(tx_radiation, String(getCurrentRad()));
}

void setup()
{
	Wire.begin(THIS_ADRESS);
	Wire.setClock(100000);
	Wire.onReceive(reciveEvent);
	Serial.begin(9600);
	 
	currentRoom = roomes[0];
	currentMenu = displayRoomMenu;
	latestCmd = command_s("", cmd_none);
	hazmatsuit = false;
	timeLeft = time_s(0, 0, 0);

	lcd.begin(16, 2);

	Serial.println("---ready---");
}
void loop()
{
	//reciveEvent(0);

	runTimer();
	runButtonReader();
	runMenu();
	runTransmitter();
	runReciver();
}