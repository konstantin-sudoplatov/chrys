package attention;

import chris.BaseMessage;
import chris.BaseMessageLoop;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import concepts.DynCptName;
import concepts.DynamicConcept;
import concepts.StatCptName;
import concepts.StaticAction;
import concepts.dyn.Neuron;
import concepts.dyn.ifaces.GlobalConcept;
import console.ConsoleMessage;
import console.Msg_ReadFromConsole;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
     * @param circle the bubble, that caldir_contains_key the target directory or null if it is the common directory.
     * @param cptName concept name to put_caldron in the name directory or null
     * @return cid
     */
    @SuppressWarnings("UnnecessaryLabelOnContinueStatement")
    public synchronized long add_cpt(Concept cpt, AttnCircle circle, String cptName) {
        // determine cid
        long cid;
        if      // static concept?
                (cpt instanceof StaticAction)
        {   //yes: get_caldron cid
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
                if      // is in cpt?ivate
                        (comDir.containsKey(cid))
                    continue GENERATE_CID;   // generate once more
                for(Caldron caldron: caldronMap.values()) {
                    if      // is in PrivDir?
                            (caldron.cpt_exists(cid))
                        continue GENERATE_CID;
                }
                break;
            }   // while
            if(((DynamicConcept)cpt).get_cid() != 0)
                throw new Crash("Concept already has a cid:\n" + cpt.to_list_of_lines() + "\nConcept cannot be assigned with a cid twice.");
            ((DynamicConcept)cpt).set_cid(cid);
            ((DynamicConcept)cpt).set_creation_time((int)(new Date().getTime()/1000));
        }
        
        if (Glob.assertions_enabled()) cpt.name_space = this;
        
        // put_caldron to target directories
        if      // is it a named concept?
                (cptName != null) 
        {   //yes: put_caldron the cid into the front and reverse directories
            if      // does this name already exist?
                    (Glob.named.name_cid.containsKey(cptName))
                //yes: that is a crime, crash
                throw new Crash("Concept " + cptName + " already exists. Attempt to create the same concept.");
            
            if (Glob.named.name_cid.put(cptName, cid) != null)
                throw new Crash(cptName + " is already present in the Glob.named.name_cid");
            if (Glob.named.cid_name.put(cid, cptName) != null)
                throw new Crash("cid " + cid + " is already present in the Glob.named.cid_name" );
            if (Glob.assertions_enabled()) cpt.concept_name = cptName;  // for debugging
        }
        if      // the concept addressed to attention dispatcher?
                (circle == null)
        {   //yes: put_caldron into comDir
            comDir.put(cid, cpt);
        }
        else { //no: put_caldron into the addressed attention circle
            if      // it is not global, i.e. can be cloned to a caldron?
                    (!(cpt instanceof GlobalConcept))
                circle.put_in_concept_directory(cid, cpt);
            else
                throw new Crash(String.format("Attempt to put global concept\n %s\n into local namespase\n %s",
                        cpt.to_list_of_lines("", 10), circle.to_list_of_lines("circle", 10)));
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
     * @throws Crash if the cid does not cpt_exists
     */
    public synchronized long copy_cpt_to_circle(long cid, AttnCircle circle) {
        Concept cpt = comDir.get(cid);
        if      //is there such a concept?
                (cpt != null)
        {   //yes: clone it and load to the circle
            if      // it is not global, i.e. can be cloned to a caldron?
                    (!(cpt instanceof GlobalConcept))
            {
                cpt = comDir.get(cid).clone();
                assert true: cpt.name_space = circle;
                circle.put_in_concept_directory(cid, cpt);
            }
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
     * @throws Crash if the name does not cpt_exists
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
    public synchronized Concept load_cpt(long cid) {
        Concept cpt = comDir.get(cid);
        if (cpt != null)
            return cpt;
        else
            throw new Crash("No such concept: cid = " + cid + ", name = " + Glob.named.cid_name.get(cid));
    }
    
    @Override
    public synchronized Concept load_cpt(String cptName) {
        Long cid = Glob.named.name_cid.get(cptName);
        if (cid != null) 
            return comDir.get(cid);
        else 
            throw new Crash("Now such concept: name = " + cptName);
    }
    
    @Override
    public synchronized boolean cpt_exists(long cid) {
        return comdir_containsKey(cid);
    }
    
    @Override
    public synchronized boolean cpt_exists(String cptName) {
        Long cid = Glob.named.name_cid.get(cptName);
        if (cid != null)
            return this.cpt_exists(cid);
        else
            return false;
    }

    @Override
    public AttnCircle get_attn_circle() {
        throw new Crash("Not supported for AttnDispatcherLoop.");
    }
    
    /**
     *  Check to see if the common concept map caldir_contains_key a concept.
     * @param cid 
     * @return
     */
    public synchronized boolean comdir_containsKey(long cid) {
        return comDir.containsKey(cid);
    }

    /**
     * Get caldir_size.
     * @return 
     */
    synchronized public int caldir_size() {
        return caldronMap.size();
    }
    
    /**
     * Find if a cid in the map.
     * @param seed seed main seed of the branch as its identifier
     * @return
     */
    synchronized public boolean caldir_contains_key(Concept seed) {
        return caldronMap.containsKey(seed.get_cid());
    }
    
    /**
     * Get caldron from the caldron map.
     * @param seed seed main seed of the branch as its identifier
     * @return 
     */
    synchronized public Caldron get_caldron(Concept seed) {
        return caldronMap.get(seed.get_cid());
    }
    
    /**
     * Add an entry to the caldron map.
     * @param seed seed main seed of the branch as its identifier
     * @param caldron
     * @return previous caldron or null.
     */
    synchronized public Caldron put_caldron(Concept seed, Caldron caldron) {
        return caldronMap.put(seed.get_cid(), caldron);
    }
    
    /**
     * Remove entry.
     * @param seed seed main seed of the branch as its identifier
     * @return removed caldron.
     */
    synchronized public Caldron remove_caldron(Concept seed) {
        return caldronMap.remove(seed.get_cid());
    }

    @Override
    public synchronized void request_termination() {
        
        // terminate circles
        for (Caldron caldron : caldronMap.values()) {
            if 
                    (caldron.isAlive())
            {
                try {
                    caldron.request_termination();
                    caldron.join();
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
        Glob.app_loop.put_in_queue(new Msg_ReadFromConsole());        
    }

    @Override
    synchronized protected boolean _defaultProc_(BaseMessage msg) {
        
        // The first call create an attention circle for the chat
        if      //is it the first pass through?
                (consoleCaldron == null)
        {//yes: create and start the chat caldron and its branches
            // Create and start chat and branches. That process will add to the caldron map.
            if      //the chat caldron is not created yet?
                    (get_caldron(load_cpt(DynCptName.chat_main_seed_uncnrn.name())) == null)
                //no, not yet: create and start it
                new AttnCircle((Neuron)load_cpt(DynCptName.chat_main_seed_uncnrn.name()), 
                        this, DynCptName.it_is_console_chat_prem).start();

            // extract the console branch
            consoleCaldron = get_caldron(load_cpt(DynCptName.console_main_seed_uncnrn.name()));
            if      //the console caldron is not created yet?
                    (consoleCaldron == null)
            {//no, not yet: rebounce the message
                this.put_in_queue(msg);
                return true;
            }
        }

        // Proccess the message
        if      // is it a message from console to the circle?
                (msg instanceof Msg_ConsoleToAttnCircle)
        {//yes: reroute the message to the console circle
            consoleCaldron.put_in_queue(msg);
            return true;
        }
        else if // is it a message to console (probably from this circle)?
                (msg instanceof ConsoleMessage)
        {   // reroute to the application loop, it'll route it to console
            Glob.app_loop.put_in_queue(msg);
            return true;
        }
        
        return false;
    }
    private Caldron consoleCaldron;
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Concept by cid directory: a map of a concepts by their cids. */
    public final Map<Long, Concept> comDir = new HashMap<>();
    
    /** cid-caldron map, where "cid" is the cid of the seed neuron of 
      the reasoning branch as an identifier of the caldron. We would need synchronization, 
      because the map can be concurrently accessed from different caldrons. */
    private Map<Long, Caldron> caldronMap = new HashMap();
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /**
     *  Load from DB or create static concept objects and put_caldron them into the common concepts map.
     */
    private void loadStaticConcepts() {
        
        // Load cpt from DB
        
        // Load CPN from DB
        
        // Create static concepts.
        for(StatCptName cidEnum: StatCptName.values()) {
            String cptName = cidEnum.name();
            if      // concept name starts with "Mrk_"?
                    (cptName.substring(0, 4).equals("Mrk_"))
            {   //yes: it is a marker, it does not require an object.
                continue;
            }
            @SuppressWarnings("UnusedAssignment")
            Class cl = null;
            try {
                cl = Class.forName(StatCptName.STATIC_CONCEPTS_PACKET_NAME + "." + cptName);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(StaticAction.class.getName()).log(Level.SEVERE, "Error getting class " + cptName, ex);
                System.exit(1);
            }
            @SuppressWarnings("UnusedAssignment")
            Constructor cons = null;
            try {
                cons = cl.getConstructor();
            } catch (NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(StaticAction.class.getName()).log(Level.SEVERE, "Error getting constractor for " + cptName, ex);
                System.exit(1);
            }
            @SuppressWarnings("UnusedAssignment")
            Concept cpt = null;
            try {
                cpt = (Concept)cons.newInstance();
                add_cpt(cpt, cptName);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(StaticAction.class.getName()).log(Level.SEVERE, "Error instantiating " + cptName, ex);
                System.exit(1);
            }
        }
    }
    
    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
