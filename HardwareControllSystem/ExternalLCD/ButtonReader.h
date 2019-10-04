#pragma once
class ButtonReader
{
public:
	ButtonReader *getInstance();
	~ButtonReader();

	enum button_e
	{
		button_none,
		button_select,
		button_up,
		button_down,
		button_left,
		button_right,
	};

private:
	ButtonReader();

};

