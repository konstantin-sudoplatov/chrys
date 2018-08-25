module interfaces;

/// Base activation interface. Does not have an implementation.
abstract interface ActivationIfc {

    enum NormalizationType {
//        NONE,           // no normalization, the value of activation can be any real number
        BIN,            // active +1, antiactive -1
//        SGN,            // active +1, antiactive -1, indefinite 0
        ESQUASH         // exponential squashification (1 - Math.exp(-activation))/(1 + Math.exp(-activation)
    }

    /// Getter
    @property NormalizationType normalization();

    /// Getter
    @property float activation();
}

/// Interface for ESQUASH normalization activation.
interface EsquashActivationIfc: ActivationIfc {

    /// Setter
    @property float activation(float a);
}

/// Imlementation for ESQUASH normalization activation. This mixin is inserted into the class, which is declared to implement
/// the EsquashActivationIfc interface.
mixin template EsquashActivationImpl(T: EsquashActivationIfc){
    static assert (is(typeof(this) == T), `You introduced yourself as a "` ~ T.stringof ~ `" and you are a "` ~
            this.stringof ~ `". Are you an imposter?`);

    /// Getter
    @property NormalizationType normalization(){
        return NormalizationType.ESQUASH;
    }

    /// Getter
    @property float activation() const {
        return activatioN;
    }

    /// Setter
    @property float activation(float a) {
        return activatioN = a;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
private:
    float activatioN = 0;
}

/// Interface for BIN normalization activation.
interface BinActivationIfc: ActivationIfc {

    /// Set activation to 1.
    float activate();

    /// Set activation to -1 (antiactivate).
    float anactivate();
}

/// Imlementation for BIN normalization activation. This mixin is inserted into the class, which is declared to implement
/// the BinActivationIfc interface.
mixin template BinActivationImpl(T: EsquashActivationIfc) {
    static assert (is(typeof(this) == T), `You introduced yourself as a "` ~ T.stringof ~ `" and you are a "` ~
            this.stringof ~ `". Are you an imposter?`);

    /// Getter
    @property NormalizationType normalization(){
        return NormalizationType.ESQUASH;
    }

    /// Getter
    @property float activation() const {
        return activatioN;
    }

    /// Set activation to 1.
    float activate(){
        return activatioN = 1;
    }

    /// Set activation to -1 (antiactivate). By definition a concept is antiactivated if its activation <= 0.
    float anactivate(){
        return activatioN = -1;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //                               Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
private:
    float activatioN = 0;
}
