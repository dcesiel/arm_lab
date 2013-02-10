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


public class RobotOperator
{
    private static double MAX_TRIANGLE_DISTANCE = 18.5;

    VisWorld vw = new VisWorld();
    VisLayer vl  = new VisLayer(vw);
    VisCanvas vc = new VisCanvas(vl);
    ParameterGUI pg = new ParameterGUI();
    double theta1_prev = 0;
    double theta2_prev = 0;
    double theta3_prev = 0;
    double theta4_prev = 0;
    double theta5_prev = 0;
    double theta6_prev = 0;

    static RobotArmGUI gui;

    public RobotOperator()
    {
        
        
    }

    public static void main(String[] args)
    {
        gui = new RobotArmGUI();
        gui.update();
    }

}
