package concepts.dyn.sample_primitives;

/**
 * A string with a special meaning. The meaning defined by the nested property cid.
 * @author su
 */
public class CiddedString extends CiddedNothing {

    /** 
     * Constructor.
     * @param nestedCid Meaning of the text field. It can be a marker or, for example, a primitive of CiddedString, 
     * containing the Marker concept and a text, that specializes it, or anything else.
     * @param text value of this concept.
     */ 
    public CiddedString(long nestedCid, String text) 
    {   super(nestedCid); 
        this.texT = text;
    } 

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
    /**
     * Getter.
     * @return 
     */
    public String get_text() {
        return texT;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%

    private String texT;

}   // class
