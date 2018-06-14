package auxiliary;

/** 
 * Structure of premises. A pair of weight of a concept and its cid. 
 */
public class Premise {
    public float weight;    // Weight with which this cid takes part in the weighted sum.
    public long cid;
    public Premise(float weight, long cid) {
        this.weight = weight;
        this.cid = cid;
    }
}
