package concepts.dyn;

import attention.Caldron;

/**
 * Interface for dynamic concepts capable of doing the reasoning steps.
 * @author su
 */
public interface AssessmentIface {
    
    /**
     * Do weighing, determine activation, may be, do actions, determine possible effects.
     * @param context a caldron in which this assess takes place.
     * @return array of effects, sorted by there suggested usefulness. These effects may serve as
     * heads in the next step of reasoning.
     */
    public long[] assess(Caldron context);
}
