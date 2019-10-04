#pragma once

#include <LiquidCrystal.h>

class AbsMenu
{
public:
	 AbsMenu();
	~AbsMenu();

	//prints the menu to the lcd
	virtual void print();
	//main state machine for runing the menu
	virtual void run();

	//to call when this menu is to be used
	virtual void activate();
	//to be callen when this menu is no longer in use
	virtual void deactivate();
protected:
	//call when to open a new menu
	void open(AbsMenu menu);
	//call when to exit this menu. NOTE. root menu can't exit.
	void exit();


private:


};

