/*
 Name:		HardwareControllSystem.ino
 Created:	9/13/2019 8:53:20 AM
 Author:	Pontus
*/

// the setup function runs once when you press reset or power the board

/*#include <require_cpp11.h>
#include <MFRC522Extended.h>
#include <deprecated.h>*/
#include <SPI.h>
#include <MFRC522.h>

#define SS_PIN 10
#define RST_PIN 9
MFRC522 mfrc522(SS_PIN, RST_PIN);  // Create MFRC522 instance

int first_LED = 8; 
int second_LED = 9;
int third_LED = 10;
int state = 0;

int buzzer_Pin = 6;


void setup() {


	Serial.begin(9600);		// Initialize serial communications with the PC
	while (!Serial);		// Do nothing if no serial port is opened (added for Arduinos based on ATMEGA32U4)
	SPI.begin();			// Init SPI bus
	mfrc522.PCD_Init();		// Init MFRC522
	mfrc522.PCD_DumpVersionToSerial();	// Show details of PCD - MFRC522 Card Reader details
	Serial.println(F("Scan PICC to see UID, SAK, type, and data blocks..."));
}

void loop()
{
	// Look for new cards
	if (!mfrc522.PICC_IsNewCardPresent())
	{
		return;
	}
	// Select one of the cards
	if (!mfrc522.PICC_ReadCardSerial())
	{
		return;
	}

	tone(buzzer_Pin, 1000, 250);
	//Show UID on serial monitor
	Serial.print("UID tag :");
	String content = "";
	byte letter;
	for (byte i = 0; i < mfrc522.uid.size; i++)
	{
		Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
		Serial.print(mfrc522.uid.uidByte[i], HEX);
		content.concat(String(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "));
		content.concat(String(mfrc522.uid.uidByte[i], HEX));
	}
	Serial.println();
	Serial.print("Message : ");
	content.toUpperCase();
	if (content.substring(1) == "A6 53 9F F7") //change here the UID of the card/cards that you want to give access
	{
		Serial.println("Authorized access");
		Serial.println();
		delay(3000);
	}

	else {
		Serial.println(" Access denied");
		delay(3000);
	}
}


/*void loop() {
	// Reset the loop if no new card present on the sensor/reader. This saves the entire process when idle.
	if (!mfrc522.PICC_IsNewCardPresent()) {
		return;
	}

	// Select one of the cards
	if (!mfrc522.PICC_ReadCardSerial()) {
		return;
	}
	
	tone(buzzer_Pin, 2000,250);
	// Dump debug info about the card; PICC_HaltA() is automatically called
	mfrc522.PICC_DumpToSerial(&(mfrc522.uid));
}*/

/*void setup() {
	// sets the pins as outputs:
	pinMode(third_LED, OUTPUT);
	pinMode(second_LED, OUTPUT);
	pinMode(first_LED, OUTPUT);
	Serial.begin(9600);		

}

void loop() {
	//if some date is sent, reads it and saves in state
	if (Serial.available() > 0)
		state = Serial.read();

	if (state == '1')
		digitalWrite(first_LED, LOW);

	else if (state == '2')
		digitalWrite(second_LED, LOW);

	else if (state == '3')
		digitalWrite(third_LED, LOW);


	else if (state == '0')
	{
		digitalWrite(third_LED, HIGH);
		digitalWrite(second_LED, HIGH);
		digitalWrite(first_LED, HIGH);
	}
}
*/