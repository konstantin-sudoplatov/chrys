package concepts.stat;

import attention.AttnCircle;
import attention.ConceptNameSpace;
import concepts.Concept;
import concepts.StaticAction;

/**
 * Clone a concept and put the clone into the name space.
 * @author su
 */
public class CloneConcept_stat extends StaticAction {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Clone a concept and put the clone into the name space. It is an unary operation, where operand
     * is the template concept.
     * @param nameSpace caldron, in which thread this function would be invoked.
     * @param paramCids [0] - source concept's cid.
     * @param extra null
     * @return cloned concept's cid
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        
        Concept cpt = nameSpace.get_cpt(paramCids[0]);
        cpt = cpt.clone();
        AttnCircle attnCircle = nameSpace.get_attn_circle();
        long cptCid = attnCircle.get_attn_dispatcher().add_cpt(cpt, attnCircle, null);
        
        return new long[] {cptCid};
    }
}
