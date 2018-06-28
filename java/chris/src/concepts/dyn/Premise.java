package concepts.dyn;

import concepts.Concept;
import concepts.DynamicConcept;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.ActivationImpl;
import concepts.dyn.ifaces.PropertyIface;
import java.util.HashSet;
import java.util.Set;

/**
 * Ancestor of all premises.
 * @author su
 */
abstract public class Premise extends DynamicConcept implements ActivationIface, PropertyIface {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** Constructor. */
    public Premise() {
    }

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public NormalizationType get_normalization_type() {
        return activatioN.get_normalization_type();
    }
   
    @Override
    public float get_activation() {
        return activatioN.get_activation();
    }

    @Override
    public void set_activation(float activation) {
        activatioN.set_activation(activation);
    }

    @Override
    public float normalize_activation() {
        return activatioN.normalize_activation();
    }
    
    @Override
    public int property_size() {
        if (propertieS == null) 
            return 0;
        else
            return propertieS.size();
    }

    @Override
    public long[] get_properties() {
        Long[] a = new Long[propertieS.size()];
        propertieS.toArray();
        long[] aa = new long[propertieS.size()];
        for(int i=0; i<a.length; i++)
            aa[i] = a[i];
        
        return aa;
    }

    @Override
    public long add_property(Concept cpt) {
        if(propertieS == null) propertieS = new HashSet();
        propertieS.add(cpt.get_cid());
        return cpt.get_cid();
    }

    @Override
    public void set_properties(Concept[] concepts) {
        if(propertieS == null) propertieS = new HashSet();
        for(Concept cpt: concepts)
            propertieS.add(cpt.get_cid());
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%


    /** Activation.  */
    private ActivationImpl activatioN = new ActivationImpl(NormalizationType.BIN);
    
    /** Set of cids, defining pertinent data . The cids are not forbidden to be duplicated in the premises. */
    private Set<Long> propertieS;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
   
}   // class
