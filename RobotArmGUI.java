
import java.awt.*;

import javax.swing.*;

import april.jmat.*;
import april.util.*;
import april.vis.*;

public class RobotArmGUI
{
    static VisWorld vw = new VisWorld();
    static VisLayer vl  = new VisLayer(vw);
    static VisCanvas vc = new VisCanvas(vl);
    static ParameterGUI pg = new ParameterGUI();

    public static void main(String[] args)
    {
        JFrame jf = new JFrame();
        jf.setLayout(new BorderLayout());
        jf.add(vc, BorderLayout.CENTER);

        pg.addDoubleSlider("t1", "Joint 1", -180, 180, 0);
        pg.addDoubleSlider("t2", "Joint 2", -180, 180, 0);
        pg.addDoubleSlider("t3", "Joint 3", -180, 180, 0);
        jf.add(pg, BorderLayout.SOUTH);

        pg.addListener(new ParameterListener() {
            public void parameterChanged(ParameterGUI pg, String name)
            {
                // TODO: Send commands to servos
                // NOTE: Check validity of angles and self collision
                update();
            }
        });

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

    static void update()
    {
        VisChain arm = new VisChain();

        VzBox segBase = new VzBox(.05, 0.04, 0.075, new VzMesh.Style(Color.red));
        arm.add(segBase);

        VzBox segPivot1 = new VzBox(.04, 0.04, 0.045, new VzMesh.Style(Color.blue));
        double pivot1Angle = pg.gd("t1") * Math.PI/180;
        arm.add(LinAlg.rotateZ(pivot1Angle),
                /* we rotate about an end instead of the center by first translating it appropriately */
                LinAlg.translate(0.02, 0, 0.075), segPivot1);


        //VzBox seg1 = new VzBox(0.105, 0.5, 1, new VzMesh.Style(Color.orange));
        //arm.add(LinAlg.rotateZ(theta1),
                /* we rotate about an end instead of the center by first translating it appropriately */
         //       LinAlg.translate(0, 0, 0.5), LinAlg.rotateX(theta2), LinAlg.translate(0, 0, 0.5), seg1);

        /* build a gripper by chaining fingers together */
        VzBox fingerShort = new VzBox(0.1, 0.1, 0.8, new VzMesh.Style(Color.darkGray));
        VisChain segFixed = new VisChain(
                LinAlg.translate(-0.07, 0, 0), fingerShort,
                LinAlg.translate(+0.15, 0, 0), fingerShort );
        arm.add(LinAlg.translate(0, 0, 1.3), segFixed);

        /* build a 3 finger gripper */
        VzBox fingerLong = new VzBox(0.1, 0.1, 1, new VzMesh.Style(Color.gray));
        VisChain segGripper = new VisChain(
                LinAlg.translate(-0.15, 0, 0), fingerLong,
                LinAlg.translate(+0.15, 0, 0), fingerLong,
                LinAlg.translate(+0.15, 0, 0), fingerLong );
        double theta3 = pg.gd("t3") * Math.PI/180;
        arm.add(LinAlg.translate(0, 0.25, -0.5), LinAlg.rotateX(theta3), LinAlg.translate(0, 0, 0.5), segGripper);

        VisWorld.Buffer vb = vw.getBuffer("arm");
        vb.addBack(arm);
        vb.swap();
    }
}
