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

public class LCMSend
{
    LCM lcm;

    public LCMSend(){
        try{
            lcm = new LCM();
        } catch (Exception e){
        
        }
    }
    
    void send(double[] angles){
	long now = TimeUtil.utime();
        dynamixel_command_list_t cmdlist = new dynamixel_command_list_t();
        cmdlist.len = 6;
        cmdlist.commands = new dynamixel_command_t[cmdlist.len];
        for (int i = 0; i < 6; i++) {
            dynamixel_command_t cmd = new dynamixel_command_t();
            cmd.position_radians = MathUtil.mod2pi(angles[i]);
            cmd.utime = now;
            cmd.speed = .1;
            cmd.max_torque = 0.6;
            cmdlist.commands[i] = cmd;
        }
        lcm.publish("ARM_COMMAND", cmdlist);
    }

}
