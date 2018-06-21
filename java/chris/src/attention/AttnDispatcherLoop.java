package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.DynCptName;
import concepts.DynamicConcept;
import concepts.StatCptName;
import concepts.StaticConcept;
import concepts.stat.DummyMarker;
import console.ConsoleMessage;
import console.Msg_ReadFromConsole;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author su
 */
public class AttnDispatcherLoop extends BaseMessageLoop implements ConceptNameSpace {

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
     * @param circle the bubble, that contains the target directory or null if it is the common directory.
     * @param cptName concept name to put in the name directory or null
     * @return cid
     */
    @SuppressWarnings("UnnecessaryLabelOnContinueStatement")
    public synchronized long add_cpt(Concept cpt, AttnCircle circle, String cptName) {
        // determine cid
        long cid;
        if      // static concept?
                (cpt instanceof StaticConcept)
        {   //yes: get cid
            cid = StatCptName.valueOf(cpt.getClass().getSimpleName()).ordinal();
        }
        else 
        {   //no: generate a unique cid. it is unique through common and all bubble directories.
            GENERATE_CID: while(true) {
                Random rnd = new Random();
                cid = rnd.nextLong();
                if      // cid in the static range? push it out
                        (cid >= 0 && cid <= Glob.MAX_STATIC_CID)
                    cid += Glob.MAX_STATIC_CID + 1;
                if      // is in cpt?
                        (comDir.containsKey(cid))
                    continue GENERATE_CID;   // generate once more
                for(AttnCircle b: attnBubbleList) {
                    if      // is in PrivDir?
                            (b.concept_directory_containsKey(cid))
                        continue GENERATE_CID;
                }
                break;
            }   // while
            ((DynamicConcept)cpt).set_cid(cid);
            ((DynamicConcept)cpt).set_creation_time((int)new Date().getTime()/1000);
        }
        
        // put to target directories
        if      // is it a named concept?
                (cptName != null) 
        {   //yes: put the cid into the front and reverse directories
            Glob.named.name_cid.put(cptName, cid);
            Glob.named.cid_name.put(cid, cptName);
        }
        if      // the concept addressed to attention dispatcher?
                (circle == null)
        {   //yes: put into comDir
            comDir.put(cid, cpt);
        }
        else { //no: put into the addressed attention circle
            circle.put_in_concept_directory(cid, cpt);
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
      
    /**
     * Load a concept by cid from common to local directory. The name directories are updated too, if it is a named concept.
     * @param cid
     * @param circle an attention bubble loop.
     * @return cid
     * @throws Crash if the cid does not exists
     */
    public synchronized long copy_cpt_to_circle(long cid, AttnCircle circle) {

        if      //is there such a concept?
                (comDir.containsKey(cid))
        {   //yes: clone it and load to the bubble
            circle.put_in_concept_directory(cid, comDir.get(cid).clone());
            return cid;
        }
        else//no: crash
            throw new Crash("No concept in common directory with cid = " + cid);
    }
    
    /**
     * Load a concept by name from common to local directory. name_cid of the local directory is updated with the name and cid.
     * @param cptName
     * @param circle
     * @return cid
     * @throws Crash if the name does not exists
     */
    public synchronized long copy_cpt_to_circle(String cptName, AttnCircle circle) {
        Long cid = Glob.named.name_cid.get(cptName);
        if      // is there such named concept?
                (cid != null)
        {   // yes: load it to the bubble
            AttnDispatcherLoop.this.copy_cpt_to_circle(cid, circle);
            return cid;
        }
        else// no: crash
            throw new Crash("No concept in common directory with name = " + cptName);
    }

    @Override
    public synchronized Concept get_cpt(long cid) {
        Concept cpt = comDir.get(cid);
        if (cpt != null)
            return cpt;
        else
            throw new Crash("No such concept: cid = " + cid + ", name = " + Glob.named.cid_name.get(cid));
    }
    
    @Override
    public synchronized Concept get_cpt(String cptName) {
        Long cid = Glob.named.name_cid.get(cptName);
        if (cid != null) 
            return comDir.get(cid);
        else 
            throw new Crash("Now such concept: name = " + cptName);
    }
    
    /**
     *  Check to see if the common concept map contains a concept.
     * @param cid 
     * @return
     */
    public synchronized boolean comdir_containsKey(long cid) {
        return comDir.containsKey(cid);
    }

    @Override
    public synchronized void request_termination() {
        
        // terminate bubbles
        for (AttnCircle bubble : attnBubbleList) {
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
                (msg instanceof Msg_ConsoleToAttnCircle)
        {
            // May be, create an attention circle for the chat
            if
                    (consoleChatCircle == null)
            {
                consoleChatCircle = new AttnCircle(this, DynCptName.it_is_console_chat_prem);
                addCircleToList(consoleChatCircle);
                consoleChatCircle.start_thread();
            }
            
            // route the message to the chat circle
            consoleChatCircle.put_in_queue(msg);

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
    
    /** Concept by cid directory: a map of a concepts by their cids. */
    public final Map<Long, Concept> comDir = new HashMap<>();
    
    /** List of all attention bubbles */
    private final ArrayList<AttnCircle> attnBubbleList = new ArrayList<>();
    
    /** Attention bubble, that chats with console. */
    private AttnCircle consoleChatCircle;
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     * Synchronized adding new bubble to the list of bubbles. We need synchronization if someone from outside the thread wants
     * to work with the list of bubbles (for example, wants to find a bubble to add a new concept to it).
     * @param bubble attention bubble loop to add to the list.
     */
    private synchronized void addCircleToList(AttnCircle bubble) {
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
        for(StatCptName cidEnum: StatCptName.values()) {
            String cptName = cidEnum.name();
            if      // concept name starts with "Mrk_"?
                    (cptName.substring(0, 4).equals("Mrk_"))
            {   //yes: it is a marker, it does not require an object. We put into the concept directory a dummy - Mrk_Unimarker
                // object for all markers
                dummyMarker.cid = cidEnum.ordinal();
                continue;
            }
            @SuppressWarnings("UnusedAssignment")
            Class cl = null;
            try {
                cl = Class.forName(StatCptName.STATIC_CONCEPTS_PACKET_NAME + "." + cptName);
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
