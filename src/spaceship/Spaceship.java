
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    int rocketXPos;
    int rocketYPos;
    int rocketYSpeed;
    int rocketXSpeed;
    int rocketDirection;
    int rocketWidth;
    int rocketHeight;
    
    int health;
    int score;
    int highScore = 0;
    boolean gameOver;
    
    Missile missiles[];
        
    int starNum = 8;
    int StarXPos[];
    int StarYPos[];
    int starHeight[];
    int starWidth[];
    boolean collision[];
    

    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_W == e.getKeyCode()) {
                    rocketYSpeed++;
                } else if (e.VK_S == e.getKeyCode()) {
                    rocketYSpeed--;
                } else if (e.VK_A == e.getKeyCode()) {
                    if (rocketXSpeed > -15)
                    rocketXSpeed--;
                } else if (e.VK_D == e.getKeyCode()) {
                    if (rocketXSpeed < 15)
                    rocketXSpeed++;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                else if (e.VK_E == e.getKeyCode()) {
                    reset();
                }
                else if (e.VK_SPACE == e.getKeyCode()) {
                    
                    
                if(rocketDirection == 1)
                {
                    missiles[Missile.currentMissile].Right = true;
                }
                else if(rocketDirection == -1)
                {
                    missiles[Missile.currentMissile].Right = false;
                }
        
                    missiles[Missile.currentMissile].Active = true;
                    missiles[Missile.currentMissile].XPos = rocketXPos;
                    missiles[Missile.currentMissile].YPos = rocketYPos;
                    if (Missile.currentMissile < Missile.missileNum)
                    {
                        Missile.currentMissile++;
                    }
                    if (Missile.currentMissile >= Missile.missileNum)
                    {
                        Missile.currentMissile = 0;
                    }
                    
                }
                
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        if(!gameOver)
            {
            for(int i=0;i<starNum;i++)
            {
                drawStar(getX(StarXPos[i]),getYNormal(StarYPos[i]),0.0,1.0,1.0 );
            }

            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,rocketDirection,1.0 );


            for(int i=0;i<Missile.missileNum;i++)
            {
                if (missiles[i].Active)
                {
                    drawMissile(getX(missiles[i].XPos),getYNormal(missiles[i].YPos),0.0,1,1);
                }
            }

            g.setColor(Color.white);
            g.drawString("Score: " + score,getX(10),getYNormal(getHeight2()-15));
            g.drawString("HighScore: " + highScore,getX(10),getYNormal(getHeight2()-30));
        }
        else
        {
            g.setColor(Color.white);
            g.drawString("Game Over",getX(getWidth2()/2),getYNormal(getHeight2()/2));
        }
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawStar(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        for(int i=0;i<starNum;i++)
        {
            g.setColor(Color.yellow);
            g.fillOval(starWidth[i]/-2,starHeight[i]/-2,starWidth[i],starHeight[i]);
        }

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    ////////////////////////////////////////////////////////////////////////////
    public void drawMissile(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

            g.setColor(Color.red);
            g.fillOval(-5,-2,10,4);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        rocketYSpeed = 0;
        rocketXSpeed = 0;
        rocketDirection = 1;
        StarXPos = new int[starNum];
        StarYPos = new int[starNum];
        starHeight = new int[starNum];
        starWidth  = new int[starNum];
        collision = new boolean[starNum];
        score = 0;
        health = 5;
        gameOver = false;
        
        for(int i=0;i<starNum;i++)
        {
            StarXPos[i] = (int)(Math.random()*getWidth2());
            StarYPos[i] = (int)(Math.random()*getHeight2());
            starHeight[i] = 20;
            starWidth[i] = 20;
            collision[i] = false;
        }
        
        
        missiles = new Missile[Missile.missileNum];
        Missile.currentMissile = 0;
        for(int i=0;i<Missile.missileNum;i++)
        {
            missiles[i] = new Missile();
        }
        
        rocketWidth = rocketImage.getWidth(this);
        rocketHeight = rocketImage.getHeight(this);
        
        
        

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            readFile();
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            bgSound = new sound("starwars.wav");
            reset();
        }
        rocketYPos+=rocketYSpeed;
        for(int i=0;i<starNum;i++)
        {
            StarXPos[i]-=rocketXSpeed;
        }
        
        if (rocketYPos + rocketYSpeed > getHeight2())
        {
            rocketYSpeed=0;
        }
        else if(rocketYPos + rocketYSpeed < 0)
        {
            rocketYSpeed=0;
        }
        
        for(int i=0;i<starNum;i++)
        {
            if (StarXPos[i] - rocketXSpeed > getWidth2())
            {
                StarXPos[i]=0;
                StarYPos[i] = (int)(Math.random()*getHeight2());
            }
            if (StarXPos[i] - rocketXSpeed < 0)
            {
                StarXPos[i]=getWidth2();
                StarYPos[i] = (int)(Math.random()*getHeight2());
            }
        }
        if (rocketXSpeed < 0)
            {
            rocketDirection = -1;
            rocketImage = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
            }
        else if (rocketXSpeed > 0)
            {
            rocketDirection = 1;
            rocketImage = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
            }
        else
            {
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            }
        
        for(int i=0;i<starNum;i++)
        {
            if ((rocketXPos + rocketWidth/2 < StarXPos[i] + starWidth[i]/2 &&
                rocketXPos + rocketWidth/2 > StarXPos[i] - starWidth[i]/2 ||
                rocketXPos - rocketWidth/2 < StarXPos[i] + starWidth[i]/2 &&
                rocketXPos - rocketWidth/2 > StarXPos[i] - starWidth[i]/2) &&
                (rocketYPos + rocketHeight/2 < StarYPos[i] + starHeight[i]/2 &&
                rocketYPos + rocketHeight/2 > StarYPos[i] - starHeight[i]/2 ||
                rocketYPos - rocketHeight/2 < StarYPos[i] + starHeight[i]/2 &&
                rocketYPos - rocketHeight/2 > StarYPos[i] - starHeight[i]/2))
                {
                    if (collision[i] == false)
                        {
                        health--;
                        zsound = new sound("ouch.wav");
                        collision[i] = true;
                        }
                }
            else
                {
                collision[i] = false;
                }

        }


            for(int i=0;i<Missile.missileNum;i++)
            {
                if(missiles[i].Right == true)
                {
                    missiles[i].XPos+=2;
                }
                else
                {
                    missiles[i].XPos-=2;
                }
            }
            
            for(int i=0;i<Missile.missileNum;i++)
            {
                for(int sn=0;sn<starNum;sn++)
                {
                    if((missiles[i].XPos < StarXPos[sn] + starWidth[sn]/2 &&
                       missiles[i].XPos > StarXPos[sn] - starWidth[sn]/2) &&
                       (missiles[i].YPos < StarYPos[sn] + starWidth[sn]/2 &&
                       missiles[i].YPos > StarYPos[sn] - starWidth[sn]/2) &&
                       missiles[i].Active == true)
                    {
                        missiles[i].Active = false;
                        if(missiles[i].Right == true)
                        {
                        StarXPos[sn]=getWidth2();
                        }
                        else
                        {
                        StarXPos[sn]=0;    
                        }
                        StarYPos[sn] = (int)(Math.random()*getHeight2());
                        score++;
                    }
                }
            }

        if(bgSound.donePlaying)
        {
            bgSound = new sound("starwars.wav");
        }
        
        if(score > highScore)
        {
            highScore = score;
        }
        if(health<=0)
        {
            gameOver = true;
        }

    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    starNum = Integer.parseInt(numStarsString.trim());
                }
                if (newLine.startsWith("nummissiles"))
                {
                    String numMissilesString = newLine.substring(12);
                    Missile.missileNum = Integer.parseInt(numMissilesString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }
    
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}

class Missile
{
    public static int missileNum = 10;
    public static int currentMissile = 0;
    
    public int XPos;
    public int YPos;
    public boolean Active;
    public boolean Right;
    
    Missile()
    {
        Active = false;
    }
}