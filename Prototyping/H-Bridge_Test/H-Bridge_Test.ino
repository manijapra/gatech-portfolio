int motorOneInOne = 11;
int motorOneInTwo = 12;
int motorOneEnable = 10;

int motorTwoInOne = 14;
int motorTwoInTwo = 15;
int motorTwoEnable = 16;

int ledPin = 13;

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
}

void loop() {
  // put your main code here, to run repeatedly:
  
  digitalWrite(motorOneEnable, HIGH);
  digitalWrite(motorTwoEnable, HIGH);
  delay(2000);
  
  digitalWrite(motorOneEnable, LOW);
  digitalWrite(motorTwoEnable, LOW);
  delay(2000);
}
