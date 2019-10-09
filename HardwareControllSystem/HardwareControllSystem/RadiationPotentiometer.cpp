#include "RadiationPotentiometer.h"

RadiationPotentiometer::RadiationPotentiometer(){}

void RadiationPotentiometer::run()
{
	//int temp = m_procentualValue;
	//readRadValue();
	////(m_oldValue != m_procentualValue
	//if (0.05 < abs(m_oldValue - m_procentualValue))
	//{
	//	m_callback(m_procentualValue);
	//	m_oldValue = temp;
	//	//Serial.println("Procentualblalalla: " + (String)m_procentualValue);
	//}

	readRadValue();

	if (0.01 > abs(m_procentualValue - m_oldValue))
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
	m_procentualValue = ((float)m_rawValue / 1023);
}

