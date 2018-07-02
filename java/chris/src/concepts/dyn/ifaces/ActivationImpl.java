package concepts.dyn.ifaces;

import chris.Crash;
import chris.Glob;
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
     * @param normType
     */ 
    public ActivationImpl(ActivationIface.NormalizationType normType) { 
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
        activatioN = activation;
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
        List<String> lst = Glob.create_list_of_lines(this, note);
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
    
    /** Normalization type. */
    private final ActivationIface.NormalizationType normType;
    
    /** Activation value. */
    private float activatioN = -1;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
}
