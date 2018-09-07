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
interface ReadinessCheckIfc {

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
mixin template ReadinessCheckImpl(T: ReadinessCheckIfc, bool isUp = true) {
    static assert (is(typeof(this) == T), `You introduced yourself as a "` ~ T.stringof ~ `" and you are a "` ~
    this.stringof ~ `". Are you an imposter?`);

    /**
            Check if the concept is ready for reasoning
        Returns: true/false
    */
    bool is_up() {
        return isUp_;
    }

    /**
            Check if the concept is not ready for reasoning
        Returns: true/false
    */
    bool is_down() {
        return !isUp_;
    }

    /**
            Set the concept ready.
    */
    void set_up() {
        isUp_ = true;
    }

    /**
            Set the concept not ready.
    */
    void set_down() {
        isUp_ = false;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    /// The go ahead flag. If it is true, then this concept's activation can be calculated and it can be used by other concepts as
    /// a premise for calculation their activations. If it is false, the reasoning must wait until it is true.
    private bool isUp_ = isUp;       // the concept is ready by default
}

unittest {
    class A: ReadinessCheckIfc {
        mixin ReadinessCheckImpl!A;
    }

    A a = new A;
    a.set_up;
    assert(a.is_up);
    a.set_down;
    assert(a.is_down);
}
