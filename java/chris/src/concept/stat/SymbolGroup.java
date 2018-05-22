package concept.stat;

import concept.StaticConcept;

/**
 * Static concept: contains a string as a group of symbols without white spaces. Can represent words, parts of words, numbers
 * and other symbols.
 * @author su
 */
public class SymbolGroup extends StaticConcept {

    public final String symbolGroup;

    /** 
     * Constructor.
     * @param cid
     * @param symbolGroup string of symbols without white spaces.
     */ 
    public SymbolGroup(long cid, String symbolGroup) { 
        super(cid);
        this.symbolGroup = symbolGroup;
    } 
}
