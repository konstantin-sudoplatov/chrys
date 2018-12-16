package cpt

/**
 *          The base for activation interfaces
 */
interface ActivationIfc {

    enum class NormalizationType {
        NONE,       // no normalization, the value of activation can be any real number
        BIN,        // active +1, antiactive -1
        ESQUASH     // exponential squashification (1 - Math.exp(-activation))/(1 + Math.exp(-activation)
    }

    var activation: Float
}

/**
 *          Interface for ESQUASH normalization activation.
 */
interface EsquashActivationIfc: ActivationIfc {

    val normalization: ActivationIfc.NormalizationType
        get() { return ActivationIfc.NormalizationType.ESQUASH }

    override var activation: Float
}

/**
 *          Interface for BIN normalization activation.
 *      When implementing do not forget to set the activation field to an initial value of -1.
 */
interface BinActivationIfc: ActivationIfc {

    val normalization: ActivationIfc.NormalizationType
        get() { return ActivationIfc.NormalizationType.BIN }

    override var activation: Float

    fun activate() { activation = 1f }

    fun anaactivate() { activation = -1f }

}