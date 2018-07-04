package concepts.stat;

import attention.ConceptNameSpace;
import concepts.Concept;
import concepts.StatCptName;
import concepts.StaticAction;
import concepts.dyn.stocks.ListStock;

/**
 * Adding a copy of element to a list. Example, adding a chat line to the chat log.
 * @author su
 */
public class CloneConceptAndAappendToList_stat extends StaticAction {

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                                  Public methods
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Clone an element and add it to a list. It is a binary operation on a list and an element 
     * without returning direct result. The element NOT added to the list directly.The concept of element is cloned
     * @param nameSpace caldron, in which thread this function would be invoked.
     * @param paramCids [0] - dictionary, [1] - element
     * @param extra null
     * @return null
     */
    @Override
    public long[] go(ConceptNameSpace nameSpace, long[] paramCids, Object extra) {
        
        // Get params
        ListStock lst = (ListStock)nameSpace.get_cpt(paramCids[0]);
        Concept elem = nameSpace.get_cpt(paramCids[1]);     // just to check that the concept exists
        
        // Clone the element
        CloneConcept_stat cloneCpt = (CloneConcept_stat)nameSpace.get_cpt(StatCptName.CloneConcept_stat.name());
        long clonedElemCid = cloneCpt.go(nameSpace, new long[]{paramCids[1]}, null)[0]; // returns array of only one cid
        lst.append_member(nameSpace.get_cpt(clonedElemCid));

        return null;
    }
}
