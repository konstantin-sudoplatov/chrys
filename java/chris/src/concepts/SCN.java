package concepts;

/**
 *  Static concepts name.
 * Concept identifiers are named after the corresponding classes. ordinal() is used as a numerical concept Id in ComDir. Also used for
 * automated generation of the static concept objects pointed by ComDir.
 * @author su
 */
public enum SCN {
    Mrk_TestMarker,
    Activate_stat,                      // set activation of a concept to 1
    Anactivate_stat,                  // set activation of a concept to -1
    CaldronStopAndWait_stat,            // request caldron to wait on a current neuron until premises changed in a such way, that the processing can be continued
    CloneConceptAndAappendToList_stat,  // clone given concept and add it to the end of a list concept
    CloneConcept_stat,                  // clone a concept and add the clone to the given name space
    CaldronIsUp_stat,                   // check status of a caldron and activate/anactivate a peg
    NotifyBranch_stat,                  // send a message to the branch loop, which in turn will invoke the NotyfyBranchAgent_stat action in the destination branch environment
    SendReadFromConsoleMessage,         // send a message to the console loop requesting new line from chatter
    SetPrimusInterPares_stat,           // set primus in the group of equals (activation set automatically)
    ;
    
    /** This package is used to find static concept classes in ComDir.generate_static_concepts(). 
      Names of all the classes must be the same as the items in this enum, except the the markers, including the DummyMarker.
      The markers classes are not needed to be defined. Remember when refactoring! */
    public static final String STATIC_CONCEPTS_PACKET_NAME = "concepts.stat";   // 
}
