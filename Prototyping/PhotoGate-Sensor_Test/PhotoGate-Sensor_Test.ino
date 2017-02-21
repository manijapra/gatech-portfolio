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

  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  analogWrite(motorOneEnable, 250);
  analogWrite(motorTwoEnable, 250);

  if (digitalRead(rGate) && !rGatePrev) 
  {
    rNumCount++;
    rGatePrev = true;
  }
  else if (!digitalRead(rGate) && rGatePrev)
  {
    rGatePrev = false;
  }
  
  if (digitalRead(lGate) && !lGatePrev) 
  {
    lNumCount++;
    lGatePrev = true;
  }
  else if (!digitalRead(lGate) && lGatePrev)
  {
    lGatePrev = false;
  }

  if ((millis() % 200) <= 10) 
  {
    Serial.print("Right: "); Serial.println(rNumCount);
    Serial.print("Left: "); Serial.println(lNumCount);
    lNumCount = 0;
    rNumCount = 0;
    delay(100);
  }
}
