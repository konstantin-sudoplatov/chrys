package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Glob;
import concepts.Concept;
import concepts.DynamicConcept;
import concepts.StatCptEnum;
import concepts.StaticConcept;
import console.ConsoleMessage;
import console.Msg_ReadFromConsole;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author su
 */
public class AttnDispatcherLoop extends BaseMessageLoop {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.     */ 
    public AttnDispatcherLoop() {
        loadStaticConcepts();
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Add concept to comdir or to a bubble dir. Static concepts know their cid already, for dynamic concept it is randomly 
     * generated outside the static range.
     * @param cpt the concept to add
     * @param bubble the bubble, that contains the target directory or null if it is the comdir.
     * @return cid
     */
    @SuppressWarnings("UnnecessaryLabelOnContinueStatement")
    public synchronized long add_cpt(Concept cpt, AttnBubbleLoop bubble) {
        // determine cid
        long cid;
        if      // static concept?
                (cpt instanceof StaticConcept)
        {   //yes: get cid
            cid = StatCptEnum.valueOf(cpt.getClass().getSimpleName()).ordinal();
        }
        else 
        {   //no: generate a unique cid. it is unique all through common and all bubble directories.
            GENERATE_CID: while(true) {
                Random rnd = new Random();
                cid = rnd.nextLong();
                if      // cid in the static range? push it out
                        (cid >= 0 && cid <= Glob.MAX_STATIC_CID)
                    cid += Glob.MAX_STATIC_CID + 1;
                if      // is in cpt?
                        (comConDir.containsKey(cid))
                    continue GENERATE_CID;   // generate once more
                for(AttnBubbleLoop b: attnBubbleList) {
                    if      // is in PrivDir?
                            (b.cptdir_containsKey(cid))
                        continue GENERATE_CID;
                }
                break;
            }   // while
            ((DynamicConcept)cpt).setCiD(cid);
        }
        
        // put to target dir
        if
                (bubble == null)
            comConDir.put(cid, cpt);      // put in cpt
        else
            bubble.cptdir_put(cid, cpt);
        
        return cid;
    }

    /**
     * Ditto.
     * @param cpt  the concept to add
     */
    public synchronized void add_cpt(Concept cpt) {
        add_cpt(cpt, null);
    }
        
    /**
     *  Check to see if the common concept map contains a concept.
     * @param cid 
     * @return
     */
    public synchronized boolean comdir_containsKey(long cid) {
        return comConDir.containsKey(cid);
    }

    @Override
    public synchronized void request_termination() {
        
        // terminate bubbles
        for (AttnBubbleLoop bubble : attnBubbleList) {
            bubble.request_termination();
        }
        
        // terminate yourself
        super.request_termination();
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    @Override
    protected void _afterStart_() {

        // prompt console
        Glob.app_loop.put_in_queue(new Msg_ReadFromConsole(AttnDispatcherLoop.class));        
    }

    @Override
    protected boolean _defaultProc_(BaseMessage msg) {
        if      // a message from console to bubble?
                (msg instanceof Msg_ConsoleToAttnBubble)
        {
            // May be, create the chat bubble
            if
                    (consoleChatBubble == null)
            {
                consoleChatBubble = new AttnBubbleLoop(this);
                addBubbleToList(consoleChatBubble);
                consoleChatBubble.start_thread();
            }
            
            // route the message to the cat bubble
            consoleChatBubble.put_in_queue(msg);

            return true;
        }
        else if // a message to console (probably from a bubble)?
                (msg instanceof ConsoleMessage)
        {   // reroute to the application loop, it'll route it to console
            Glob.app_loop.put_in_queue(msg);
            return true;
        }
        
        return false;
    }
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** Common concept directory: a map of concepts by cid's. */
    private final Map<Long, Concept> comConDir = new HashMap<>();
    
    /** List of all attention bubbles */
    private final ArrayList<AttnBubbleLoop> attnBubbleList = new ArrayList<>();
    
    /** Attention bubble, that chats with console. */
    private AttnBubbleLoop consoleChatBubble;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     * Synchronized adding new bubble to the list of bubbles. We need synchronization if someone from outside the thread wants
     * to work with the list of bubbles (for example, wants to find a bubble to add a new concept to it).
     * @param bubble attention bubble loop to add to the list.
     */
    private synchronized void addBubbleToList(AttnBubbleLoop bubble) {
        attnBubbleList.add(consoleChatBubble);
    }

    /**
     *  Load from DB or create static concept objects and put them into the common concepts map.
     */
    private void loadStaticConcepts() {
        
        // Load cpt from DB
        
        // Load CPN from DB
        
        // Create static concepts.
        for(StatCptEnum cidEnum: StatCptEnum.values()) {
            String cptName = cidEnum.name();
            @SuppressWarnings("UnusedAssignment")
            Class cl = null;
            try {
                cl = Class.forName(StatCptEnum.STATIC_CONCEPTS_PACKET_NAME + "." + cptName);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(StaticConcept.class.getName()).log(Level.SEVERE, "Error getting class " + cptName, ex);
                System.exit(1);
            }
            @SuppressWarnings("UnusedAssignment")
            Constructor cons = null;
            try {
                cons = cl.getConstructor();
            } catch (NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(StaticConcept.class.getName()).log(Level.SEVERE, "Error getting constractor for " + cptName, ex);
                System.exit(1);
            }
            @SuppressWarnings("UnusedAssignment")
            Concept cpt = null;
            try {
                cpt = (Concept)cons.newInstance();
                add_cpt(cpt);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(StaticConcept.class.getName()).log(Level.SEVERE, "Error instantiating " + cptName, ex);
                System.exit(1);
            }
        }
    }
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
    
}
