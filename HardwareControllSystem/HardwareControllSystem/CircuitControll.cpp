#include "CircuitControll.h"

CircuitControll::CircuitControll(){}

void CircuitControll::runBuzzer()
{
	//Serial.println("Tone size: " + (String)toneQueue.size());
	//tone(PIN_BUZZER, frequency, duration);
	//Serial.print("buzz");
	enum StatesBuzz
	{
		s_ready,
		s_running,
		s_done,
	};

	static StatesBuzz buzz_state = s_ready;
	static long mili_start;
	switch (buzz_state)
	{
	case s_ready:
		//Serial.print("s_ready");
		if (!toneQueue.empty())
		{
			mili_start = millis();
			buzz_state = s_running;
		}
		break;
	case s_running:
		//Serial.print("BUZZ: " + (String)toneQueue.size());
		if (toneQueue.front().frequency == 0)
		{
			noTone(PIN_BUZZER);
		}
		else
			tone(PIN_BUZZER, toneQueue.front().frequency);
		
		if (millis() >= mili_start + toneQueue.front().duration)
		{
			buzz_state = s_done;
		}
		break;
	case s_done:
		//Serial.print("s_done");
		noTone(PIN_BUZZER);
		toneQueue.pop();
		buzz_state = s_ready;
		break;
	default:
		break;
	}
}

void CircuitControll::clearToneQueue()
{
	while (!toneQueue.empty())
		toneQueue.pop();
}

void CircuitControll::addToneToQueue(toneCmd tone)
{
	clearToneQueue();
	toneQueue.push(tone);
}

void CircuitControll::addToneArrayToQueue(const toneCmd queue[], int lenght)
{
	clearToneQueue();
	for (int i = 0; i < lenght; i++)
	{
		toneQueue.push(queue[i]);
	}
}

void CircuitControll::run()
{
	runBuzzer();
	runLeds();
}

void CircuitControll::soundLogin()
{
	addToneArrayToQueue(tuneLogin, 5);
}

void CircuitControll::soundLogout()
{
	addToneArrayToQueue(tuneLogout, 5);
}

void CircuitControll::soundBoot()
{
	addToneArrayToQueue(tuneBoot, 6);
}

void CircuitControll::soundVarning()
{
	addToneArrayToQueue(tuneVarning, 7);
}

void CircuitControll::soundLowBeep()
{
	addToneArrayToQueue(tuneLowBeep, 2);
}

void CircuitControll::blinkWarning()
{
	addLedCmdArrayToQueue(led_e::red, warningBlink, 7);
}

void CircuitControll::soundHighBeep()
{
	addToneArrayToQueue(tuneHighBeep, 2);
}


//void CircuitControll::setLed(ledColor_e color, onOff_e onOff)
//{
//	byte myByte = 0;
//	bitWrite(myByte, (int)color, onOff);
//	updateShiftRegister(myByte);
//}

void CircuitControll::setLed(led_e pos, onOff_e onOff)
{
	bitWrite(bitPatern, pos, onOff);
	//Serial.println("bitpattern: " + (String)bitPatern);
}

void CircuitControll::runLeds()
{
	for (ledCmdQueue::size_type i = 0; i < ledArraySize_c; i++)
	{
		if (cmdArray[i].empty())
			continue;

		ledCmd *currentLed = cmdArray[i].front();

		switch (currentLed->state)
		{
		case ledCmd::state_e::ready:

			currentLed->startTime = millis();
			setLed((led_e)i, currentLed->onOff);
			//Serial.println("Led " + (String)i+" is: " + (String)currentLed->onOff);
			currentLed->state = ledCmd::state_e::running;
			break;
		case ledCmd::state_e::running:
			if (millis() > currentLed->startTime + currentLed->duration)
			{
				setLed((led_e)i, onOff_e::off);
				cmdArray[i].pop();
				delete currentLed;
			}
			break;
		default:
			break;
		}
	}
	updateShiftRegister(bitPatern);
}

/*
 * updateShiftRegister() - This function sets the latchPin to low, then calls the Arduino function 'shiftOut' to shift out contents of variable 'leds' in the shift register before putting the 'latchPin' high again.
 */
void CircuitControll::updateShiftRegister(byte leds)
{
	//Serial.println("Shift out byte: " + (String)leds);
	leds ^= 0xff;
	shiftOut(PIN_DATA, PIN_CLOCK, MSBFIRST, leds);

	digitalWrite(PIN_LATCH, HIGH);
	digitalWrite(PIN_LATCH, LOW);
}

void CircuitControll::deleteLedQueue(led_e led)
{
	while (!cmdArray[led].empty())
	{
		delete cmdArray[led].front();
		cmdArray[led].pop();
	}
}

void CircuitControll::addLedCmd(led_e led, onOff_e value, int dur)
{
	//Serial.println("add to led queue:" + (String)led + ", " + (String)value + ", " + (String)dur);
	ledCmd *cmd = new ledCmd(value, dur);
	deleteLedQueue(led);
	cmdArray[led].push(cmd);
}

void CircuitControll::addLedCmdArrayToQueue(led_e led, const ledCmd queue[], int lenght)
{
	deleteLedQueue(led);
	for (int i = 0; i < lenght; i++)
	{
		ledCmd* cmd = new ledCmd(queue[i].onOff, queue[i].duration);
		cmdArray[led].push(cmd);
	}
}


