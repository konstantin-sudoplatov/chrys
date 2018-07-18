package concepts.dyn;

import attention.Caldron;
import attention.ConceptNameSpace;
import chris.Crash;
import concepts.Concept;
import concepts.SCN;
import concepts.StaticAction;
import concepts.dyn.ifaces.TransientIface;
import concepts.dyn.neurons.Unconditional_nrn;
import concepts.dyn.premises.Peg_prem;

/**
 * This concept is used in the interplay of branches. From each side it is visible as a pair of notify-and-send-telegram action
 * and peg, signaled from the other side.
 * <p>After creation it has to be initialized with two seeds for corresponding branches and two pegs for each side. If telegram is
 * used, then it must also be made known to this concept (only its cid matters, no difference which instance in which caldron).
 * <p> After that this concept can be used only in the defined branches.
 * @author su
 */
public class Teleport_actn extends Action {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     */ 
    public Teleport_actn() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Notify the other side by activating the peg and calling the reasoning method on the destination branch (from the destination branch,
     * its _defaultProc_() method). If the telegram is set, it is passed over also. This method will determine on its own which
     * of the two branches is the destination.
     * @param nameSpace current name space (caldron).
     */
    @Override
    public void go(ConceptNameSpace nameSpace) {
        assert nameSpace == Thread.currentThread();
        
        // Determine our and the destination seeds
        long destSeedCid;
        long destPegCid;
        long currentSeedCid = ((Caldron)nameSpace).get_seed_cid();
        if
                (currentSeedCid == seedHereCid)
        {
            destSeedCid = seedThereCid;
            destPegCid = pegThereCid;
        }
        else if
                (currentSeedCid == seedThereCid)
        {
            destSeedCid = seedHereCid;
            destPegCid = pegHereCid;
        }
        else
            throw new Crash("Must be called from one of the two defined caldrons.");
        
        // Find the telegram object
        TransientIface telegram;
        if      // is telegram defined?
                (telegramCid != 0)
            telegram = (TransientIface)nameSpace.load_cpt(telegramCid);
        else
            telegram = null;
        
        // Call the notify branch static action 
        ((StaticAction)nameSpace.load_cpt(SCN.NotifyBranch_stat.name())).go(nameSpace, new long[]{destSeedCid, destPegCid}, telegram);
    }
    
    /**
     * Add new pair seed-premise. This method is called twice, once for our branch, the second time for the other branch.
     * @param seed seed of the destination branch.
     * @param premise premise on the destination branch.
     */
    public void add_destination(Unconditional_nrn seed, Peg_prem premise) {
        assert seed != null;
        assert premise != null;
        if      // here seed is still empty?
                (seedHereCid == 0)
        {//yes: set it up
            seedHereCid = seed.get_cid();
            pegHereCid = premise.get_cid();
        }
        else if // there seed is still empty?
                (seedThereCid == 0)
        {//yes: set it up
            seedThereCid = seed.get_cid();
            pegThereCid = premise.get_cid();
        }
        else
            throw new Crash("Trying to add third seed/peg pair.");
        
    }
    
    /**
     * Set telegram concept. If no telegram needs to be sent do not call this method, leave it unset.
     * @param telegram 
     */
    public void set_telegram(TransientIface telegram) {
        assert telegramCid == 0;
        telegramCid = ((Concept)telegram).get_cid();
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--

    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    private long seedHereCid;
    private long pegHereCid;
    private long seedThereCid;
    private long pegThereCid;
    private long telegramCid;       // 0 - no telegram will be sent
    
    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
