package concept.en;

/**
 *  Static concepts identifiers.
 * Concept Ids are named after the corresponding classes. ordinal() is used as a numerical concept Id in ComDir. Also used for
 * automated generation of the static concept objects pointed by ComDir.
 * @author su
 */
public enum SCid {
    MrkStatType,
    MrkDynType,
    MrkStatName,
    MrkDynName,
    ConversationByConsole,
    ;
    
    /** Is used to find static concept classes in ComDir.generate_static_concepts(). Must be the same as the package containing
      those classes. Remember when refactoring! */
    public static final String STATIC_CONCEPTS_PACKET_NAME = "concept.stat";   // 
}
