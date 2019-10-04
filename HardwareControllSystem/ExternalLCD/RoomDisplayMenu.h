#pragma once
#include "Menu.h"

class RoomDisplayMenu :
	public Menu
{
public:
	RoomDisplayMenu();
	~RoomDisplayMenu();

	virtual void print();
	//virtual void run();

	//to call when this menu is to be used
	virtual void activate();
	//to be callen when this menu is no longer in use
	//virtual void deactivate();
};

