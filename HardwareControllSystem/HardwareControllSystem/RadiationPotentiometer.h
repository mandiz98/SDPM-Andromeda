#pragma once
#include "Arduino.h"
class RadiationPotentiometer
{
#define PIN_ANALOGRADREAD A3
public:
	RadiationPotentiometer();

	void run();

	void setValueChangeCallback(void (*m_callback)(float));

private:

	void (*m_callback)(float);

	void readRadValue();

	float m_procentualValue;
	float m_oldValue;

};

