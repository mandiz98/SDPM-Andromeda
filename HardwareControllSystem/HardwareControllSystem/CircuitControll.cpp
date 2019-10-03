#include "CircuitControll.h"

CircuitControll::CircuitControll(){}

void CircuitControll::runBuzzer()
{
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
		Serial.print("BUZZ: " + (String)toneQueue.size());
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
	toneQueue.push(tone);
}

void CircuitControll::addArrayToQueue(toneCmd queue[], int lenght)
{
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

//void CircuitControll::setLed(ledColor_e color, onOff_e onOff)
//{
//	byte myByte = 0;
//	bitWrite(myByte, (int)color, onOff);
//	updateShiftRegister(myByte);
//}

void CircuitControll::setLed(led_e pos, onOff_e onOff)
{
	bitWrite(bitPatern, pos, onOff);

}

void CircuitControll::runLeds()
{
	//Serial.println("runLeds");
	for (ledCmdQueue::size_type i = 0; i < ledArraySize_c; i++)
	{
		if (cmdArray[i].empty())
			continue;
		//Serial.println(" Led: " + (String)i);

		ledCmd *currentLed = cmdArray[i].front();

		switch (currentLed->state)
		{
		case ledCmd::state_e::ready:

			currentLed->startTime = millis();
			setLed((led_e)i, currentLed->onOff);
			currentLed->state = ledCmd::state_e::running;
			//Serial.println("Led " + (String)i + " "+ (String)currentLed->onOff);
			break;
		case ledCmd::state_e::running:
			//Serial.println("running");
			if (millis() > currentLed->startTime + currentLed->duration)
			{
				cmdArray[i].pop();
				delete currentLed;
			}

			break;

		default:
			break;
		}
	}
	updateShiftRegister(bitPatern);
	//Serial.println("bitPatern: " + (String)bitPatern);

}

/*
 * updateShiftRegister() - This function sets the latchPin to low, then calls the Arduino function 'shiftOut' to shift out contents of variable 'leds' in the shift register before putting the 'latchPin' high again.
 */
void CircuitControll::updateShiftRegister(byte leds)
{
	digitalWrite(PIN_LATCH, LOW);
	shiftOut(PIN_DATA, PIN_CLOCK, LSBFIRST, leds);
	digitalWrite(PIN_LATCH, HIGH);
}

void CircuitControll::addLedCmd(led_e led, onOff_e value, int dur)
{
	ledCmd *cmd = new ledCmd(value, dur);

	cmdArray[led].push(cmd);
}


