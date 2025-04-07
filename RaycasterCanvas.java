package larj2me;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class RaycasterCanvas extends GameCanvas implements Runnable 
{

    private volatile boolean running;
    private long fps;
    private int state;
    
    public static final int KEY_NUM5_PRESSED = (1 << Canvas.KEY_NUM5);
    
    static double DR = 0.0174532925;
    
    // PLAYER
    float plrX, plrY;
    float plrDeltaX, plrDeltaY;
    float plrAngle;
    
    // LEVEL
    int mapW = 8, mapH = 8;
    int mapS = mapW * mapH;
    boolean mapDrawn = false;
    int tileSize = 30; // Define tile size as 30 pixels
    
    public float distance(float ax, float ay, float bx, float by, float ang)
    {
        return (float) ( Math.sqrt( (bx-ax)*(bx - ax) + (by-ay)*(by-ay) ));
    }
    
    int map[] = 
    {
      1,1,1,1,1,1,1,1,
      1,0,1,0,0,0,0,1,
      1,0,1,0,0,0,0,1,
      1,0,1,0,0,1,0,1,
      1,0,0,0,1,0,0,1,
      1,0,0,1,1,1,0,1,
      1,0,0,0,0,0,0,1,
      1,1,1,1,1,1,1,1,
      
    };

    public RaycasterCanvas() 
    {
        super(true);

        state = 0;
        fps = 60;
        
        plrX = getWidth() / 2;
        plrY = getWidth() / 2 - 20;
        
        plrAngle = 0;
        plrDeltaX = (float) Math.cos(plrAngle) * 5;
        plrDeltaY = (float) Math.sin(plrAngle) * 5;
    }

    public void start() 
    {
        running = true;
        Thread t = new Thread(this);
        t.start();
        
        System.out.println("Width: "+ getWidth() + ", Height: " + getHeight());
    }

    public void stop() 
    {
        running = false;
    }

    public void run() 
    {
        Graphics g = getGraphics();

        while (running == true) 
        {
            tick();
            input();
            render(g);
            try 
            {
                Thread.sleep(1000 / fps);
            } 
            catch (InterruptedException ie) 
            {
                stop();
            }
        }
    }

    private void tick() 
    {
        state = (int) ((state + 1) % fps);
    }

    private void input() 
    {
        int keyStates = getKeyStates();
        if ((keyStates & LEFT_PRESSED) != 0) 
        {
            plrAngle -= 0.2;
            
            if(plrAngle < 0)
            {
                plrAngle += (float)(2*Math.PI);
                
            }
            plrDeltaX = (float) Math.cos(plrAngle) * 5;
            plrDeltaY = (float) Math.sin(plrAngle) * 5;
        }
        if ((keyStates & RIGHT_PRESSED) != 0) 
        {
            plrAngle += 0.2;
            
            if(plrAngle > (float)(2*Math.PI))
            {
                plrAngle -= (float)(2*Math.PI); 
            }
            plrDeltaX = (float) Math.cos(plrAngle) * 5;
            plrDeltaY = (float) Math.sin(plrAngle) * 5;
        }
        
        int xo = 0;
        int yo = 0;
        if(plrDeltaX < 0) {xo = -10;} else {xo = 10;}
        if(plrDeltaY < 0) {yo = -10;} else {yo = 10;}
        
        int gridX = (int) (plrX / tileSize);
        int gridX_add_xo = (int) ((plrX + xo) / tileSize);
        int gridX_sub_xo = (int) ((plrX - xo) / tileSize);
        
        int gridY = (int) (plrY / tileSize);
        int gridY_add_yo = (int) ((plrY + yo) / tileSize);
        int gridY_sub_yo = (int) ((plrY - yo) / tileSize);
        
        
        
        if ((keyStates & UP_PRESSED) != 0) 
        {
            if(map[gridY * mapW + gridX_add_xo] == 0) {plrX += plrDeltaX;}
            if(map[gridY_add_yo * mapW + gridX] == 0) {plrY += plrDeltaY;}
        }
        if ((keyStates & DOWN_PRESSED) != 0) 
        {
            if(map[gridY * mapW + gridX_sub_xo] == 0) {plrX -= plrDeltaX;}
            if(map[gridY_sub_yo * mapW + gridX] == 0) {plrY -= plrDeltaY;}
        }
        
        /*if ((keyStates & KEY_NUM5_PRESSED) != 0)
        {
            mapDrawn = true;
        }
        else
        {
            mapDrawn = false;
        }*/
    }

    private void render(Graphics g) 
    {
        g.setColor(0x4C4C4C);
        g.fillRect(0, 0, getWidth(), getHeight());
       
        if(mapDrawn)
        {
            drawMap2D(g);
            drawPlayer(g);
        }
        else
        {
            g.setColor(0,255,255);
            g.fillRect(0, 0, getWidth(), getHeight()/2);
        }
        castRays(g);
        
        flushGraphics();
    }
    
    protected void keyPressed(int keyCode) {
        if (keyCode == Canvas.KEY_NUM5) 
        {
            mapDrawn = !mapDrawn;
        }
    }

    /*protected void keyReleased(int keyCode) {
        if (keyCode == Canvas.KEY_NUM5)
        {
            mapDrawn = false;
        }
    }*/

    // PLAYER
    public void drawPlayer(Graphics g)
    {
        g.setColor(255, 255, 0);
        g.fillRect((int)(plrX-2), (int)(plrY-2), 4, 4);
        g.drawLine((int)(plrX), (int)(plrY), (int)(plrX+plrDeltaX*3), (int)(plrY+plrDeltaY*3));
    }
    
    // MAP
    public void drawMap2D(Graphics g)
    {
        int xo, yo;
        //int drawnMapSize = (int)(mapS / 2.0);
        
        for(int y = 0; y < mapH; y++)
        {
            for(int x = 0; x < mapW; x++)
            {
                if(map[y * mapW + x] == 1)
                {
                    g.setColor(255, 255, 255);
                }
                else
                {
                    g.setColor(0, 0, 0);
                }
                xo = x * tileSize; yo = y * tileSize;
                
                g.fillRect(xo + 1, yo + 1, tileSize-1, tileSize-1);
            }
        }
    }
    
    // RAYS
    public void castRays(Graphics g)
    {
        int r;
        int mX, mY, mP;
        int dof; // Depth of Field
        int fov = 90;
        
        float rX = 0, rY = 0, rAngle;
        float xo = 0, yo = 0; // X and Y offset
        
        rAngle = (float) (plrAngle - DR * (fov/2));
        if(rAngle < 0) { rAngle += 2* Math.PI;}
        if(rAngle > 2* Math.PI) { rAngle -= 2* Math.PI;}
        
        for(r = 0; r < fov; r++)
        {
            // Check Horizontal rays first
            dof = 0;
            float distH = 100000000;
            float horiz_x = plrX, horiz_y = plrY;
            
            float aTan = (float) (-1 / Math.tan(rAngle));
            
            if(Math.sin(rAngle) > 0.001) // Looking up
            {
                // Scary math time
                rY = (float) (((int)(plrY / tileSize) * tileSize) + tileSize);
                rX = (plrY - rY) * aTan + plrX;

                yo = tileSize;
                xo = -yo * aTan;
                
            }
            
            else if(Math.sin(rAngle) < -0.001) // Looking down
            {
                // Scary math time 2
                rY = (float) (((int)(plrY / tileSize) * tileSize) - 0.0001);
                rX = (plrY - rY) * aTan + plrX;

                yo = -tileSize;
                xo = -yo * aTan;
                
            }
            
            else // Looking straight left or right
            {
                // Imagine looking left or right lmao couldn't be me
                
                rX = plrX;
                rY = plrY;
                dof = 8;
            }
            
            // It's time to Cast them Rays - Jack "Minecraft Steve" Black, 2026 probably
            while(dof < 8) // We don't want to loop forever
            {
                // Scary math time 2 Episode 1
                mX = (int)(rX / tileSize);
                mY = (int)(rY / tileSize);
                
                // Get ray position in The Grid (patent pending)
                mP = mY * mapW + mX;
                
                if(mP > 0 && mP < mapW*mapH && map[mP] > 0) // We hit a wall chat
                {
                    dof = 8;
                    
                    horiz_x = rX;
                    horiz_y = rY;
                    distH = distance(plrX, plrY, horiz_x, horiz_y, rAngle);
                }
                else
                {
                    /* 
                        If we didn't hit a wall (sad :( )
                        All we have to do is to add the offset
                        Idk how this can be but yeah, that is all we need for raycasting!
                        Man I love raycasting
                    */

                    rX += xo;
                    rY += yo;
                    dof += 1;
                }
            }
            //drawRay2D(g, 0, 255, 0, (int)(plrX), (int)(plrY), (int)(rX), (int)(rY));
            
            // Now check the vertical lines
            dof = 0;
            float distV = 100000000;
            float vert_x = plrX, vert_y = plrY;
            
            float nTan = (float) -Math.tan(rAngle);
            
            if(Math.cos(rAngle) > 0.001) // Looking right
            {
                // Scary math time
                rX = (float) (((int)(plrX / tileSize) * tileSize) + tileSize);
                rY = (plrX - rX) * nTan + plrY;

                xo = tileSize;
                yo = -xo * nTan;
                
            }
            
            else if(Math.cos(rAngle) < -0.001) // Looking left
            {
                // Scary math time 2
                rX = (float) (((int)(plrX / tileSize) * tileSize) - 0.0001);
                rY = (plrX - rX) * nTan + plrY;

                xo = -tileSize;
                yo = -xo * nTan;
                
            }
            
            else // Looking straight up or down
            {
                // Lmao what a sucker looking up or down
                
                rX = plrX;
                rY = plrY;
                dof = 8;
            }
            
            // It's time to Cast them Rays Again - Jack "Minecraft Steve" Black, 2026 probably
            while(dof < 8) // We don't want to loop forever
            {
                // Scary math time 2 Episode 1
                mX = (int)(rX / tileSize);
                mY = (int)(rY / tileSize);
                
                // Get ray position in The Grid (patent pending)
                mP = mY * mapW + mX;
                
                if(mP > 0 && mP < mapW*mapH && map[mP] > 0) // Mr. President, another ray has hit the wall
                {
                    dof = 8;
                    
                    vert_x = rX;
                    vert_y = rY;
                    distV = distance(plrX, plrY, vert_x, vert_y, rAngle);
                }
                else
                {
                    /* 
                        If we didn't hit a wall (sad :( )
                        All we have to do is to add the offset
                        Idk how this can be but yeah, that is all we need for raycasting!
                        Man I love raycasting
                    */

                    rX += xo;
                    rY += yo;
                    dof += 1;
                }
            }
            
            float shade = 1; // Simple shading for the walls
            float totalDist = 0;

            // Use shortest distance
            if(distV < distH)
            {

                    rX = vert_x;
                    rY = vert_y;
                    totalDist = distV;

                    shade = 0.7f;
            }
            else if (distH < distV)
            {

                    rX = horiz_x;
                    rY = horiz_y;
                    totalDist = distH;

                    shade = 1.0f;
            }
            else
            {
                    rX = horiz_x;
                    rY = horiz_y;
                    totalDist = distH;

                    shade = 1.0f;
            }
                
            if(mapDrawn)
            {
                drawRay2D(g, 0, (int)(255*shade), 0, (int)(plrX), (int)(plrY), (int)(rX), (int)(rY));
            }
            else
            {
                drawRay3D(g, 0, (int)(255*shade), 0, totalDist, rAngle, r*4);
            }
            
            
            
            rAngle += DR; // Jump to next ray
            if(rAngle < 0) { rAngle += 2* Math.PI;}
            if(rAngle > 2* Math.PI) { rAngle -= 2* Math.PI;}
        }
    }
    
    public void drawRay2D(Graphics g, int red, int green, int blue, int px, int py, int rx, int ry)
    {
        g.setColor(red, green, blue);
        g.drawLine(px, py, rx, ry);
    }
    
    public void drawRay3D(Graphics g, int red, int green, int blue, float dist, float rAngle, int x)
    {
        g.setColor(red, green, blue);
        
        // We need to fix the fisheye effect first!!!!
        float ca = plrAngle - rAngle;
        if(ca < 0) { ca += (2*Math.PI); }
        if(ca > (2*Math.PI)) { ca -= (2*Math.PI); }

        float fixed_dist = (float) (dist * Math.cos(ca)); // Fix the fisheye effect
        
        
        float lineHeight = (tileSize * getHeight()) / fixed_dist;
        if(lineHeight > getHeight()) {
            // When line is too tall, we need to adjust both height and offset
            lineHeight = getHeight();
        }
        float lineOffset = getHeight()/2 - lineHeight/2;
        
        drawVerticalLine(g, x, (int)(lineOffset), 4, (int)(lineOffset+lineHeight));
    }
    
    public void drawVerticalLine(Graphics g, int x, int y, int width, int height)
    {
        g.fillRect(x, y, width, height-y);
    }
}
