#pragma once
#include "RFID.h"
#include <Arduino.h>
#include <queue>
#include <vector>
#include <ArduinoSTL.h>

class CircuitControll
{
#define PIN_BUZZER 6
#define PIN_LATCH 2
#define PIN_CLOCK 4
#define PIN_DATA 7
public:
	
	CircuitControll();

	enum led_e
	{
		one,
		two,
		three,
		four,
		five,
		six,
		seven,
		eight,
		nine,
		ten,
	};

	enum onOff_e
	{
		off,
		on,
	};

	struct toneCmd
	{
		unsigned short duration;
		unsigned short frequency;

		toneCmd(unsigned short freq, unsigned short dur = -1) { duration = dur; frequency = freq; }

	};

	void clearToneQueue();

	void addToneToQueue(toneCmd tone);

	void addArrayToQueue(const toneCmd queue[], int lenght);

	//void setLed(ledColor_e, onOff_e);

	void run();

	//void setShiftRegister(byte);

	void soundLogin();
	void soundLogout();
	void soundBoot();
	void soundVarning();
	void soundHighBeep();
	void soundLowBeep();

	

	void addLedCmd(led_e led, onOff_e value, int dur = -1);
protected:

private:

	

	struct ledCmd
	{
		enum state_e
		{
			ready,
			running,
		};
		state_e state = ready;

		onOff_e onOff;
		int duration;
		long startTime;
		bool active = false;

		ledCmd(onOff_e onOrOff, int delay) {onOff = onOrOff, duration = delay; }
	};

	const int ledArraySize_c = 8;

	void setLed(led_e, onOff_e);

	typedef queue<ledCmd*> ledCmdQueue;

	ledCmdQueue cmdArray[8];
	

	//unsigned int m_frequency = 2500;
	//unsigned int m_duration = 1000;

	queue<toneCmd> toneQueue;

	byte bitPatern = 0;

	void runBuzzer();
	
	void runLeds();
	
	void updateShiftRegister(byte);

	const toneCmd tuneLogin[5] = {
		toneCmd(1500,200),
		toneCmd(1750,200),
		toneCmd(2000,200),
		toneCmd(2250,200),
		toneCmd(2500,200),
	};
	const toneCmd tuneLogout[5] = {
		toneCmd(2500,200),
		toneCmd(2250,200),
		toneCmd(2000,200),
		toneCmd(1750,200),
		toneCmd(1500,200),
	};
	const toneCmd tuneBoot[6] = {
		toneCmd(1750,200),
		toneCmd(2000,200),
		toneCmd(2250,200),
		toneCmd(2500,300),
		toneCmd(2000,200),
		toneCmd(2500,400),
	};
	const toneCmd tuneVarning[8] = {
		toneCmd(3500,800),
		toneCmd(0, 300),
		toneCmd(3500,800),
		toneCmd(0, 300),
		toneCmd(3500,800),
		toneCmd(0, 300),
		toneCmd(3500,800),
		toneCmd(0),
	};
	const toneCmd tuneHighBeep[2] = {
		toneCmd(2500,400),
		toneCmd(0,200),
	};
	const toneCmd tuneLowBeep[2] = {
		toneCmd(1500,400),
		toneCmd(0,200),
	};

};