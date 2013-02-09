import java.awt.*;
import java.io.*;
import static java.lang.System.out;

import javax.swing.*;

import lcm.lcm.*;
import april.jmat.*;
import april.util.*;
import april.vis.*;
import april.lcmtypes.*;


public class RobotArmGUI
{
    private static double MAX_TRIANGLE_DISTANCE 18.5;

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

    public RobotArmGUI()
    {
        JFrame jf = new JFrame();
        jf.setLayout(new BorderLayout());
        jf.add(vc, BorderLayout.CENTER);

        pg.addDoubleSlider("t1", "Joint 1", -180, 180, 0);
        pg.addDoubleSlider("t2", "Joint 2", -180, 180, 0);
        pg.addDoubleSlider("t3", "Joint 3", -180, 180, 0);
	    pg.addDoubleSlider("t4", "Joint 4", -180, 180, 0);
	    pg.addDoubleSlider("t5", "Joint 5", -180, 180, 0);
	    pg.addDoubleSlider("t6", "Joint 6", -180, 180, 0);
        jf.add(pg, BorderLayout.SOUTH);

        pg.addListener(new JointParamListener());
        update();

        /* Point vis camera the right way */
        vl.cameraManager.uiLookAt(
                new double[] {-2.27870, -6.35237, 4.75098 },
                new double[] { 0,  0, 0.00000 },
                new double[] { 0.13802,  0.40084, 0.90569 }, true);

        jf.setSize(800, 800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

    public static void main(String[] args)
    {
        RobotArmGUI gui = new RobotArmGUI();
        while(true) init();
    }

    private getDistance(double x, double y){
        return Math.sqrt((x^2 + y^2));
    }

    void init(){
        long now = TimeUtil.utime();
        dynamixel_command_list_t cmdlist = new dynamixel_command_list_t();
        cmdlist.len = 6;
        cmdlist.commands = new dynamixel_command_t[cmdlist.len];
        for (int i = 0; i < 6; i++) {
            dynamixel_command_t cmd = new dynamixel_command_t();
            cmd.position_radians = MathUtil.mod2pi(0);
            cmd.utime = now;
            cmd.speed = 0.5;
            cmd.max_torque = 0.6;
            cmdlist.commands[i] = cmd;
        }
        lcm.publish("ARM_COMMAND", cmdlist);
    }

    void update(double x, double y)
    {
        double theta1, theta2, theta3, theta4;
        double distance = getDistance(x, y);
        if (distance < MAX_TRIANGLE_DISTANCE){

        }

    }
}
