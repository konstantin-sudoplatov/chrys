package attention;

import concept.Concept;
import java.util.HashMap;
import java.util.Map;

/**
 * Data store for the attention flow thread(s). 
 * It holds all data, which an attention flow needs to do its reasoning, first of all the concept references.
 * @author su
 */
public class AttnBubble implements Runnable {

    //##################################################################################################################
    //                                              Public types        
    public enum Type {
        CONSOLE_LISTENER
    }
    
    //##################################################################################################################
    //                                              Public data

    /** Private concept directory: a concept object by its Id. */
    private Map<Long, Concept> privDir = new HashMap();

    /** Type of the bubble. */
    public final Type bubbleType;
    
    //##################################################################################################################
    //                                              Constructors

    /** 
     *  Constructor.
     * @param bubbleType
     */ 
    public AttnBubble(Type bubbleType) { 
        this.bubbleType = bubbleType;
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

//    public long getBid() {
//        return bid;
//    }
//
//    public void setBid(long bid) {
//        this.bid = bid;
//    }
//
//    public Map<Long, Concept> getPrivDir() {
//        return privDir;
//    }
    
    //##################################################################################################################
    //                                              Protected data

    //##################################################################################################################
    //                                              Protected methods

    //##################################################################################################################
    //                                              Private data
//    /** Bubble Id. Initialized by an illegal ID to show it is not yet generated. */
//    private long bid = -1;

    //##################################################################################################################
    //                                              Private methods, data
}
