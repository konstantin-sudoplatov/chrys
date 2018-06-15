package concepts;

/**
 *  Static concepts identifiers.
 * Concept identifiers are named after the corresponding classes. ordinal() is used as a numerical concept Id in ComDir. Also used for
 * automated generation of the static concept objects pointed by ComDir.
 * @author su
 */
public enum StatCptEnum {
    Mrk_Nothing,                // an empty static concept, serves as a filler in some cases, for example to show that no special processing should be done
    Mrk_wait_for_the_line_from_chatter,     // used in the "wait_act" concept as a cause for waiting
    Mrk_UnorderedListOfCids,
    Mrk_ElementaryPremise,
    InitiateChatting,
    ;
    
    /** This package is used to find static concept classes in ComDir.generate_static_concepts(). 
      Names of all the classes must be the same as the items in this enum, except the the markers, including the DummyMarker.
      The markers classes are not needed to be defined. Remember when refactoring! */
    public static final String STATIC_CONCEPTS_PACKET_NAME = "concepts.stat";   // 
}
