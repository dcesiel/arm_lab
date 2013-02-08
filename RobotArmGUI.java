import java.awt.*;
import java.io.*;
import static java.lang.System.out;

import javax.swing.*;

import lcm.lcm.*;
import april.jmat.*;
import april.util.*;
import april.vis.*;
import armlab.lcmtypes.*;


public class RobotArmGUI implements LCMSubscriber
{
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

        /* Subscribe to ARM_STATUS */
        LCM.getSingleton().subscribe("ARM_STATUS", gui);
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
	//Create matrix to hold current location of parts
	double curXYZ[][] = new double[4][4];
	double tempXYZ[][] = new double[4][4];
	for(int i = 0; i < 4; i ++){
		for(int j = 0; j < 4; j ++){
			curXYZ[i][j] = 0;
			if(i == j){
				curXYZ[i][j] = 1;
			}
		}
	}

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
	
	boolean isCrashed = false;
	
	//Update matrix to offset by base
	curXYZ = LinAlg.multiplyMany(curXYZ, LinAlg.translate(0.0,0.0,0.75));
	if(curXYZ[2][3] < 0.45){ //Checks to see if the bot can move there, if it can't reset the prior joints to how they were
		isCrashed = true;
	}

	//Update matrix to offset by seg1
	curXYZ = LinAlg.multiplyMany(	curXYZ,
				LinAlg.translate(0.1,0.0,0.0),
				LinAlg.rotateZ(-theta1-1.5707963267948966192313216916397514420985846996875529),
				LinAlg.translate(0.0,0.0,0.45)
				);
	if(curXYZ[2][3] < 0.45){ //Checks to see if the bot can move there, if it can't reset the prior joints to how they were
		isCrashed = true;
		theta1 = theta1_prev;
	}

	//Update matrix to offset by seg2
	curXYZ = LinAlg.multiplyMany(	curXYZ,
					LinAlg.rotateX(-theta2),
					LinAlg.translate(0.0,0.0,1.05)
			     	    );
	if(curXYZ[2][3] < 0.45){ //Checks to see if the bot can move there, if it can't reset the prior joints to how they were
		isCrashed = true;
		theta1 = theta1_prev;
		theta2 = theta2_prev;
	}

	//Update matrix to offset by seg3
	curXYZ = LinAlg.multiplyMany(	curXYZ,
					LinAlg.rotateX(-theta3),
					LinAlg.translate(0.0,0.0,1.0)
			     	    );
	if(curXYZ[2][3] < 0.45){ //Checks to see if the bot can move there, if it can't reset the prior joints to how they were
		isCrashed = true;
		theta1 = theta1_prev;
		theta2 = theta2_prev;
		theta3 = theta3_prev;
	}

	//Update matrix to offset by seg4
	curXYZ = LinAlg.multiplyMany(	curXYZ,
					LinAlg.rotateX(-theta4),
					LinAlg.translate(0.0,0.0,0.8)
			     	    );
	if(curXYZ[2][3] < 0.45){ //Checks to see if the bot can move there, if it can't reset the prior joints to how they were
		isCrashed = true;
		theta1 = theta1_prev;
		theta2 = theta2_prev;
		theta3 = theta3_prev;
		theta4 = theta4_prev;
	}

	//Update matrix to offset by seg5
	curXYZ = LinAlg.multiplyMany(	curXYZ,
					LinAlg.rotateZ(-theta5),
					LinAlg.translate(0.0,0.0,0.2)
			     	    );
	if(curXYZ[2][3] < 0.45){ //Checks to see if the bot can move there, if it can't reset the prior joints to how they were
		isCrashed = true;
	}

	//Update matrix to offset by two prong gripper
	curXYZ = LinAlg.multiplyMany(	curXYZ,
					LinAlg.translate(0.0,0.0,0.85)
			     	    );
	if(curXYZ[2][3] < 0.45){ //Checks to see if the bot can move there, if it can't reset the prior joints to how they were
		isCrashed = true;
		theta1 = theta1_prev;
		theta2 = theta2_prev;
		theta3 = theta3_prev;
		theta4 = theta4_prev;
		theta5 = theta5_prev;
	}

	//Update matrix to offset by three prong gripper
	curXYZ = LinAlg.multiplyMany(	curXYZ,
					LinAlg.translate(0.0,0.0,-0.85),
					LinAlg.rotateX(-theta6),
					LinAlg.translate(0.0,0.0,0.85)
			     	    ); 	//first should be LinAlg.translate(0.0,-0.15,-0.85), but for some reason
					//it doesn't work when rotating in +x direction, only -x...
	if(curXYZ[2][3] < 0.45){ //Checks to see if the bot can move there, if it can't reset the prior joints to how they were
		isCrashed = true;
		theta1 = theta1_prev;
		theta2 = theta2_prev;
		theta3 = theta3_prev;
		theta4 = theta4_prev;
		theta5 = theta5_prev;
		theta6 = theta6_prev;
	}

	arm.add(segBottom);

	arm.add(LinAlg.translate(0.1, 0, 0.375), LinAlg.rotateZ(theta1+1.5707963267948966192313216916397514420985846996875529),
                /* we rotate about an end instead of the center by first translating it appropriately */
                LinAlg.translate(0, 0, 0.225), seg1);

	arm.add(LinAlg.translate(0, 0, 0.225), LinAlg.rotateX(theta2),
       	        /* we rotate about an end instead of the center by first translating it appropriately */
       	        LinAlg.translate(0, 0, 0.525), seg2);

	arm.add(LinAlg.translate(0, 0, 0.525), LinAlg.rotateX(theta3),
	                /* we rotate about an end instead of the center by first translating it appropriately */
	                LinAlg.translate(0, 0, 0.5), seg3);

	arm.add(LinAlg.translate(0, 0, 0.5), LinAlg.rotateX(theta4),
	                /* we rotate about an end instead of the center by first translating it appropriately */
	                LinAlg.translate(0, 0, 0.4), seg4);

	arm.add(LinAlg.translate(0, 0, 0.4), LinAlg.rotateZ(theta5),
                	/* we rotate about an end instead of the center by first translating it appropriately */
                	LinAlg.translate(0, 0, 0.1), seg5);

	arm.add(LinAlg.translate(0, 0, 0.5), segFixed);

	arm.add(LinAlg.translate(0, 0.25, -0.5), LinAlg.rotateX(theta6), LinAlg.translate(0, 0, 0.5), segGripper);

	theta1_prev = theta1;
	theta2_prev = theta2;
	theta3_prev = theta3;
	theta4_prev = theta4;
	theta5_prev = theta5;
	theta6_prev = theta6;

        VisWorld.Buffer vb = vw.getBuffer("arm");
        vb.addBack(arm);
        vb.swap();
    }

    @Override
    public void messageReceived(LCM lcm, String channel, LCMDataInputStream dins)
    {
        try {
            dynamixel_status_list_t arm_status = new dynamixel_status_list_t(dins);
            /* access positions using arm_status.statuses[i].position_radians */
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
