package attention;

import concept.stat.SCid;

/**
 *
 * @author su
 */
public class ConsoleListener extends AttnBubble {
    //##################################################################################################################
    //                                              Public types        
    
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     */ 
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ConsoleListener() 
    {   super(); 
        _lightCpt_(SCid.ConversationByConsole);     // light up conversation by console static concept to mark the fact of going conversation
    } 

    //##################################################################################################################
    //                                              Public methods
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        for(int i=0; ; i++) {
            System.out.println("attention.AttnFlow.run()");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
        }
    }

    //##################################################################################################################
    //                                              Protected data

    //##################################################################################################################
    //                                              Protected methods

    //##################################################################################################################
    //                                              Private data

    //##################################################################################################################
    //                                              Private methods, data
}
