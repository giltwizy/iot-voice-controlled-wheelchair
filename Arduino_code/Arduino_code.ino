#include<Wire.h>
#include<LiquidCrystal_I2C.h>
#include <Ultrasonic.h>
//Definitions of the Digital pins used

Ultrasonic ultrasonic1(2, 3);
Ultrasonic ultrasonic2(5, 6);
#define buzzer 7

// Left Motor
int leftMotorSpeed = 8;
int leftMotorA = 10;
int leftMotorB = 9;
// Right Motor
int rightMotorSpeed = 11;
int rightMotorA = 13;
int rightMotorB = 12;
char x;
//Declaration of the variables that will be globally used

#define right A0
#define left A3
#define forward A2
#define stop_ A1

float duration, distance; //Duration and distance are determined by the Ultrasonic Sensor HC-SR04
LiquidCrystal_I2C lcd(0x27, 16, 2);
String voice;

//User defined functions

void obstacleStop() {
  lcd.clear();
  lcd.print("Obstacle presence..");
  digitalWrite(leftMotorA, LOW);
  digitalWrite(leftMotorB, LOW);
  digitalWrite(rightMotorA, LOW);
  digitalWrite(rightMotorB, LOW);
  tone(buzzer, 1000);
  // delay(1500);
  //noTone(buzzer);
  //delay(2000);
}



void goForward() {
  lcd.clear();
  lcd.print("Going Foward..");
  // turn on motor A
  digitalWrite(leftMotorA, HIGH);
  digitalWrite(leftMotorB, LOW);
  // set speed to 200 out of possible range 0~255
  analogWrite(leftMotorSpeed, 160);
  digitalWrite(rightMotorA, HIGH);
  digitalWrite(rightMotorB, LOW);
  // set speed to 200 out of possible range 0~255
  analogWrite(rightMotorSpeed, 170);
}

void stopMoving() {
  lcd.clear();
  lcd.print("Stopping..");
  analogWrite(rightMotorSpeed, 0);
  analogWrite(leftMotorSpeed, 0);
  digitalWrite(leftMotorA, LOW);
  digitalWrite(leftMotorB, LOW);
  digitalWrite(rightMotorA, LOW);
  digitalWrite(rightMotorB, LOW);
}


void goBack() {
  lcd.clear();
  lcd.print("Going backward..");
  analogWrite(rightMotorSpeed, 150);
  analogWrite(leftMotorSpeed, 150);
  digitalWrite(leftMotorA, LOW);
  digitalWrite(leftMotorB, HIGH);
  digitalWrite(rightMotorA, LOW);
  digitalWrite(rightMotorB, HIGH);
}


void goLeft() {
  lcd.clear();
  lcd.print("Going left..");
  digitalWrite(leftMotorA, LOW);
  digitalWrite(leftMotorB, HIGH);
  digitalWrite(rightMotorA, HIGH);
  digitalWrite(rightMotorB, LOW);
  analogWrite(rightMotorSpeed, 150);
  analogWrite(leftMotorSpeed, 150);
  delay(1000);
  stopMoving();
}


void goRight() {
  lcd.clear();
  lcd.print("Going right..");
  digitalWrite(leftMotorA, HIGH);
  digitalWrite(leftMotorB, LOW);
  digitalWrite(rightMotorA, LOW);
  digitalWrite(rightMotorB, HIGH);
  analogWrite(rightMotorSpeed, 150);
  analogWrite(leftMotorSpeed, 150);
  delay(2000);
  stopMoving();
}





void wrongCommand() {
  lcd.clear();
  lcd.print("WRONG COMMAND,TRY AGAIN..");
  tone(buzzer, 450);
  delay(1000);
  noTone(buzzer);
  delay(2000);
}

void setup() {

  Serial.begin(9600);
  Serial.println(" Salaam");
  lcd.begin();
  lcd.clear();
  lcd.print("READY");
  delay(5000);
  lcd.clear();
  lcd.print("TELL ME YOUR DIRECTION");


  //pinMode(leftMotorSpeed, OUTPUT);
  //pinMode(rightMotorSpeed, OUTPUT);
  pinMode(leftMotorA, OUTPUT);
  pinMode(leftMotorB, OUTPUT);
  pinMode(rightMotorA, OUTPUT);
  pinMode(rightMotorB, OUTPUT);

  pinMode(right, INPUT_PULLUP);
  pinMode(left, INPUT_PULLUP);
  pinMode(forward, INPUT_PULLUP);
  pinMode(stop_, INPUT_PULLUP);

}


void loop() {
  obstacle();
  if (digitalRead(right) == HIGH || digitalRead(left) == HIGH
      || digitalRead(forward) == HIGH || digitalRead(stop_) == HIGH) {
    button_();
  } else {
    obstacle();
    while (Serial.available()) {
      delay(10);
      char c = Serial.read();
      if (c == '#') {
        break;
      }
      voice += c;
    }
  }


  if (voice.length() > 0) {
    Serial.println(voice.length());
    obstacle();
    if (voice == "go forward" || voice == "go front" ||
        voice == "will ford" || voice == "go for it" ||
        voice == "will forward" || voice == "go fly" ||
        voice == "guilford" || voice == "millford" ||
        voice == "well ford" || voice == "go for ride" ||
        voice == "well for ride" || voice == "forward" ||
        voice == "ford" || voice == "beforward" || voice == "bailey" || voice == "barry"  ||
        voice == "bella" || voice == "belly" || voice == "trendy berry" || voice == "twin dembele" ||
        voice == "10 dembele" || voice == "x")
    {
      x = 'A';
      Serial.println(x);
      obstacle();
      goForward() ;
    }
    else if (voice == "go back" || voice == "will back" || voice == "call back" || voice == "back") {
      x = 'B';
      Serial.println(x);
      obstacle();
      goBack();
    }
    else if (voice == "go left" || voice == "turn left" || voice == "left" || voice == "kushoto" || voice == "nenda kushoto" || voice == "kata kushoto") {
      x = 'C';
      Serial.println(x);
      goRight();
    }
    else if (voice == "go right" || voice == "turn right"  || voice == "girl right" || voice == "right" || voice == "kulia" || voice == "coolio" || voice == "courier" || voice == "qualia" || voice == "colyer" || voice == "korea" || voice == "clear" || voice == "coolie") {
      x = 'D';
      goLeft();
    }
    else if ( voice == "stop" || voice == "si mama" ) {
      x = 'V';
      stopMoving();
    }
    else
    {
      wrongCommand();
      //button_();
    }


    voice = "";
  }
}

void obstacle() {
  Serial.println("hr");
  if (ultrasonic1.Ranging(CM) <= 30 && x == 'A') {
    obstacleStop(); Serial.println("StopED");
  } else if (ultrasonic1.Ranging(CM) >= 31 && x == 'A') {
    goForward(); noTone(buzzer);
  }
  else if (ultrasonic2.Ranging(CM) <= 30 && x == 'B') {
    obstacleStop(); Serial.println("Stop");
  } else if (ultrasonic2.Ranging(CM) >= 31 && x == 'B') {
    goBack(); noTone(buzzer);
  }
  else {
    noTone(buzzer);
  }
}

void button_() {
  Serial.println("Here");
  if (digitalRead(right) == HIGH) {
    x = 'V';
    goLeft();

  }
  if (digitalRead(left) == HIGH) {
    x = 'V';
    goRight();
  }
  if (digitalRead(forward) == HIGH) {
    x = 'A'; obstacle();
    goForward();
  }
  if (digitalRead(stop_) == HIGH) {
    x = 'V';
    stopMoving();
  }
}
