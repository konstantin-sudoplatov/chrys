package concepts.dyn.neurons;

import attention.ConceptNameSpace;
import auxiliary.Lot;
import chris.Glob;
import concepts.dyn.Neuron;
import concepts.dyn.ifaces.ActivationIface;
import concepts.dyn.ifaces.LotIface;
import concepts.dyn.ifaces.LotImpl;
import java.util.List;

/**
 * Neuron, that has an implementation of a lot interface and implements the _calculateActivation_()
 * method as a weighed sum of premise's activations.
 * @author su
 */
public class WeighedSum_nrn extends Neuron implements LotIface{

    //---***---***---***---***---***--- public classes ---***---***---***---***---***---***

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** Constructor. */
    public WeighedSum_nrn() {}

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public WeighedSum_nrn clone() {
        WeighedSum_nrn clone = (WeighedSum_nrn)super.clone();
        if (lotS != null) clone.lotS = (LotImpl)lotS.clone();
        
        return clone;
    }
    
    @Override
    public NormalizationType get_normalization_type() {
        return NormalizationType.ESQUASH;
    }
    
    @Override
    public int lot_size() {
        return lotS.lot_size();
    }
    
    @Override
    public Lot get_lot(int index) {
        return lotS.get_lot(index);
    }

    @Override
    public Lot[] get_lots() {
        return lotS.get_lots();
    }

    @Override
    public Lot add_lot(Lot lot) {
        return lotS.add_lot(lot);
    }

    @Override
    public void set_lots(Lot[] lots) {
        lotS.set_lots(lots);
    }

    @Override
    public float get_bias() {
        return lotS.get_bias();
    }

    @Override
    public void set_bias(float bias) {
        lotS.set_bias(bias);
    }
        
    /**
     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
     * @param note printed in the first line just after the object type.
     * @param debugLevel 0 - the shortest, 2 - the fullest
     * @return list of lines, describing this object.
     */
    @Override
    public List<String> to_list_of_lines(String note, Integer debugLevel) {
        List<String> lst = super.to_list_of_lines(note, debugLevel);
        if (debugLevel == 0)
            Glob.add_line(lst, String.format("lot_size() = %s", lot_size()));
        else if(debugLevel > 0) {
            Glob.add_list_of_lines(lst, lotS.to_list_of_lines("lotS", debugLevel-1));
        }

        return lst;
    }
    
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$
    //
    //      Protected    Protected    Protected    Protected    Protected    Protected
    //
    //~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$~~~$$$

    //---$$$---$$$---$$$---$$$---$$$--- protected data $$$---$$$---$$$---$$$---$$$---$$$--
    
    //---$$$---$$$---$$$---$$$---$$$--- protected methods ---$$$---$$$---$$$---$$$---$$$---

    @Override
    protected float _calculateActivation_(ConceptNameSpace caldron) {

        // Calculate
        double weightedSum = get_bias();
        for(int i = 0; i < lot_size(); i++) {
            Lot l = get_lot(i);
            ActivationIface premise = (ActivationIface)caldron.get_cpt(l.cid);
            float premActivation = premise.get_activation();
            float weight = l.weight;
            weightedSum += weight*premActivation;
        }
        float wS = (float)weightedSum;
        
        // Normalize as exponential squash
        _activation_ = ((float)((1 - Math.exp(-wS))/(1 + Math.exp(-wS))));;

        return _activation_;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--

    /** Weights and premises. */
    private LotImpl lotS = new LotImpl();

    //---%%%---%%%---%%%---%%%---%%% private classes ---%%%---%%%---%%%---%%%---%%%---%%%--
   
}   // class
