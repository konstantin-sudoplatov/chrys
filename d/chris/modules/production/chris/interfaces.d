module interfaces;

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
        return activation_;
    }

    /// Setter
    @property float activation(float a) {
        return activation_ = a;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    /// activation value
    private float activation_ = 0;
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
        return activation_;
    }

    /// Set activation to 1.
    float activate(){
        return activation_ = 1;
    }

    /// Set activation to -1 (antiactivate). By definition a concept is antiactivated if its activation <= 0.
    float anactivate(){
        return activation_ = -1;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    private float activation_ = 0;
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

/// Interface for checking dependencies of a concept on premises and other concepts.
interface PrerequisiteCheckIfc {

    /// Getter
    @property bool go_ahead();

    /// Setter
    @property bool go_ahead(bool goAhead);
}

/**
        Imlementation for the prerequisite check interface. This mixin is inserted into the class, which is declared to implement
    the PrerequisiteCheckIfc interface.
    Parameters:
        T = static type of the class, that uses this implementation. That class must extend the PrerequisiteCheckIfc interface.
*/
mixin template PrerequisiteCheckImpl(T: PrerequisiteCheckIfc) {
    static assert (is(typeof(this) == T), `You introduced yourself as a "` ~ T.stringof ~ `" and you are a "` ~
    this.stringof ~ `". Are you an imposter?`);

    /// Getter
    @property bool go_ahead() {
        return goAhead_;
    }

    /// Setter
    @property bool go_ahead(bool goAhead) {
        return goAhead_ = goAhead;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    /// The go ahead flag. If it is true, then this concept's activation can be calculated and it can be used by other concepts as
    /// a premise for calculation their activations. If it is false, the reasoning must wait until it is true.
    private bool goAhead_ = true;       // the concept is ready by default
}

unittest {
    class A: PrerequisiteCheckIfc {
        mixin PrerequisiteCheckImpl!A;
    }

    A a = new A;
    a.go_ahead = true;
    assert(a.go_ahead);
    a.go_ahead = false;
    assert(!a.go_ahead);
}
