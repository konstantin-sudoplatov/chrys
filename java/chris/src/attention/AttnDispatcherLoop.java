package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Glob;
import concepts.Concept;
import concepts.ConceptDirectory;
import concepts.DynamicConcept;
import concepts.StatCptEnum;
import concepts.StaticConcept;
import concepts.stat.DummyMarker;
import console.ConsoleMessage;
import console.Msg_ReadFromConsole;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;

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
     * Add a concept to the common or a bubble directory. Static concepts know their cid already, for dynamic concept it is randomly 
     * generated outside the static range.
     * @param cpt the concept to add
     * @param bubble the bubble, that contains the target directory or null if it is the common directory.
     * @param cptName concept name to put in the name directory or null
     * @return cid
     */
    @SuppressWarnings("UnnecessaryLabelOnContinueStatement")
    public synchronized long add_cpt(Concept cpt, AttnBubbleLoop bubble, String cptName) {
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
                        (comDir.cpt_dir.containsKey(cid))
                    continue GENERATE_CID;   // generate once more
                for(AttnBubbleLoop b: attnBubbleList) {
                    if      // is in PrivDir?
                            (b.cpt_dir_containsKey(cid))
                        continue GENERATE_CID;
                }
                break;
            }   // while
            ((DynamicConcept)cpt).set_cid(cid);
        }
        
        // put to target dir
        if
                (bubble == null)
        {
            comDir.cpt_dir.put(cid, cpt);
            if (cptName != null) comDir.name_dir.put(cptName, cid);
        }
        else {
            bubble.cpt_dir_put(cid, cpt);
            if (cptName != null) bubble.name_dir_put(cptName, cid);
        }
        
        return cid;
    }

    /**
     * Ditto.
     * @param cpt  the concept to add
     * @param name the name of the concept
     * @return cid
     */
    public synchronized long add_cpt(Concept cpt, String name) {
        return add_cpt(cpt, null, name);
    }

    /**
     * Ditto.
     * @param cpt  the concept to add
     * @return cid
     */
    public synchronized long add_cpt(Concept cpt) {
        return add_cpt(cpt, null, null);
    }
      
    public synchronized long copy_cpt_to_bubble(long cid, AttnBubbleLoop bubble) {
        
        throw new UnsupportedOperationException("not realized yet");
//        return cid;
    }
    
    /**
     *  Check to see if the common concept map contains a concept.
     * @param cid 
     * @return
     */
    public synchronized boolean comdir_containsKey(long cid) {
        return comDir.cpt_dir.containsKey(cid);
    }

    @Override
    public synchronized void request_termination() {
        
        // terminate bubbles
        for (AttnBubbleLoop bubble : attnBubbleList) {
            Thread thread = bubble.get_thread();
            if 
                    (thread.isAlive())
            {
                try {
                    bubble.request_termination();
                    thread.join();
                } catch (InterruptedException ex) {}
            }
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
    synchronized protected boolean _defaultProc_(BaseMessage msg) {
        if      // is it a message from console to bubble?
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
            
            // route the message to the chat bubble
            consoleChatBubble.put_in_queue(msg);

            return true;
        }
        else if // is it a message to console (probably from a bubble)?
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

    /** Common concept directory: a map of concepts by cids. */
    private final ConceptDirectory comDir = new ConceptDirectory();
    
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
        attnBubbleList.add(bubble);
    }

    /**
     *  Load from DB or create static concept objects and put them into the common concepts map.
     */
    private void loadStaticConcepts() {
        
        // Load cpt from DB
        
        // Load CPN from DB
        
        // Create static concepts.
        DummyMarker dummyMarker = new DummyMarker();
        for(StatCptEnum cidEnum: StatCptEnum.values()) {
            String cptName = cidEnum.name();
            if      // concept name starts with "Mrk_"?
                    (cptName.substring(0, 4).equals("Mrk_"))
            {   //yes: it is a marker, it does not require an object. We put into the concept directory a dummy - Mrk_Marker
                // object for all markers
                dummyMarker.cid = cidEnum.ordinal();
                continue;
            }
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
