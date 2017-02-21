#include <SoftwareSerial.h>

int motorOneInOne = 11;
int motorOneInTwo = 12;
int motorOneEnable = 10;

int motorTwoInOne = 14;
int motorTwoInTwo = 15;
int motorTwoEnable = 16;

SoftwareSerial BLUEFRUIT(0, 1);

char buffer[5];
int bufferLoc = 0;

int ledPin = 13;

int state = 0;

void setup() {
  // put your setup code here, to run once:
  pinMode(motorOneInOne, OUTPUT);
  pinMode(motorOneInTwo, OUTPUT);
  pinMode(motorOneEnable, OUTPUT);

  pinMode(motorTwoInOne, OUTPUT);
  pinMode(motorTwoInTwo, OUTPUT);
  pinMode(motorTwoEnable, OUTPUT);
  
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, HIGH);
  
  digitalWrite(motorOneInOne, LOW);
  digitalWrite(motorOneInTwo, HIGH);

  digitalWrite(motorTwoInOne, LOW);
  digitalWrite(motorTwoInTwo, HIGH);
  
  Serial.begin(9600);
  while (!Serial);
  
  BLUEFRUIT.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (BLUEFRUIT.available() > 0)
  {
    char rec = BLUEFRUIT.read();
    if (rec == '!')
    {
      bufferLoc = 0;
    }
    buffer[bufferLoc] = rec;
    bufferLoc++;
    if (bufferLoc > sizeof(buffer) - 1)
    {
      parseButton();
    }
  }
  if (state == 1)
  {
    digitalWrite(motorOneEnable, HIGH);
    digitalWrite(motorTwoEnable, HIGH);
  }
  else if (state == 0)
  {
    digitalWrite(motorOneEnable, LOW);
    digitalWrite(motorTwoEnable, LOW);
  }
  delay(10);
}

void parseButton()
{
  if (buffer[1] == 'B') {
    char buttnum = buffer[2]; //UP - 5, DOWN - 6, LEFT - 7, RIGHT - 8
    boolean pressed = buffer[3] - '0';

    if (buttnum == '5')
    {
      if (pressed) 
      {
        digitalWrite(motorOneInOne, HIGH);
        digitalWrite(motorOneInTwo, LOW);
      
        digitalWrite(motorTwoInOne, HIGH);
        digitalWrite(motorTwoInTwo, LOW);
        state = 1;
      }
      else state = 0;
    }
    else if (buttnum == '6')
    {
      if (pressed) 
      {
        digitalWrite(motorOneInOne, LOW);
        digitalWrite(motorOneInTwo, HIGH);
      
        digitalWrite(motorTwoInOne, LOW);
        digitalWrite(motorTwoInTwo, HIGH);
        state = 1;
      }
      else state = 0;
    }
    else if (buttnum == '7')
    {
      if (pressed) 
      {
        digitalWrite(motorOneInOne, LOW);
        digitalWrite(motorOneInTwo, HIGH);
      
        digitalWrite(motorTwoInOne, HIGH);
        digitalWrite(motorTwoInTwo, LOW);
        state = 1;
      }
      else state = 0;
    }
    else if (buttnum == '8')
    {
      if (pressed) 
      {
        digitalWrite(motorOneInOne, HIGH);
        digitalWrite(motorOneInTwo, LOW);
      
        digitalWrite(motorTwoInOne, LOW);
        digitalWrite(motorTwoInTwo, HIGH);
        state = 1;
      }
      else state = 0;
    }
    
  }
  else
  {
    state = 0;
  }
}
