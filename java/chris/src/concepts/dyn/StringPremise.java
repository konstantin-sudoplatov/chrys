package concepts.dyn;

/**
 * Premise, that bears a string. First used by "DynCptName.line_of_chat_string_prem".
 * If active (+1), the string has a meaningful value, if antiactive (-1), then string is
 * undefined.
 * @author su
 */
public class StringPremise extends BasePremise {

    /** 
     * Constructor.
     * @param string
     */ 
    public StringPremise(String string) { 
        strinG = string;
    } 
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^
    
    /**
     * Getter.
     * @return 
     */
    public String get_text() {
        return strinG;
    }

    /**
     * Setter.
     * @param string 
     */
    public void set_text(String string) {
        this.strinG = string;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%
    
    private String strinG;
}
