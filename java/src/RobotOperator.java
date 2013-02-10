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
            
        }
        else {
            System.out.println("Bad parameter please try: -gui or -90");
        }
            
    }

}
