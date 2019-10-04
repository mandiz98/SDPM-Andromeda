#include "ButtonReader.h"



ButtonReader::ButtonReader()
{
}


ButtonReader *ButtonReader::getInstance()
{
	static ButtonReader *instance = nullptr;
	if (instance == nullptr)
		instance = new ButtonReader();

	return instance;
}

ButtonReader::~ButtonReader()
{
}
