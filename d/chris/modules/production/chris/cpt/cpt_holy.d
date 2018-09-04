module cpt_holy;
import cpt_holy_abstract;
import cpt_live_abstract, cpt_live;

import global, tools;

/**
            Static concept.
    Actually, it is immutable since all fields are immutable. Making the class or constructor immutable, however would introduce
    unneccessary complexity in the code, that uses this class.
*/
final class HolyStaticConcept: HolyConcept {

    immutable void* fp;                     /// function pointer to the static concept function
    immutable StatCallType call_type;       /// call type of the static concept function

    /**
                Constructor
        Parameters:
            cid = cid
            fp = function pointer to the static concept function
            callType = call type of the static concept function
    */
    this(Cid cid, void* fp, StatCallType callType){
        cast()super.cid = cid;
        cast()this.fp = cast(immutable)fp;
        cast()call_type = callType;
    }

    /**
        Create live wrapper for the holy static concept.
    */
    override StaticConcept live_factory() const {
        return new StaticConcept(cast(immutable)this);
    }
}

/**
            Uncontitional neuron.
        It is a degenerate neuron, capable only of applying its effects without consulting any premises. Its activation is always 1.
 */
class HolyUnconditionalNeuron: HolyNeuron {

    /**
        Create live wrapper for the holy static concept.
    */
    override UnconditionalNeuron live_factory() const {
        return new UnconditionalNeuron(cast(immutable)this);
    }
}

/**
            Seed.
*/
final class HolySeed: HolyUnconditionalNeuron {

    /**
        Create live wrapper for the holy static concept.
    */
    override Seed live_factory() const {
        return new Seed(cast(immutable)this);
    }
}
