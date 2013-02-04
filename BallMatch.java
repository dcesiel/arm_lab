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

public class BallMatch extends MouseAdapter
{
  static double errorK = 0;
  static double error = 0;
  ImageSource is;

  boolean templateCurrent;
  BufferedImage template;

  boolean click2, getTemplate;
  int X1, X2, Y1, Y2; 

  JFrame jf = new JFrame("LED Tracker Demo");
  JImage jim = new JImage();

  ParameterGUI pg = new ParameterGUI();

  public BallMatch(ImageSource _is)
  {
    is = _is;

    // Determine which slider values we want
    pg.addDoubleSlider("errork","error Threshold",0,100,error);
    pg.addButtons("getTemplateButton", "Grab new template");

    jim.setFit(true);

    // Setup window layout
    jf.setLayout(new BorderLayout());
    jf.add(jim, BorderLayout.CENTER); 
    jf.add(pg, BorderLayout.SOUTH);
    jf.setSize(1024, 768);
    jf.setVisible(true);
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  // Returns the bounds of the pixel coordinates of the led: {min_x, min_y, max_x, max_y}
  public void matchBall(BufferedImage img, BufferedImage template, ParameterGUI pg)
  {
	  for (int y = 0; y < img.getHeight() - template.getHeight(); y++){
      for (int x = 0; x < img.getWidth() - template.getWidth(); x++){
        for (int ty = 0; ty < template.getHeight(); ty++) {
          for (int tx = 0; tx < template.getWidth(); tx++) {
            int templateRGB = template.getRGB(tx, ty);
            int imageRGB = img.getRGB(x + tx, y + ty);

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
              markBall(img, bounds);
            }  
          }
        }
      }
    }
  }

  @Override
    public void mouseClicked(MouseEvent me) {
      if(getTemplate){
        int screenX = me.getXOnScreen();
        int screenY = me.getYOnScreen();

        if(! click2){
            X1 = screenX;
            Y1 = screenY;
            System.out.println("First Time screen(X,Y) = " + screenX + "," + screenY);
            click2 = true;
        }
        if(click2){
            X2 = screenX;
            Y2 = screenY;
            System.out.println("First Time screen(X,Y) = " + screenX + "," + screenY);
            getTemplate = false;
        }
    }
  }

  public void run()
  {
    is.start();
    ImageSourceFormat fmt = is.getCurrentFormat();

    // Initialize visualization environment now that we know the image dimensions

    while(true) {
      // read a frame
      byte buf[] = is.getFrame().data;
      if (buf == null)
        continue;


      BufferedImage im = ImageConvert.convertToImage(fmt.format, fmt.width, fmt.height, buf);


      //If button has been clicked get a new template
      pg.addListener(new ParameterListener() {
          public void paramaterChanged(ParameterGUI pg, String name)
          {
              if (name.equals("getTemplateButton")){
                getTemplate = true;
                click2 = false;
                while(getTemplate){} 
                templateCurrent = false;
              }
          }
      });

      template = setTemplate(im);

      matchBall(im, template, pg);

      //Place template in the top left corner of the screeen
      for (int ty = 0; ty < template.getHeight(); ty++) {
        for (int tx = 0; tx < template.getWidth(); tx++) {
          im.setRGB(tx, ty, template.getRGB(tx, ty));
        }
      }
      int [] bounds = {0, 0, template.getWidth(), template.getHeight()};
      markBall(im, bounds);

      //display image
      jim.setImage(im);
    }
  }

  public BufferedImage setTemplate(BufferedImage img){
    return null;
  }

  public void markBall(BufferedImage img, int[] bounds){

    // Display the detection, by drawing on the image
    if (true) {
      // draw the horizontal lines
      for (int y : new int[]{bounds[1], bounds[3]})
        for (int x = bounds[0]; x <=bounds[2]; x++) {
          img.setRGB(x,y, 0xff0000ff); //Go Blue!
        }

      // draw the horizontal lines
      for (int x : new int[]{bounds[0], bounds[2]})
        for (int y = bounds[1]; y <=bounds[3]; y++) {
          img.setRGB(x,y, 0xff0000ff); //Go Blue!
        }

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
