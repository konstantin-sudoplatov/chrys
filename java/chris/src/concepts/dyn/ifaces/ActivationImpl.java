package concepts.dyn.ifaces;

import attention.ConceptNameSpace;
import auxiliary.Lot;
import chris.Crash;
import chris.Glob;
import concepts.Concept;
import java.util.List;

/**
 * Implementation of the ActivationIface.
 * @author su
 */
public class ActivationImpl implements ActivationIface, Cloneable {

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** 
     * Constructor.
     * @param host
     * @param activType
     * @param normType
     */ 
    public ActivationImpl(Concept host, ActivationIface.ActivationType activType, ActivationIface.NormalizationType normType) { 
        hosT = host;
        this.activType = activType;
        this.normType = normType; 
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    @Override
    public ActivationImpl clone() {
        try {
            return (ActivationImpl)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new Crash("Cloning a concept failed.");
        }
    }
        
    @Override
    public NormalizationType get_normalization_type() {
        return normType;
    }

    @Override
    public float get_activation() {
        return activatioN;
    }

    @Override
    public void set_activation(float activation) {
        if      // activation is to be set for this concept?
                (activType == ActivationIface.ActivationType.SET)
            // set it
            activatioN = activation;
        else // crash
            throw new Crash("Activation cannot be set with concept of type " + activType.toString());
    }

    /**
     * Calculate weighed sum, normalize it according the neuron's type.
     * @param caldron
     * @return 
     */
    @Override
    public float calculate_activation(ConceptNameSpace caldron) {
        LotIface host;
        switch(activType) {
            case WEIGHED_SUM: // calculate the weighted sum
                host = (LotIface)hosT;
                double weightedSum = host.get_bias();
                for(int i = 0; i < host.size(); i++) {
                    Lot l = host.get_lot(i);
                    ActivationIface premCpt = (ActivationIface)caldron.get_cpt(l.cid);
                    float premActivation = premCpt.get_activation();
                    float weight = l.weight;
                    weightedSum += weight*premActivation;
                }
                activatioN = (float)weightedSum;

            normalize_activation();
            break;
            
            case AND:
                activatioN = 1;
                host = (LotIface)hosT;
                for(int i = 0; i < host.size(); i++) {
                    Lot l = host.get_lot(i);
                    ActivationIface premCpt = (ActivationIface)caldron.get_cpt(l.cid);
                    if (premCpt.get_activation() <= 0) {
                        activatioN = -1;
                        break;
                    }
                }
                break;

            case OR:
                activatioN = -1;
                host = (LotIface)hosT;
                for(int i = 0; i < host.size(); i++) {
                    Lot l = host.get_lot(i);
                    ActivationIface premCpt = (ActivationIface)caldron.get_cpt(l.cid);
                    if (premCpt.get_activation() > 0) {
                        activatioN = 1;
                        break;
                    }
                }
                break;
                
            case SET:
                throw new Crash("Activation cannot be calculated for concept of type " + activType.toString());
        }

        return activatioN;
    }

    @Override
    public float normalize_activation() {
        switch(normType) {
            case BIN:
                if (activatioN > 0)
                    activatioN = 1;
                else 
                    activatioN = -1;
                break;

            case SGN:    
                if (activatioN > 0)
                    activatioN = 1;
                else if (activatioN < 0)
                    activatioN = -1;
                else
                    activatioN = 0;
                break;
            
            case ESQUASH:
                activatioN = ((float)((1 - Math.exp(-activatioN))/(1 + Math.exp(-activatioN))));
                break;
                
            case NONE:
                break;
        }

        return activatioN;
    }

    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = Glob.create_list_of_lines(this, note, debugLevel);
        Glob.add_line(lst, String.format("normType = %s, activatioN = %s", normType.name(), activatioN));

        return lst;
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
    
    /** Component, that implements the interface using this object to transfer methods to. */
    private final Concept hosT;
    
    /** Activation type */
    private final ActivationIface.ActivationType activType;
    
    /** Normalization type. */
    private final ActivationIface.NormalizationType normType;
    
    /** Activation value. */
    private float activatioN = -1;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
