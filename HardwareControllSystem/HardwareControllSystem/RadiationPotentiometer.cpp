#include "RadiationPotentiometer.h"

RadiationPotentiometer::RadiationPotentiometer(){}

void RadiationPotentiometer::run()
{
	readRadValue();

	if (0.02 > abs(m_procentualValue - m_oldValue)) // if not big enough change, do nothing, so not to spam
	{
		return;
	}
	m_oldValue = m_procentualValue;

	m_callback(m_procentualValue);
}

void RadiationPotentiometer::setValueChangeCallback(void(*m_callback)(float))
{
	this->m_callback = m_callback;
}

void RadiationPotentiometer::readRadValue()
{
	int m_rawValue = analogRead(PIN_ANALOGRADREAD);
	m_procentualValue = ((float)m_rawValue / 1023); // convert the raw value to something more more readable
}

