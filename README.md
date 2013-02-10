arm_lab
=======

EECS 498 arm lab

To start listening to lcm messages:

    cd java
    
To get ttyUSB:

    ls /dev/ttyUSB*

To start the arm driver:

    java -cp "$CLASSPATH:/home/MYUSERNAME/arm_lab/java/armlab.jar" armlab.arm.ArmDriver -d /dev/ttyUSB[x]


Code info:
RobotArmGUI.java: For manuel control of arm
RobotOperator.java Class to run RobotArmguI.java


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
