#pragma once

#include <ArduinoSTL.h>
#include <stack>
#include "MenuEngine.h"

class Menu
{
public:
	Menu();
	~Menu();

	//prints the menu to the lcd
	virtual void print();
	//main state machine for runing the menu
	virtual void run();

	//to call when this menu is to be used
	virtual void activate();
	//to be callen when this menu is no longer in use
	virtual void deactivate();

	//call when to open a new menu
	void open(Menu menu);
	//call when to exit this menu. NOTE. root menu can't exit.
	void exit();

};

