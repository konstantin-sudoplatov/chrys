package console;

import attention.Msg_ConsoleToAttnBubble;
import chris.Glob;
import chris.BaseMessage;
import chris.BaseMessageLoop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import chris.Msg_AppTermination;

/**
 * Takes lines from console and routes them to the application message loop.
 * @author su
 */
public class ConsoleLoop extends BaseMessageLoop {
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Protected
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    synchronized protected boolean _defaultProc_(BaseMessage msg) {
        
        if      // request for writing things?
                (msg instanceof Msg_WriteToConsole)
        {   // print it out on to the screen
            System.out.print(((Msg_WriteToConsole)msg).text);  // should contain feed line symbols
            return true;
        }
        else
            if      // request for reading from console?
                    (msg instanceof Msg_ReadFromConsole)
            {   // read and send
                while(true) {
                    System.out.print(">");
                    String line = readLine();
                    if      // empty line entered?
                            (line.equals("")) 
                    {
                        continue;
                    }
                    else if // termination requested?
                            (line.equalsIgnoreCase("P") || line.equalsIgnoreCase("STOP"))
                        {   // command the application loop to finish work
                            Glob.app_loop.put_in_queue(new Msg_AppTermination());
                            return true;
                        }
                        else {
                            Glob.app_loop.put_in_queue(new Msg_ConsoleToAttnBubble(ConsoleLoop.class, line));
                            return true;
                        }
                }
            }

        return false;
    }   // run()

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%--- private data ---%%%---%%%---%%%---%%%---%%%---%%%

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
    
    /**
     * Take a line from console.
     * @return 
     */
    private String readLine() {
        String стр = null;
        try {
            стр = br_Консоль.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ConsoleLoop.class.getName()).log(
                    Level.SEVERE, "Error while reading from console.", ex);
        }
        return стр;
    }
    private BufferedReader br_Консоль  = new BufferedReader(new InputStreamReader(System.in));
    
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
