import java.awt.*;
import java.io.*;
import static java.lang.System.out;

import javax.swing.*;

import lcm.lcm.*;
import april.jmat.*;
import april.util.*;
import april.vis.*;
import armlab.lcmtypes.*;
import april.*;

//======================================================================//
// RobotOperator class                                                  //
// A class for testing the different robot modules                      //
//                                                                      //
//Use:  -gui for running gui                                            //
//      -90 for running 90 state machine                                //
//======================================================================//
public class RobotOperator
{

    static RobotArmGUI gui;
    static StateMachine stateMachine = new StateMachine();

    public RobotOperator()
    {
        
    }

    public static void main(String[] args)
    {
        if (args.length > 0 && args[0].equals("-gui")){
            gui = new RobotArmGUI();
            gui.update();
        }
        else if (args.length > 0 && args[0].equals("-90")){
            //Call this using RobotOperator -gui angleVal distanceVal
            if (args.length > 2){
                Double angle = Double.valueOf(args[1]);
                Double distance = Double.valueOf(args[2]);
                System.out.println(angle);
                System.out.println(distance);
                stateMachine.pickUp90(angle, distance);
            }
            else {
                System.out.println("Please format like this: RobotOperator -gui angleVal distanceVal");
            }
        }
        else {
            System.out.println("Bad parameter please try: -gui or -90");
        }
            
    }

}
