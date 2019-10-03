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
		unsigned int duration;
		unsigned int frequency;

		toneCmd(unsigned int freq, unsigned int dur) { duration = dur; frequency = freq; }

	};

	void clearToneQueue();

	void addToneToQueue(toneCmd tone);

	void addArrayToQueue(toneCmd queue[], int lenght);

	//void setLed(ledColor_e, onOff_e);

	void run();

	//void setShiftRegister(byte);

	

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

};