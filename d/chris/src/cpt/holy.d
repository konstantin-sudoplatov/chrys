module cpt.holy;
import global;
import interfaces;

/**
            Base for all concepts.
    "Holy" means stable and storable as opposed to "Live" concepts, that are in a constant using and change and living only
    in memory. All live concepts contain reference to its holy counterpart. There can be many sin instances that corresponds
    to only one holy partner, which is considered immutable by them.

    "shared" attribute is inherrited by successors and cannot be changed.
*/
shared abstract class HolyConcept {

protected:
    immutable Cid _cid = 0;       /// Cid of the concept, to check if cid used to find a concept is its actual cid. (paranoia)
}

/**
            Static concept.
*/
class StaticConcept: HolyConcept {
    import common_types;

    /// Static concept descriptor
    immutable StatDescriptor sd;

    /**
                Constructor
        Parameters:
            cid = cid
            sd = descriptor of a static concept
    */
    this(Cid cid, StatDescriptor sd){
        cast()_cid = cid;
        cast()this.sd = sd;
    }
}

/**
            Base for all holy dynamic concepts.
*/
abstract class HolyDynamicConcept: HolyConcept {
    this(){}
}

/**
            Base for all neurons.
        All of its descendant class names will have the suffix of "_hnr".
*/
abstract class HolyNeuron: HolyDynamicConcept {
    this(){
        super();
    }
}

/**
            Uncontitional neuron.
        It is a degenerate, capable only of applying its effects without consulting any premises. Its activation is always 1.
    Ideal for seeds.
 */
class UnconditionalNeuron_hnr: HolyDynamicConcept {
    this() {
        super();
    }
}


//public class Unconditional_nrn extends Neuron {
