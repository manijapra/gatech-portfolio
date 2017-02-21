#include <SoftwareSerial.h>
#include <PID_v1.h>

//DC Motor Right
int motorOneInOne = 11;
int motorOneInTwo = 12;
int motorOneEnable = 10;

//DC Motor Left
int motorTwoInOne = 14;
int motorTwoInTwo = 15;
int motorTwoEnable = 20;

//BluetoothLE
SoftwareSerial BLUEFRUIT(0, 1);

char buffer[5];
unsigned int bufferLoc = 0;

//LED
int ledPin = 13;

//PhotoGates
int lGate = 23;
int rGate = 22;

boolean rGatePrev = false;
boolean lGatePrev = false;

int state = 0;
boolean usePID = false;

//PID
double lSetpoint, lInput, lOutput;
double rSetpoint, rInput, rOutput;

PID leftPID(&lInput, &lOutput, &lSetpoint, 40, 12, 0.30, DIRECT);
PID rightPID(&rInput, &rOutput, &rSetpoint, 20, 2.5, 0.3, DIRECT);

void setup() {
  delay(5000);
  // put your setup code here, to run once:
  //DC Motors
  pinMode(motorOneInOne, OUTPUT);
  pinMode(motorOneInTwo, OUTPUT);
  pinMode(motorOneEnable, OUTPUT);

  pinMode(motorTwoInOne, OUTPUT);
  pinMode(motorTwoInTwo, OUTPUT);
  pinMode(motorTwoEnable, OUTPUT);

  //PhotoGates
  pinMode(lGate, INPUT);
  pinMode(rGate, INPUT);

  //LED
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, HIGH);

  //DC Motor Initialize H-Bridge Direction
  digitalWrite(motorOneInOne, LOW);
  digitalWrite(motorOneInTwo, HIGH);

  digitalWrite(motorTwoInOne, LOW);
  digitalWrite(motorTwoInTwo, HIGH);

  //Initialize PID on both Motors
  lInput = checkLeftRotations(40);
  lSetpoint = 0;
  leftPID.SetMode(MANUAL);
  leftPID.SetOutputLimits(0, 255);

  rInput = checkRightRotations(40);
  rSetpoint = 0;
  rightPID.SetMode(MANUAL);
  rightPID.SetOutputLimits(0, 255);

  //Begin Bluetooth LE
  BLUEFRUIT.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:

  //Parse BLE UART Packets Received
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
    
  if (usePID)
  {
    //State Machine (1 - Power motors, 0 - Stop)
    if (state == 1)
    {
      lSetpoint = 3;
      rSetpoint = 3;
    }
    else if (state == 0)
    {
      lSetpoint = 0;
      rSetpoint = 0;
    }
    lInput = checkLeftRotations(40);
    rInput = checkRightRotations(40);
    leftPID.Compute();
    rightPID.Compute();
    analogWrite(motorOneEnable, lOutput);
    analogWrite(motorTwoEnable, rOutput);
    delay(10);
  }
  else
  {
    if (state == 1)
    {
      analogWrite(motorOneEnable, 250);
      analogWrite(motorTwoEnable, 250);
    }
    else if (state == 0)
    {
      analogWrite(motorOneEnable, 0);
      analogWrite(motorTwoEnable, 0);
    }
  }
}

int checkLeftRotations(unsigned int milliseconds)
{
  unsigned int startTime = millis();
  int lNumCount = 0;
  while ((millis() - startTime) != milliseconds)
  {
    if (digitalRead(lGate) && !lGatePrev) 
    {
      lNumCount++;
      lGatePrev = true;
    }
    else if (!digitalRead(lGate) && lGatePrev)
    {
      lGatePrev = false;
    }
  }

  return lNumCount;
}

int checkRightRotations(unsigned int milliseconds)
{
  unsigned int startTime = millis();
  int rNumCount = 0;
  while ((millis() - startTime) != milliseconds)
  {
    if (digitalRead(rGate) && !rGatePrev) 
    {
      rNumCount++;
      rGatePrev = true;
    }
    else if (!digitalRead(rGate) && rGatePrev)
    {
      rGatePrev = false;
    }
  }
  
  return rNumCount;
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
    else if (buttnum == '1')
    {
      if (pressed) 
      {
        if (usePID) 
        {
          usePID = false;
          rightPID.SetMode(MANUAL);
          leftPID.SetMode(MANUAL);
        }
        else 
        {
          usePID = true;
          rightPID.SetMode(AUTOMATIC);
          leftPID.SetMode(AUTOMATIC);
        }
      }
    }
  }
  else
  {
    state = 0;
  }
}

