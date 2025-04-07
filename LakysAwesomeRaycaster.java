/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package larj2me;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;


/**
 * @author lakymint
 */
public class LakysAwesomeRaycaster extends MIDlet implements CommandListener {
    private Display mDisplay;

    private RaycasterCanvas mCanvas;
    private Command mExitCommand;

    public LakysAwesomeRaycaster()
    {
        
    }
    
    public void startApp() {
        if (mCanvas == null) {
            mCanvas = new RaycasterCanvas();
            mCanvas.start();
            
        }

        mDisplay = Display.getDisplay(this);
        mDisplay.setCurrent(mCanvas);
    }

    public void pauseApp() {}

    public void destroyApp(boolean unconditional) {
        mCanvas.stop();
    }

    public void commandAction(Command c, Displayable s) {
        if (c.getCommandType() == Command.EXIT) {
            destroyApp(true);
            notifyDestroyed();
        }
    }

}
