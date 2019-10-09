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
		//one,
		//two,
		//three,
		//four,
		//five,
		//six,
		//seven,
		//eight,
		//nine,
		//ten,

		red = 2, 
		green = 1,
		blue = 0,
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

		toneCmd(unsigned short freq, unsigned short dur = 0) { duration = dur; frequency = freq; }

	};

	void clearToneQueue();

	void addToneToQueue(toneCmd tone);

	void addToneArrayToQueue(const toneCmd queue[], int lenght);

	//void setLed(ledColor_e, onOff_e);

	void run();

	//void setShiftRegister(byte);

	void soundLogin();
	void soundLogout();
	void soundBoot();
	void soundVarning();
	void soundHighBeep();
	void soundLowBeep();

	void blinkWarning();

	struct ledCmd
	{
		enum state_e
		{
			ready,
			running,
		};
		state_e state = ready;

		onOff_e onOff;
		unsigned short duration;
		long startTime;

		ledCmd(onOff_e onOrOff, unsigned short delay) {onOff = onOrOff, duration = delay; }
	};

	void addLedCmd(led_e led, onOff_e value, int dur = -1);

	void addLedCmdArrayToQueue(led_e led, const ledCmd queue[], int lenght);

protected:

private:

	const unsigned short warningTimer = 400;
	const unsigned short warningTimerDelay = 100;

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
	const toneCmd tuneVarning[7] = {
		toneCmd(3500,warningTimer),
		toneCmd(0, warningTimerDelay),
		toneCmd(3500,warningTimer),
		toneCmd(0, warningTimerDelay),
		toneCmd(3500,warningTimer),
		toneCmd(0, warningTimerDelay),
		toneCmd(3500,warningTimer)
	};
	const toneCmd tuneHighBeep[2] = {
		toneCmd(2500,400),
		toneCmd(0,200),
	};
	const toneCmd tuneLowBeep[2] = {
		toneCmd(1500,400),
		toneCmd(0,200),
	};

	const ledCmd warningBlink[7] = {
		ledCmd(onOff_e::on,warningTimer),
		ledCmd(onOff_e::off,warningTimerDelay),
		ledCmd(onOff_e::on,warningTimer),
		ledCmd(onOff_e::off,warningTimerDelay),
		ledCmd(onOff_e::on,warningTimer),
		ledCmd(onOff_e::off,warningTimerDelay),
		ledCmd(onOff_e::on,warningTimer),
	};

	void deleteLedQueue(led_e);
};