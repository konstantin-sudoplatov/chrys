package attention;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *                                          Dispatcher of attention flows.
 * It holds and manages the set of attention bubbles and manages flows on those bubbles.
 * @author su
 */
public class AttnDispatcher {
    //##################################################################################################################
    //                                              Public types        
    
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /**
     *                                      Disable instantiation.
     * This class is supposed to contain only static members, it should not ever be instantiated.
     */
    private AttnDispatcher() {} 

    //##################################################################################################################
    //                                              Public methods

    /**
     * Add an entry to the ATB.
     * @param ab the bubble object to add.
     */
    public static synchronized void add_atb(AttnBubble ab) {
        ATB.add(ab);
    }

    /**
     *      Start attention flows.
     */
    public static synchronized void start() {
        ATB.forEach((AttnBubble _item) -> {
            Thread thr = new Thread(_item);
            attnThread.add(thr);
            thr.start();
        });
    }
    
    /**
     *          Wait for all threads to finish.
     */
    public static synchronized void join() {
        for(Thread thr: attnThread) {
            try {
                thr.join();
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
    
    /** Attention bubbles. */
    private static final List<AttnBubble> ATB = new ArrayList();
    
    /** List of threads. So far it is one thread per flow and one flow per bubble. */
    private static List<Thread> attnThread = new ArrayList();
}
