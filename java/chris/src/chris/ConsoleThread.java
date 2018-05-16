package chris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Msg_AppTermination;

/**
 * Takes lines from console and routes them to the application message loop.
 * @author su
 */
public class ConsoleThread extends Thread {
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public void run() {
        while(true) {
            System.out.print(">");
            String line = readLine();
            
            if      // termination requested?
                    (line.equalsIgnoreCase("П") || line.equalsIgnoreCase("P") ||
                    line.equalsIgnoreCase("СТОП") || line.equalsIgnoreCase("STOP"))
            {   // command the application loop to finish work
                Glob.app_loop.put_in_queue(new Msg_AppTermination());
                break;
            }
            else if (line.equals("")) continue;
            else {
                System.out.println(
                        "p, п, stop, стоп   - leave\n" +
                        "?                  - help\n"
                );
            }
                
        }   // while
        
        System.out.println("Shutting down...");
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
            Logger.getLogger(ConsoleThread.class.getName()).log(
                    Level.SEVERE, "Error while reading from console.", ex);
        }
        return стр;
    }
    private BufferedReader br_Консоль  = new BufferedReader(new InputStreamReader(System.in));
    
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
