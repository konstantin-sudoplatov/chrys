package concepts.dyn.parts;

import attention.ConceptNameSpace;

/**
 * Interface for dynamic concepts capable of doing the reasoning steps.
 * @author su
 */
public interface EvaluationIface {

    /**
     * Do weighing, determine activation, do actions, determine possible effects.
     * As a side effect of the assessment an action of the concept may raise the caldron's
     * flag "stopReasoningRequested".
     * @param caldron a caldron in which this assess takes place.
     */
    public void calculate_signum_activation_and_do_actions(ConceptNameSpace caldron);

    /**
     * Calculate weighed sum, normalize it as the signum function.
     * @param caldron
     * @return 
     */
    public float calculate_signum_activation(ConceptNameSpace caldron);
}
