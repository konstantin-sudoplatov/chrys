package concepts.dyn.primitives;

import concepts.Concept;
import concepts.dyn.ifaces.TransientIface;

/**
 * Contains a string.
 * @author su
 */
public class TransientString_prim extends String_prim implements TransientIface {

    /** 
     * Constructor.
     */ 
    public TransientString_prim() { 
    } 
    
    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    @Override
    public void follow(Concept src) {
        String_prim srcCpt = (String_prim)src;
        set_string(srcCpt.get_string());
    }

}
