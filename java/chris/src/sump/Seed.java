package sump;

import attention.Caldron;
import chris.Glob;
import concepts.DynamicConcept;
import concepts.dyn.AssessionIface;
import concepts.dyn.EffectIface;
import concepts.dyn.PropertyIface;

/**
 * Container for a set of premises and corresponding set of effects. It implements the AssessionIface
 interface like the Neuron and what it does is to get the premises in the context, activate them,
 and get the effect in context, so making them ready for assertions.
 * @author su
 */
public class Seed extends DynamicConcept implements AssessionIface, EffectIface, PropertyIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** Constructor. */
    public Seed() {
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public long[] assess(Caldron context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long get_effect(int index) {
        return effectS[index];
    }

    @Override
    public long[] get_effects() {
        return effectS;
    }

    @Override
    public long add_effect(long cid) {
        effectS = Glob.append_array(effectS, cid);
        return cid;
    }

    @Override
    public void set_effects(long[] propArray) {
        this.effectS = propArray;
    }

    @Override
    public long get_property(int index) {
        return propertieS[index];
    }

    @Override
    public long[] get_properties() {
        return propertieS;
    }

    @Override
    public long add_property(long cid) {
        propertieS = Glob.append_array(propertieS, cid);
        return cid;
    }

    @Override
    public void set_properties(long[] propArray) {
        this.propertieS = propArray;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%
    
    /** Array of possible effects. */
    private long[] effectS;
    
    /** Array of cids, defining pertinent data. */
    private long[] propertieS;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
   
}   // class
