package concepts;

/**
 *  Static concepts identifiers.
 * Concept identifiers are named after the corresponding classes. ordinal() is used as a numerical concept Id in ComDir. Also used for
 * automated generation of the static concept objects pointed by ComDir.
 * @author su
 */
public enum StatCptName {
    Mrk_ItIsConsoleChat,
    Mrk_ItIsHttpChat,
    RequestStopReasoning,
    RequestNextLineFromChatter,
    ;
    
    /** This package is used to find static concept classes in ComDir.generate_static_concepts(). 
      Names of all the classes must be the same as the items in this enum, except the the markers, including the DummyMarker.
      The markers classes are not needed to be defined. Remember when refactoring! */
    public static final String STATIC_CONCEPTS_PACKET_NAME = "concepts.stat";   // 
}
