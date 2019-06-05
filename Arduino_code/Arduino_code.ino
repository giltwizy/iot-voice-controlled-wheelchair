#include<Wire.h>
#include<LiquidCrystal_I2C.h>

//Definitions of the Digital pins used

#define trigPin 12 //trigPin is the Ultrasonic Sensor HC-SR04 trigger pin
#define echoPin 13 //echoPin is the Ultrasonic Sensor HC-SR04 echo pin
#define buzzer 10

// Left Motor
int leftMotorSpeed = 9;
int leftMotorA = 8;
int leftMotorB = 7;

// Right Motor
int rightMotorSpeed = 6;
int rightMotorA = 5;
int rightMotorB = 4;

//Declaration of the variables that will be globally used

float duration,distance;  //Duration and distance are determined by the Ultrasonic Sensor HC-SR04
LiquidCrystal_I2C lcd(0x3F,2,1,0,4,5,6,7,3,POSITIVE);
String voice;

//User defined functions

void goForward(){
   lcd.clear();
   lcd.print("Going Foward..");
   // turn on motor A
  digitalWrite(leftMotorA, HIGH);
  digitalWrite(leftMotorB, LOW);
  // set speed to 200 out of possible range 0~255
  analogWrite(leftMotorSpeed, 200);
  digitalWrite(rightMotorA, HIGH);
  digitalWrite(rightMotorB, LOW);
  // set speed to 200 out of possible range 0~255
  analogWrite(rightMotorSpeed, 200);
  }


void obstacleStop(){
  lcd.clear();
  lcd.print("Obstacle presence..");
  digitalWrite(leftMotorA, LOW);
  digitalWrite(leftMotorB, LOW); 
  digitalWrite(rightMotorA, LOW);
  digitalWrite(rightMotorB, LOW);
  tone(buzzer,450);
  delay(1000);
  noTone(buzzer);
  delay(2000);  
}


void goBack(){  
  if (distance <= 30) {
    obstacleStop();
  }
  else{
    lcd.clear();
    lcd.print("Going backward..");
    digitalWrite(leftMotorA, LOW);
    digitalWrite(leftMotorB, HIGH); 
    digitalWrite(rightMotorA, LOW);
    digitalWrite(rightMotorB, HIGH);    
  }
}


void stopMoving(){
  lcd.clear();
  lcd.print("Stopping..");
  digitalWrite(leftMotorA, LOW);
  digitalWrite(leftMotorB, LOW); 
  digitalWrite(rightMotorA, LOW);
  digitalWrite(rightMotorB, LOW);
}


void goLeft(){
  lcd.clear();
  lcd.print("Going left..");
  digitalWrite(leftMotorA, LOW);
  digitalWrite(leftMotorB, HIGH);
  digitalWrite(rightMotorA, HIGH);
  digitalWrite(rightMotorB, LOW);
  delay(2000);
  stopMoving();  
}


void goRight(){
  lcd.clear();
  lcd.print("Going right..");
  digitalWrite(leftMotorA, HIGH);
  digitalWrite(leftMotorB, LOW);
  digitalWrite(rightMotorA, LOW);
  digitalWrite(rightMotorB, HIGH);
  delay(2000);
  stopMoving();
}


void wrongCommand(){
  lcd.clear();
  lcd.print("WRONG COMMAND,TRY AGAIN..");
  tone(buzzer,450);
  delay(1000);
  noTone(buzzer);
  delay(2000);
}


void setup() {
  
  Serial.begin(9600);

  //Ultrasonic Sensor HC-SR04 pins initialization
  pinMode(trigPin,OUTPUT);
  pinMode(echoPin,INPUT);

  //LCD I2C initialization
  lcd.begin(16,2);
  lcd.clear();
  lcd.print("READY");
  delay(5000);
  lcd.clear();
  lcd.print("TELL ME YOUR DIRECTION");  
  
  pinMode(leftMotorSpeed, OUTPUT);
  pinMode(rightMotorSpeed, OUTPUT);
  pinMode(leftMotorA, OUTPUT);
  pinMode(leftMotorB, OUTPUT);
  pinMode(rightMotorA, OUTPUT);
  pinMode(rightMotorB, OUTPUT);  
}


void loop() {

// Write a pulse to the HC-SR04 Trigger Pin
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  // Measure the response from the HC-SR04 Echo Pin
  duration = pulseIn(echoPin, HIGH);
  
  // Determine distance from duration
  // Use 343 metres per second as speed of sound
  
  distance = (duration / 2) * 0.0343;

  while(Serial.available()) {
  delay(10);
  char c=Serial.read();
  if(c=='#'){
      break;
    }
  voice += c;
  }

  if(voice.length() > 0){
  Serial.println(voice);
  if(voice == "go forward" || voice == "forward" || voice == "ford"|| voice == "beforward" || voice == "bailey" ||voice == "barry"  || voice == "bella" || voice == "belly" || voice == "trendy berry" || voice == "twin dembele" || voice == "10 dembele")
    {
      goForward() ;
    }
  else if(voice =="go back" || voice =="back"){
      goBack();
    }
  else if(voice =="go left" || voice == "turn left" || voice == "left" || voice =="kushoto" || voice == "nenda kushoto" || voice == "kata kushoto"){
      goLeft();
    }
  else if(voice =="go right" || voice =="turn right"  || voice == "right" || voice == "kulia" || voice =="coolio" || voice == "courier" || voice == "qualia" || voice == "colyer" || voice == "korea" || voice == "clear" || voice == "coolie"){
      goRight();
    }
  else if( voice =="stop" || voice == "si mama" ){
      stopMoving();
    }
  else
  {
    wrongCommand();
  }
  
  voice="";
  }
  }
