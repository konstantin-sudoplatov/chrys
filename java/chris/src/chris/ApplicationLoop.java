package chris;

import attention.AttnDispMessage;
import console.ConsoleMessage;

/**
 * Application message loop. It is the main loop of the application and it works in the main thread.
 * @author su
 */
public class ApplicationLoop extends BaseMessageLoop {

    @Override
    public void start() {
        throw new Crash("This loop must be started calling the run() method in the application thread.");
    }

    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //                                  Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$ protected data ---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    @Override
    protected BaseMessageLoop _nextHop_(BaseMessage msg) {
        if
                (msg instanceof ConsoleMessage)
            return Glob.console_loop;
        else if
                (msg instanceof AttnDispMessage)
            return Glob.attn_disp_loop;
        else
            return null;
    }
    
    @Override
    synchronized protected boolean _defaultProc_(BaseMessage msg) {
        if      // request for termination the application?
                (msg instanceof Msg_AppTermination)
        {   // yes: stop the application
            Glob.terminate_application();
            return true;
        }

        switch(msg.getClass().getName()) {
            
        }
        
        // let the base class deal with an unrecognized message
        return false;
    }
    
    //---$$$---$$$---$$$---$$$---$$$--- protected классы ---$$$---$$$---$$$---$$$---$$$---



}
