package concepts.dyn.premises;

import attention.Caldron;
import chris.Crash;
import chris.Glob;
import concepts.dyn.primitives.Set_prim;
import java.util.List;

/**
 * A peg with link to its owner caldron. Used in the inter caldron notification. Its mate is the Link_actn.
 * @author su
 */
public class Link_prem extends Peg_prem {

    /** 
     * Constructor.
     */ 
    public Link_prem() { 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Activate the premise and transfer its activation onto the agregator.
     */
    public void activate() {
        if (agregatoR == null)
            throw new Crash("The agregate premise must be set before activation.");
        super.set_activation(1);
        if
                (agregatoR instanceof And_prem)
            ((And_prem)agregatoR).decrement_green_count();
        else if 
                (agregatoR instanceof Or_prem)
            ((Or_prem)agregatoR).calculate_activation();
        else
            throw new Crash("Currently supported only for And_prem and Or_prem.");
    }

    /**
     * Antiactivate the premise and transfer its activation onto the agregator.
     */
    public void antiactivate() {
        if (agregatoR == null)
            throw new Crash("The agregate premise must be set before activation.");
        super.set_activation(-1);
        if
                (agregatoR instanceof And_prem)
            ((And_prem)agregatoR).calculate_activation();
        else if 
                (agregatoR instanceof Or_prem)
            ((Or_prem)agregatoR).calculate_activation();
        else
            throw new Crash("Currently supported only for And_prem and Or_prem.");
    }
    
    /**
     * Become a member of the agregate premise.
     * @param agregator 
     */
    public void join_agregator(Set_prim agregator) {
        agregator.add_member(this);
        agregatoR = agregator;
    }

    /**
     * Leave the agregate premise club.
     * @param agregator 
     */
    public void leave_agregator(Set_prim agregator) {
        agregator.remove_member(this);
        agregatoR = null;
    }
    
    /**
     * Getter.
     * @return the agregate premise. 
     */
    public Set_prim get_agregator() {
        return agregatoR;
    }
    
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        if (debugLevel > 0) {
            Glob.add_list_of_lines(lst, agregatoR.to_list_of_lines("agregatoR", debugLevel-1));
        }
        
        return lst;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    /** An agregate premise like And_prem or Or_prem which contains this premise as a member. */
    private Set_prim agregatoR;
}
