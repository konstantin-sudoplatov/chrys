module cpt.cpt_interfaces;
import std.format;

import project_params;

import chri_shared;
import crank.crank_types: DcpDescriptor;
//import chri_shared;

/// Base activation interface. Does not have an implementation.
abstract interface ActivationIfc {

    enum NormalizationType {
        NONE,           // no normalization, the value of activation can be any real number
        BIN,            // active +1, antiactive -1
//        SGN,            // active +1, antiactive -1, indefinite 0
        ESQUASH         // exponential squashification (1 - Math.exp(-activation))/(1 + Math.exp(-activation)
    }

    /// Getter
    NormalizationType normalization();

    /// Getter
    @property float activation() const;
}

/// Interface for ESQUASH normalization activation.
interface EsquashActivationIfc: ActivationIfc {

    /// Setter
    @property float activation(float a);
}

/**
        Imlementation for ESQUASH normalization activation. This mixin is inserted into the class, which is declared to implement
    the EsquashActivationIfc interface.

    Note: strange thing happens - mixed in a class implemented functions work without the "override" attribute. And that is
          fine, there is no way to insert this attribute into the template.

    Parameters:
        T = static type of the class, that uses this implementation. That class must extend the EsquashActivationIfc interface.
*/
mixin template EsquashActivationImpl(T: EsquashActivationIfc){
    static assert (is(typeof(this) == T), `You introduced yourself as a "` ~ T.stringof ~ `" and you are a "` ~
            this.stringof ~ `". Are you an imposter?`);

    /// Getter
    NormalizationType normalization(){
        return NormalizationType.ESQUASH;
    }

    /// Getter
    @property float activation() const {
        return _activation;
    }

    /// Setter
    @property float activation(float a) {
        return _activation = a;
    }

    /// activation value
    protected float _activation = 0;
}

unittest {
    class A: EsquashActivationIfc {
        mixin EsquashActivationImpl!A;
    }

    A a = new A;
    assert(a.normalization == A.NormalizationType.ESQUASH);
    a.activation = 0.5;
    assert(a.activation == 0.5);
}

/// Interface for BIN normalization activation.
interface BinActivationIfc: ActivationIfc {

    /// Set activation to 1.
    float activate();

    /// Set activation to -1 (antiactivate).
    float anactivate();
}

/**
        Imlementation for BIN normalization activation. This mixin is inserted into the class, which is declared to implement
    the BinActivationIfc interface.
    Parameters:
        T = static type of the class, that uses this implementation. That class must extend the BinActivationIfc interface.
*/
mixin template BinActivationImpl(T: BinActivationIfc) {
    static assert (is(typeof(this) == T), `You introduced yourself as a "` ~ T.stringof ~ `" and you are a "` ~
            this.stringof ~ `". Are you an imposter?`);

    /// Getter
    NormalizationType normalization(){
        return NormalizationType.BIN;
    }

    /// Getter
    float activation() const {
        return _activation;
    }

    /// Set activation to 1.
    float activate(){
        return _activation = 1;
    }

    /// Set activation to -1 (antiactivate). By definition a concept is antiactivated if its activation <= 0.
    float anactivate(){
        return _activation = -1;
    }

    // Activation, anactivated by default
    protected float _activation = -1;
}

unittest {
    class A: BinActivationIfc {
        mixin BinActivationImpl!A;
    }

    A a = new A;
    assert(a.normalization == A.NormalizationType.BIN);
    a.activate;
    assert(a.activation == 1);
    a.anactivate;
    assert(a.activation == -1);
}

/**
            Interface for logical (boolean logic) premises.
    Note: the interface must be "shared" for it would be overrided by shared functions. Else it causes strange errors
          "not implemented interface functions".
*/
interface PremiseIfc {

    /// Getter.
    const(Cid[]) premises();

    /// Add premises by cid.
    void addPremises(Cid[] premCids);

    /// Add premise by cid.
    void addPremises(Cid premCid);

