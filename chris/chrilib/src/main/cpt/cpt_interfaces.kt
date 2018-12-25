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

    fun activate() { activation = 1f }

    fun anactivate() { activation = -1f }

    fun normalization(): NormalizationType
}
