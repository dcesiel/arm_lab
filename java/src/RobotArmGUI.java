import java.awt.*;
import java.io.*;
import static java.lang.System.out;

import javax.swing.*;

import lcm.lcm.*;
import april.jmat.*;
import april.util.*;
import april.vis.*;
import armlab.lcmtypes.*;


public class RobotArmGUI
{
    VisWorld vw = new VisWorld();
    VisLayer vl  = new VisLayer(vw);
    VisCanvas vc = new VisCanvas(vl);
    ParameterGUI pg = new ParameterGUI();
    ConstraintCheck cc = new ConstraintCheck();

    public RobotArmGUI()
    {
        JFrame jf = new JFrame();
        jf.setLayout(new BorderLayout());
        jf.add(vc, BorderLayout.CENTER);

        pg.addDoubleSlider("t1", "Joint 1", -179, 179, 0);
        pg.addDoubleSlider("t2", "Joint 2", -100, 100, 0);
        pg.addDoubleSlider("t3", "Joint 3", -180, 180, 0);
        pg.addDoubleSlider("t4", "Joint 4", -180, 180, 0);
        pg.addDoubleSlider("t5", "Joint 5", -180, 180, 0);
        pg.addDoubleSlider("t6", "Joint 6", -180, 180, 0);
        jf.add(pg, BorderLayout.SOUTH);

        pg.addListener(new JointParamListener());

        /* Point vis camera the right way */
        vl.cameraManager.uiLookAt(
                new double[] {-2.27870, -6.35237, 4.75098 },
                new double[] { 0,  0, 0.00000 },
                new double[] { 0.13802,  0.40084, 0.90569 }, true);

        jf.setSize(800, 800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

     class JointParamListener implements ParameterListener
     {
         public void parameterChanged(ParameterGUI pg, String name)
         {
             // TODO: Send commands to servos
             // NOTE: Check validity of angles and self collision
             update();
         }
     }
    void update()
    {
	
	VisChain arm = new VisChain();

    	VzBox segBottom = new VzBox(0.5, 0.4, 0.75, new VzMesh.Style(Color.red)); //Base shape

    	VzBox seg1 = new VzBox(0.4, 0.4, 0.45, new VzMesh.Style(Color.orange)); //First segment shape
    	double theta1 = pg.gd("t1") * Math.PI/180;

	VzBox seg2 = new VzBox(0.4, 0.4, 1.05, new VzMesh.Style(Color.blue)); //Second segment shape
	double theta2 = pg.gd("t2") * Math.PI/180;

	if(theta2 > 2.12){ //making sure it doesn't rotate back on itself
		theta2 = 2.12;
	}

	if(theta2 < -2.12){
		theta2 = -2.12;
	}

	VzBox seg3 = new VzBox(0.35, 0.45, 1.0, new VzMesh.Style(Color.red)); //Third segment shape
	double theta3 = pg.gd("t3") * Math.PI/180;

	if(theta3 > 2.12){ //making sure it doesn't rotate back on itself
		theta3 = 2.12;
	}

	if(theta3 < -2.12){
		theta3 = -2.12;
	}

	VzBox seg4 = new VzBox(0.35, 0.65, 0.8, new VzMesh.Style(Color.orange)); //Fourth segment shape
	double theta4 = pg.gd("t4") * Math.PI/180;

	if(theta4 > 2.12){ //making sure it doesn't rotate back on itself
		theta4 = 2.12;
	}

	if(theta4 < -2.12){
		theta4 = -2.12;
	}

	VzBox seg5 = new VzBox(0.5, 0.55, 0.2, new VzMesh.Style(Color.blue)); //Fifth segment shape
	double theta5 = pg.gd("t5") * Math.PI/180;

        /* build a gripper by chaining fingers together */
        VzBox fingerShort = new VzBox(0.1, 0.1, 0.85, new VzMesh.Style(Color.darkGray)); //Two prong gripper shape
        VisChain segFixed = new VisChain(
                LinAlg.translate(-0.07, 0, 0), fingerShort,
                LinAlg.translate(+0.15, 0, 0), fingerShort );

        /* build a 3 finger gripper */
        VzBox fingerLong = new VzBox(0.1, 0.1, 0.85, new VzMesh.Style(Color.gray)); //Three prong gripper shape
        VisChain segGripper = new VisChain(
                LinAlg.translate(-0.15, 0, 0), fingerLong,
                LinAlg.translate(+0.15, 0, 0), fingerLong,
                LinAlg.translate(+0.15, 0, 0), fingerLong );
        double theta6 = pg.gd("t6") * Math.PI/180;

	if(theta6 > 0){ //Restricting gripper range to 30 degrees open
		theta6 = 0;
	}

	if(theta6 < -.5235){
		theta6 = -.5235;
	}
	
	theta6 += 1.5707963267948966192313216916397514420985846996875529;
	
        double[] angles = new double[6];
        angles[0] = theta1;
        angles[1] = theta2;
        angles[2] = theta3;
        angles[3] = theta4;
        angles[4] = theta5;
        angles[5] = theta6;	
	cc.check(angles);
	
	arm.add(segBottom);

	arm.add(LinAlg.translate(0.1, 0, 0.375), LinAlg.rotateZ(theta1+1.5707963267948966192313216916397514420985846996875529),
                /* we rotate about an end instead of the center by first translating it appropriately */
                LinAlg.translate(0, 0, 0.225), seg1);

	arm.add(LinAlg.translate(0, 0, 0.225), LinAlg.rotateX(-theta2),
       	        /* we rotate about an end instead of the center by first translating it appropriately */
       	        LinAlg.translate(0, 0, 0.525), seg2);

	arm.add(LinAlg.translate(0, 0, 0.525), LinAlg.rotateX(-theta3),
	                /* we rotate about an end instead of the center by first translating it appropriately */
	                LinAlg.translate(0, 0, 0.5), seg3);

	arm.add(LinAlg.translate(0, 0, 0.5), LinAlg.rotateX(-theta4),
	                /* we rotate about an end instead of the center by first translating it appropriately */
	                LinAlg.translate(0, 0, 0.4), seg4);

	arm.add(LinAlg.translate(0, 0, 0.4), LinAlg.rotateZ(theta5-1.5707963267948966192313216916397514420985846996875529),
                	/* we rotate about an end instead of the center by first translating it appropriately */
                	LinAlg.translate(0, 0, 0.1), seg5);

	arm.add(LinAlg.translate(0, 0, 0.5), segFixed);

	arm.add(LinAlg.translate(0, 0.25, -0.5), LinAlg.rotateX(theta6-1.5707963267948966192313216916397514420985846996875529), LinAlg.translate(0, 0, 0.5), segGripper);

        VisWorld.Buffer vb = vw.getBuffer("arm");
        vb.addBack(arm);
        vb.swap();
    }

}
