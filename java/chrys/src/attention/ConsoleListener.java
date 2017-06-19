package attention;

import concept.ComDir;
import concept.DynamicConcept;
import concept.stat.SCid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author su
 */
public class ConsoleListener extends AttnBubble {
    //##################################################################################################################
    //                                              Public types        
    
    //##################################################################################################################
    //                                              Public data

    //##################################################################################################################
    //                                              Constructors

    /** 
     * Constructor.
     */ 
    @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
    public ConsoleListener() 
    {   super(); 
//        Concept ConversationByConsole = new SimpleConcept(SCid.ConceptName.ordinal(), "ConversationByConsole");
//        SimpleConcept(SCid.ConceptName.ordinal(), "ConversationByConsole");
        Map what = new HashMap(2);
        List par1 = new ArrayList(1);
        par1.add("ConversationByConsole");
        what.put(new Long(SCid.ConceptName.ordinal()), par1);
        List par2 = new ArrayList(1);
        par2.add("Conversation");
        what.put(new Long(SCid.ConceptType.ordinal()), par2);
        DynamicConcept convByCon = new DynamicConcept(what);
        
        ComDir.add_cpt(convByCon, this);    // light up conversation by console concept to mark the fact of ongoing conversation
    } 

    //##################################################################################################################
    //                                              Public methods
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        for(int i=0; ; i++) {
            System.out.println("attention.AttnFlow.run()");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
        }
    }

    //##################################################################################################################
    //                                              Protected data

    //##################################################################################################################
    //                                              Protected methods

    //##################################################################################################################
    //                                              Private data

    //##################################################################################################################
    //                                              Private methods, data
}
