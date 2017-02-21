#include <PID_v1.h>

int motorOneInOne = 11;
int motorOneInTwo = 12;
int motorOneEnable = 10;

int motorTwoInOne = 14;
int motorTwoInTwo = 15;
int motorTwoEnable = 20;

int ledPin = 13;

int lGate = 23;
int rGate = 22;

int rNumCount = 0;
int lNumCount = 0;

boolean rGatePrev = false;
boolean lGatePrev = false;

//PID
double lSetpoint, lInput, lOutput;
double rSetpoint, rInput, rOutput;

PID leftPID(&lInput, &lOutput, &lSetpoint, 70, 20, 0.30, DIRECT);
PID rightPID(&rInput, &rOutput, &rSetpoint, 50, 5, 0.3, DIRECT);

void setup() {
  // put your setup code here, to run once:
  pinMode(motorOneInOne, OUTPUT);
  pinMode(motorOneInTwo, OUTPUT);
  pinMode(motorOneEnable, OUTPUT);

  pinMode(motorTwoInOne, OUTPUT);
  pinMode(motorTwoInTwo, OUTPUT);
  pinMode(motorTwoEnable, OUTPUT);

  pinMode(lGate, INPUT);
  pinMode(rGate, INPUT);

  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, HIGH);

  digitalWrite(motorOneInOne, LOW);
  digitalWrite(motorOneInTwo, HIGH);

  digitalWrite(motorTwoInOne, LOW);
  digitalWrite(motorTwoInTwo, HIGH);

  lInput = checkLeftRotations(40);
  lSetpoint = 3;
  leftPID.SetMode(AUTOMATIC);
  leftPID.SetOutputLimits(0, 255);

  rInput = checkRightRotations(40);
  rSetpoint = 3;
  rightPID.SetMode(AUTOMATIC);
  rightPID.SetOutputLimits(0, 255);

  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  lInput = checkLeftRotations(40);
  rInput = checkRightRotations(40);
  Serial.print("Left: "); Serial.print(lInput);
  Serial.print(" Right: "); Serial.print(rInput);
  leftPID.Compute();
  rightPID.Compute();
  Serial.print(" Left: "); Serial.print(lOutput); Serial.print(" Right: "); Serial.println(rOutput);
  analogWrite(motorOneEnable, lOutput);
  analogWrite(motorTwoEnable, rOutput);
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

