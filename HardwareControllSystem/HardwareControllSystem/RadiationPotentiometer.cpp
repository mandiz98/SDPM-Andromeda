#include "RadiationPotentiometer.h"

RadiationPotentiometer::RadiationPotentiometer(){}

void RadiationPotentiometer::run()
{
	readRadValue();

	if (5 > abs(m_mappedValue - m_oldValue)) // if not big enough change, do nothing, so not to spam
	{
		return;
	}
	m_oldValue = m_mappedValue;

	m_callback(m_mappedValue);
}

void RadiationPotentiometer::setValueChangeCallback(void(*m_callback)(float))
{
	this->m_callback = m_callback;
}

void RadiationPotentiometer::readRadValue()
{
	int m_rawValue = analogRead(PIN_ANALOGRADREAD);
	map(m_rawValue, 0, 1023, 0, 100);
	m_mappedValue = m_rawValue;
	//m_procentualValue = ((float)m_rawValue / 1023); // convert the raw value to something more more readable
}

