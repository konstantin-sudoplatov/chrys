package concepts.dyn;

import attention.CaldronLoop;

/**
 * Interface for dynamic concepts capable of doing the reasoning steps.
 * @author su
 */
public interface AssertionIface {
    
    /**
     * Do weighing, determine activation, may be, do actions, determine possible effects.
     * @param context a caldron in which this assertion takes place.
     * @return array of effects, sorted by there suggested usefulness. These effects may serve as
     * heads in the next step of reasoning.
     */
    public long[] assertion(CaldronLoop context);
}
