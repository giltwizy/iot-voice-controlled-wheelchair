#include<Wire.h>
#include<LiquidCrystal_I2C.h>

//Definitions of the Digital pins used

#define trigPin = 10; //trigPin is the Ultrasonic Sensor HC-SR04 trigger pin
#define echoPin = 13; //echoPin is the Ultrasonic Sensor HC-SR04 echo pin
#define leftMotor = 12;
#define rightMotor = 11;
#define buzzer=4;

//Declaration of the variables that will be globally used

float duration,distance;  //Duration and distance are determined by the Ultrasonic Sensor HC-SR04
LiquidCrystal_I2C lcd(0x3F,2,1,0,4,5,6,7,3,POSITIVE);
String voice;

void setup() {

  //Ultrasonic Sensor HC-SR04 pins initialization
  pinMode(trigPin,OUTPUT);
  pinMode(echoPin,INPUT);

  //LCD I2C initialization
  lcd.begin(16,2);
  lcd.clear();
  lcd.print("READY");
  delay(5000);
  lcd.print("TELL ME YOUR DIRECTION");
  
  Serial.begin(9600);
  pinMode(leftMotor, OUTPUT);
  pinMode(rightMotor, OUTPUT);
  
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


//User defined functions

void goForward(){
   lcd.clear();
   lcd.print("Going Foward..");
   digitalWrite(leftMotor,HIGH);
   digitalWrite(rightMotor,HIGH);
  }


void goBack(){  
  if (distance <= 30) {
    obstacleStop();
  }
  else{
    lcd.clear();
    lcd.print("Going backward..");
    digitalWrite(leftMotor,HIGH); //reverse polarity
    digitalWrite(rightMotor,HIGH); //reverse polarity    
  }

void goLeft(){
  lcd.clear();
  lcd.print("Going left..");
  digitalWrite(leftMotor,HIGH); //reverse polarity
  digitalWrite(rightMotor,HIGH);
  delay(2000);
  stopmoving();  
}


void goRight(){
  lcd.clear();
  lcd.print("Going right..");
  digitalWrite(leftMotor,HIGH);
  digitalWrite(rightMotor,HIGH); //reverse polarity
  delay(2000);
  stopmoving();
}


void stopmoving(){
  lcd.clear();
  lcd.print("Stopping..");
  digitalWrite(leftMotor,LOW);
  digitalWrite(rightMotor,LOW);
}


void obstacleStop(){
  lcd.clear();
  lcd.print("Obstacle presence..");
  digitalWrite(leftMotor,LOW);
  digitalWrite(rightMotor,LOW);
  tone(buzzer,450);
  delay(1000);
  noTone(buzzer);
  delay(2000);  
}


void wrongCommand(){
  lcd.clear();
  lcd.print("WRONG COMMAND,TRY AGAIN..");
  tone(buzzer,450);
  delay(1000);
  noTone(buzzer);
  delay(2000);
}
