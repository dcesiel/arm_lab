arm_lab
=======

EECS 498 arm lab

To start listening to lcm messages:

    cd java
    
To get ttyUSB:

    ls /dev/ttyUSB*

To start the arm driver:

    java -cp "$CLASSPATH:/home/MYUSERNAME/arm_lab/java/armlab.jar" armlab.arm.ArmDriver -d /dev/ttyUSB[x]
    
To start listening to the arm location:

    java -cp "$CLASSPATH:/home/MYUSERNAME/arm_lab/java/arlstaff.jar" arlstaff.arm.ArmController -d /dev/ttyUSB[x]


Code info:
RobotArmGUI.java: For manuel control of arm
RobotOperator.java Class to run RobotArmguI.java

BallMatch.java: The master class that does template matching and calls all necessary classes

StateMachine.java:  Contains pickUp90 and pickUpStraight state machines for arm
    Methods: pickUp90(double armDistance, double angle); (returns array of 6 servo angles)
             pickUpStraight(double armDistance, double angle); (returns array of  6 servo angles)
             
ConstraintCheck.java: Checks constraints and makes call to LCMSend.
    Methods: check(double[] angles); (returns array of 6 servo angles)
    
LCMSend.java: Sends out servo angles to the robot
    Methods: send(double[] angles);  
    
This is an example of how the objects would call each other:
BallMatch.java
  |
  |->StateMachine.ballPickUp(still need to figure out these params)
        |
        |->ConstraintCheck.check(angles)
                |
                |->LCMSend.send(angles)


Arm Dimensions:
Base:
  x: 5cm
  y: 7.5cm
  z: 4cm

Pivot1:
  x: 4cm
  y: 4.5cm
  z: 4cm

Length1:
  x: 4cm
  y: 10.5cm
  z: 4.5cm

Length2:
  x: 3.5cm
  y: 10cm
  z: 4.5cm

Length3:
  x: 3.5cm
  y: 8cm
  z: 6.5cm

Pivot2:
  x: 5cm
  y: 2cm
  z: 5.5cm

Claw (opens and closes in the negative x direction):
  x: 12cm
  y: 8.5cm
  z: 5.5cm
