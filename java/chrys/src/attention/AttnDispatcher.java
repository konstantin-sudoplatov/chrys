package attention;

import concept.ComDir;
import java.util.ArrayList;
import java.util.List;

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
     *      Start attention flows.
     */
    public static void start() {
        List<AttnBubble> lstATB = ComDir.getATB();
        synchronized(lstATB) {
            for(AttnBubble bubble: lstATB) {
                Thread thr = new Thread(bubble);
                attnThread.add(thr);
                thr.start();
            }
        }
    }
    
    /**
     *          Wait for all threads to finish.
     */
    public static void join() {
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
    
    /** List of threads. So far it is one thread per flow and one flow per bubble. */
    private static List<Thread> attnThread = new ArrayList();
}
