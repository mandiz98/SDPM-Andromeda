#pragma once
#include <stdlib.h>
#include<ArduinoSTL.h>
#include <vector>
#include "Menu.h"
#include <LiquidCrystal.h>

using namespace std;
class MenuEngine
{
public:
	//static MenuEngine getInstance();
	//MenuEngine();
	~MenuEngine();
	
	//void init(LiquidCrystal lcd);
	//void run();

	//void openMenu(Menu menu);
	//void closeMenu();

	//LiquidCrystal getLCD();
	
private:

	LiquidCrystal lcd;
	//vector<Menu> menuStack;

};

