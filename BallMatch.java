import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;

import april.jcam.*;
import april.util.*;
import april.jmat.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BallMatch implements MouseListener
{  

    //Image Globals    
    ImageSource is;
    BufferedImage template = null;
    BufferedImage im = null;

    //Template creation globals
    boolean clicked, getTemplate, onScreen, setTemplate = true, update = true;
    int X1 = 0, X2 = 1, Y1 = 0, Y2 = 1;

    //Error Bar Variables
    double error = 0;
    double errorK = 0;

    //GUI Gloabals
    JFrame jf = new JFrame();
    JImage jim = new JImage();
    ParameterGUI pg = new ParameterGUI();

    
	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {
		onScreen = true;
	}

	public void mouseExited(MouseEvent e) {
		onScreen = false;
	}

	public void mousePressed(MouseEvent me) {
             if(getTemplate & onScreen){
                X1 = me.getX();
                Y1 = me.getY();
                System.out.println("X: " + X1 + " Y: " + Y1);
                clicked = true;
            }
	}

	public void mouseReleased(MouseEvent me) {
            if(getTemplate & onScreen){
                X2 = me.getX();
                Y2 = me.getY();
                System.out.println("X: " + X2 + " Y: " + Y2);
                clicked = false;
                getTemplate = false;
                template = im;          
                int bounds[] = {X1, Y1, X2, Y2};
                markBall(im, bounds);

                jim.setImage(im);
            }
	}

    public BallMatch(ImageSource _is)
    {
        is = _is;

        // Determine which slider values we want
        pg.addDoubleSlider("errork","error Threshold",0,100,error);
        pg.addButtons("getTemplateButton", "Grab new template");
    	pg.addButtons("acceptTemplate", "Accept Template");
        pg.addButtons("calibrate", "Calibrate");
	    pg.addButtons("start", "Start");
	   	
        jim.setFit(true);
	
        // Setup window layout
        jf.setLayout(new BorderLayout());
        jf.add(jim, BorderLayout.NORTH);
        jf.add(pg, BorderLayout.SOUTH);
        jf.setSize(800, 800);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jim.addMouseListener(this);
    }

    // Returns the bounds of the pixel coordinates of the led: {min_x, min_y, max_x, max_y}
    public void matchBall()
    {
        for (int y = 0; y < im.getHeight() - template.getHeight(); y++){
            for (int x = 0; x < im.getWidth() - template.getWidth(); x++){
                for (int ty = 0; ty < template.getHeight(); ty++) {
                  for (int tx = 0; tx < template.getWidth(); tx++) {
                        int templateRGB = template.getRGB(tx, ty);
                        int imageRGB = im.getRGB(x + tx, y + ty);

                        int templateRed = (templateRGB>>16)&0xff;
                        int imageRed = (imageRGB>>16)&0xff;
                        int templateGreen = (templateRGB>>8)&0xff;
                        int imageGreen = (imageRGB>>8)&0xff;
                        int templateBlue = (templateRGB>>0)&0xff;
                        int imageBlue = (imageRGB>>0)&0xff;
                        error +=  Math.sqrt(Math.pow(templateRed - imageRed,2) +
                                            Math.pow(templateGreen - imageGreen,2) +
                                            Math.pow(templateBlue - imageBlue,2));
                        if (error < errorK){
                            int [] bounds = {x, y, (x+template.getWidth()), (y+template.getHeight())};
                            markBall(im, bounds);
                        }
                    }
                }
            }
        }
    }

    public void run(){
        is.start();
        final ImageSourceFormat fmt = is.getCurrentFormat();

        // Initialize visualization environment now that we know the image dimensions
        pg.addListener(new ParameterListener() {
            public void parameterChanged(ParameterGUI pg, String name)
            {
                if (name.equals("getTemplateButton")){
                    getTemplate = true;
                    // read a frame
                    byte buf[] = is.getFrame().data;
                    if (buf != null){
                        im = ImageConvert.convertToImage(fmt.format, fmt.width, fmt.height, buf);

                        jim.setImage(im);
                     }
                }
                if (name.equals("acceptTemplate")){
                    System.out.println("WTF");
                    setTemplate = false;
                }
            }
        });

        while(setTemplate){
            System.out.println("Waiting");
        }

        while(true) {
            // read a frame
            byte buf[] = is.getFrame().data;
            if (buf == null)
                continue;

            im = ImageConvert.convertToImage(fmt.format, fmt.width, fmt.height, buf);


            //If button has been clicked get a new template

            //matchBall(im, template, pg);

            //Place template in the top left corner of the screeen
            for (int ty = 0; ty < Y2-Y1; ty++) {
                for (int tx = 0; tx < X2-X1; tx++) {
                    im.setRGB(tx, ty, template.getRGB(X1 + tx, Y1 + ty));
                }
            }

            int [] bounds = {0, 0, X2-X1, Y2-Y1};
            markBall(im, bounds);

            
            //display image/
            jim.setImage(im);
        }
    }

    public void markBall(BufferedImage img, int[] bounds){
        // draw the horizontal lines
        for (int x = bounds[0]; x <=bounds[2]; x++) {
            img.setRGB(x,bounds[1], 0xff0000ff); //Go Blue!
            img.setRGB(x,bounds[3], 0xff0000ff); //Go Blue!
        }

        // draw the horizontal lines
        for (int y = bounds[1]; y <=bounds[3]; y++) {
            img.setRGB(bounds[0],y, 0xff0000ff); //Go Blue!
            img.setRGB(bounds[2],y, 0xff0000ff); //Go Blue
        }
     }

        public static void main(String args[]) throws IOException
        {
            ArrayList<String> urls = ImageSource.getCameraURLs();

            String url = null;
            if (urls.size()==1)
                url = urls.get(0);

            if (args.length > 0)
                url = args[0];

            if (url == null) {
                System.out.printf("Cameras found:\n");
                for (String u : urls)
                    System.out.printf("  %s\n", u);
                System.out.printf("Please specify one on the command line.\n");
                return;
            }

            ImageSource is = ImageSource.make(url);
            new BallMatch(is).run();
        }


    }