    /// Adapter.
    void addPremises(DcpDescriptor[] premDescs);

    /// Adapter.
    void addPremises(DcpDescriptor premDesc);
}

/**
    Imlementation of the PremiseIfc.
*/
mixin template PremiseImpl(T : PremiseIfc) {
    static assert (is(typeof(this) == T), `You introduced yourself as a "` ~ T.stringof ~ `" and you are a "` ~
            this.stringof ~ `". Are you an imposter?`);

    /// Getter.
    const(Cid[]) premises() const {
        return cast(const Cid[])_premises;
    }

    /// Add premises by cid.
    void addPremises(Cid[] premCids) {
        debug foreach(cid; premCids)
            assert(!_maps_filled_ || cast(BinActivationIfc)_sm_[cid].live_factory,    // _sm_ is null in case of calls from unittest
                    format!"Cid %s must implement BinActivationIfc. Check the class %s."(cid, typeid(_sm_[cid])));

        _premises ~= premCids;
    }

    /// Add premise by cid.
    void addPremises(Cid premCid) {
        debug assert(!_maps_filled_ || cast(BinActivationIfc)_sm_[premCid].live_factory,     // _sm_ is null in case of calls from unittest
                format!"Cid %s must implement BinActivationIfc. Check the class %s."(premCid, typeid(_sm_[premCid])));

        _premises ~= premCid;
    }

    /// Adapter.
    void addPremises(DcpDescriptor[] premDescs) {
        Cid[] cids;
        foreach(cd; premDescs)
            cids ~= cd.cid;

        addPremises(cids);
    }

    /// Adapter.
    void addPremises(DcpDescriptor premDesc) {
        addPremises(premDesc.cid);
    }

    /// Array of premise cids.
    protected Cid[] _premises;
}

/**
        Interface for checking dependencies of a concept on premises and other concepts. Deprecated until it is realy justified.
    For now it seems to overlap the activation interfaces functionality.
*/
deprecated interface ReadinessCheckIfc {

    /**
            Check if the concept is ready for reasoning
        Returns: true/false
    */
    bool is_up();

    /**
            Check if the concept is not ready for reasoning
        Returns: true/false
    */
    bool is_down();

    /**
            Set the concept ready.
    */
    void set_up();

    /**
            Set the concept not ready.
    */
    void set_down();
}

/**
        Imlementation for the prerequisite check interface. This mixin is inserted into the class, which is declared to implement
    the PrerequisiteCheckIfc interface.
    Parameters:
        T = static type of the class, that uses this implementation. That class must extend the PrerequisiteCheckIfc interface.
        isUp = initial state: true - up, false - down
*/
deprecated mixin template ReadinessCheckImpl(T: ReadinessCheckIfc, bool isUp = true) {
    static assert (is(typeof(this) == T), `You introduced yourself as a "` ~ T.stringof ~ `" and you are a "` ~
    this.stringof ~ `". Are you an imposter?`);

    /**
            Check if the concept is ready for reasoning
        Returns: true/false
    */
    bool is_up() {
        return _isUp;
    }

    /**
            Check if the concept is not ready for reasoning
        Returns: true/false
    */
    bool is_down() {
        return !_isUp;
    }

    /**
            Set the concept ready.
    */
    void set_up() {
        _isUp = true;
    }

    /**
            Set the concept not ready.
    */
    void set_down() {
        _isUp = false;
    }

    /// The go ahead flag. If it is true, then this concept's activation can be calculated and it can be used by other concepts as
    /// a premise for calculation their activations. If it is false, the reasoning must wait until it is true.
    protected bool _isUp = isUp;       // the concept is ready by default
}

//unittest {
//    class A: ReadinessCheckIfc {
//        mixin ReadinessCheckImpl!A;
//    }
//
//    A a = new A;
//    a.set_up;
//    assert(a.is_up);
//    a.set_down;
//    assert(a.is_down);
//}
