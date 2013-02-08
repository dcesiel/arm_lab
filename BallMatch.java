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
import java.util.Vector;


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
    double errorK = 35;
    int scalefactor = 4;
	int pRange = 20;



    //Calibration Globals
    boolean calibrate = false, notcalibrated = true; 
	boolean cal1 = false, cal2 = false, cal3 = false, cal4 = false;
	Location CalPt1 = new Location();
	Location CalPt2 = new Location();
	Location CalPt3 = new Location();
	Location CalPt4 = new Location();

	//Globals for runing
	boolean run = false;
	

    //GUI Gloabals
    JFrame jf = new JFrame();
    JImage jim = new JImage();
    ParameterGUI pg = new ParameterGUI();


    //Locations Found
    public static class Location{
		int x = 0;
		int y = 0;
    }
    Vector<Location> found = new Vector<Location>();
	Vector<Location> located = new Vector<Location>();


    
	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {
		onScreen = true;
	}

	public void mouseExited(MouseEvent e) {
		onScreen = false;
	}

	public void mousePressed(MouseEvent me) {
			System.out.println("I have be PRESSED!");
             if(getTemplate & onScreen){
                X1 = me.getX();
                Y1 = me.getY();
                System.out.println("X: " + X1 + " Y: " + Y1);
                clicked = true;
            }

			if(calibrate & !cal1){
				CalPt1.x = me.getX();
				CalPt1.y = me.getY();
				cal1 = true;
				System.out.println("Calibrating1 X: " + CalPt1.x  + " Y: " + CalPt1.y);
			}
			else if(calibrate & onScreen & !cal2){
				CalPt2.x = me.getX();
				CalPt2.y = me.getY();
				cal2 = true;
				System.out.println("Calibrating2 X: " + CalPt2.x  + " Y: " + CalPt2.y);

			}
			else if(calibrate & onScreen & !cal3){
				CalPt3.x = me.getX();
				CalPt3.y = me.getY();
				cal3 = true;
				System.out.println("Calibrating3 X: " + CalPt3.x  + " Y: " + CalPt3.y);
			}
			else if(calibrate & onScreen & !cal4){
				CalPt4.x = me.getX();
				CalPt4.y = me.getY();
				cal4 = true;
				System.out.println("Calibrating4 X: " + CalPt4.x  + " Y: " + CalPt4.y);
				calibrate = false;
				notcalibrated = false;
System.out.println("Calibrating4" + notcalibrated);

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
                mark(im, bounds, 0xff0000ff);
                jim.setImage(im);
            }
	}

    public BallMatch(ImageSource _is)
    {
        is = _is;

        // Determine which slider values we want
        pg.addDoubleSlider("errork","error Threshold",0,100, errorK);
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

	errorK = pg.gd("errork");
        int tsizeX = X2 - X1;
        int tsizeY = Y2 - Y1;
        error = 0;
        boolean nskiped = true;

        for (int y = 0; y < im.getHeight() - tsizeY; y += scalefactor){
            for (int x = 0; x < im.getWidth() - tsizeX; x += scalefactor){
                for (int ty = 0; ty < tsizeY; ty += scalefactor) {
                  for (int tx = 0; tx < tsizeX; tx += scalefactor) {
                        int templateRGB = template.getRGB(X1 + tx, Y1 + ty);
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
                    
                    }
                    if (error > (errorK * tsizeX * tsizeY)/ (2 * scalefactor)){
                        nskiped = false;
                        break;
                    }
                }
                if (error < (errorK * tsizeX * tsizeY)/(2*scalefactor) & nskiped){
                   System.out.println("Error is " + error + "    K is " + errorK);
                    int [] bounds = {x, y, x + X2 - X1, y + Y2 - Y1};
                    mark(im, bounds, 0xff0000ff);
                    Location loc = new Location();
                    loc.x = (x + x + X2 - X1)/2;
                    loc.y = (y +y + Y2 - Y1)/2;
                    found.add(loc);
                }
                error = 0;
                nskiped = true;
            }
        }

		int X = 0, Y = 0, totalX = 0, totalY = 0, count = 0;
		int compX = 0, compY = 0;
		int bound[] = new int[4];
		System.out.println("found " + found.size());

		while(0 != found.size()){
			
			X = found.get(0).x;
			Y = found.get(0).y;
			totalX += X;
			totalY += Y;
			count++;
			found.remove(0);
			System.out.println("found1 " + found.size());
System.out.println("HHHH " + X + " " + Y);

			for(int i = 0; i < found.size(); i++){
				compX = found.get(i).x;
				compY = found.get(i).y;
				System.out.println("HHHH " + compX + " " + compY);

				if(((compX <= X) & (compX + pRange >= X)) |
					((compX >= X) & (compX- pRange <= X))){
					if(((compY <= Y) & (compY + pRange >= Y)) |
						((compY >= Y) & (compY - pRange <= Y))){
						totalX += compX;
						totalY += compY;
						count++;
						found.remove(i);
						i--;
						System.out.println("found2 " + found.size());
					}
				}
			}
			Location temp = new Location();
			temp.x = totalX / count;
			temp.y = totalY / count;

			totalX = 0;
			totalY = 0;
			count = 0;
			
			bound[0] = temp.x -1;
			bound[1] =  temp.y -1;
			bound[2] =  temp.x + 1;
			bound[3] = temp.y+1;
            mark(im, bound, 0xff00ff00);


			//add code to optimize the order of balls that should be retrived
			located.add(temp);
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
                    System.out.println("Template has been accepted!");
                    setTemplate = false;
                }
                if (name.equals("calibrate")){
                    System.out.println("Calibration Time");
                    calibrate = true;
                }
                if (name.equals("start")){
                    System.out.println("Running Time");
                    run = true;
                }
            }
        });

		//Waiting for Template
		System.out.println("Waiting to Template.");
        while(setTemplate){System.out.println(setTemplate);}

		//Waiting for Calibration
		System.out.println("Waiting to Calibrating");
		while(notcalibrated){System.out.println("CAL" + notcalibrated);}

		//Waiting to Start
		System.out.println("Waiting to Start");
		while(!run){System.out.println("SART" + run);}
			

        while(run) {
            // read a frame
            byte buf[] = is.getFrame().data;
            if (buf == null)
                continue;

            im = ImageConvert.convertToImage(fmt.format, fmt.width, fmt.height, buf);


            //If button has been clicked get a new template
            matchBall();

            //outputs the image template and calibration points to the screen
			screenOutput();
        }
    }

	public void screenOutput(){
			//Place template in the top left corner of the screeen
            for (int ty = 0; ty < Y2-Y1; ty++) {
                for (int tx = 0; tx < X2-X1; tx++) {
                    im.setRGB(tx, ty, template.getRGB(X1 + tx, Y1 + ty));
                }
            }
            int [] bounds = {0, 0, X2-X1, Y2-Y1};
            mark(im, bounds, 0xff0000ff);

			//mark calibration points
			int [] bounds1 = {CalPt1.x - 1, CalPt1.y - 1, CalPt1.x + 1, CalPt1.y + 1};
			mark(im, bounds1, 0xffff0000);

			int [] bounds2 = {CalPt2.x - 1, CalPt2.y - 1, CalPt2.x + 1, CalPt2.y + 1};
			mark(im, bounds2, 0xffff0000);
		
			int [] bounds3 = {CalPt3.x - 1, CalPt3.y - 1, CalPt3.x + 1, CalPt3.y + 1};
			mark(im, bounds3, 0xffff0000);

			int [] bounds4 = {CalPt4.x - 1, CalPt4.y - 1, CalPt4.x + 1, CalPt4.y + 1};
			mark(im, bounds4, 0xffff0000);




            //display image/
            jim.setImage(im);
	}
    public void mark(BufferedImage img, int[] bounds, int color){
        // draw the horizontal lines
        for (int x = bounds[0]; x <=bounds[2]; x++) {
            img.setRGB(x,bounds[1], color); //Go Blue!
            img.setRGB(x,bounds[3], color); //Go Blue!
        }

        // draw the horizontal lines
        for (int y = bounds[1]; y <=bounds[3]; y++) {
            img.setRGB(bounds[0],y, color); //Go Blue!
            img.setRGB(bounds[2],y, color); //Go Blue
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
