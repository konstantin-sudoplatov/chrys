package concepts.dyn.sample_primitives;

import concepts.dyn.Primitive;

/**
 * Any string of text without any limitations.
 * @author su
 */
public class JustString extends Primitive {

    /** 
     * Constructor.
     * @param text string of symbols.
     */ 
    public JustString(String text) { 
        this.text = text;
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
        return text;
    }

    /**
     * Setter.
     * @param text 
     */
    public void set_text(String text) {
        this.text = text;
    }
    
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data ---%%%---%%%---%%%---%%%---%%%---%%%

    private String text;

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
}
