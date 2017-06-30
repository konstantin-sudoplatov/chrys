package attention;

import concept.ComDir;
import concept.dyn.DynamicConcept;
import concept.en.SCid;
import java.util.ArrayList;
import java.util.List;

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
        // Create dynamic concept showing ConversationByConsole fact
        DynamicConcept dCpt = new DynamicConcept();
        List<Long> args = new ArrayList<>(1);
        args.add(new Long(SCid.ConversationByConsole.ordinal()));
        dCpt.add_cpt(new Long(SCid.MrkStatType.ordinal()), args);
        
        ComDir.add_cpt(dCpt, this);    // add the concept to the private dir
        _throwInCaldron_(dCpt.getCid());    // throw in caldron
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
