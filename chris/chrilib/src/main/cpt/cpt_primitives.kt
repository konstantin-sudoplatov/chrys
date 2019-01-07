package cpt

import basemain.Cid
import cpt.abs.Primitive
import cpt.abs.SpiritPrimitive

class SpMarkPrim(cid: Cid): SpiritPrimitive(cid) {
    override fun liveFactory(): MarkPrim = MarkPrim(this)
}

class MarkPrim(spMarkPrim: SpMarkPrim): Primitive(spMarkPrim)

class SpStringPrim(cid: Cid): SpiritPrimitive(cid) {
    var string: String = ""
        private set(value) { field = value }

    override fun toString(): String {
        return super.toString() + "\n    string = $string"
    }

    override fun liveFactory() = StringPrim(this)

    fun load(string: String): SpStringPrim {
        this.string = string
        return this
    }
}

class StringPrim(spStringPrim: SpStringPrim): Primitive(spStringPrim)

class SpNumPrim(cid: Cid): SpiritPrimitive(cid) {
    var num: Double = Double.NaN
        private set(value) { field = value }

    override fun toString(): String {
        return super.toString() + "\n    num = $num"
    }

    override fun liveFactory() = NumPrim(this)

    fun load(num: Double): SpNumPrim {
        this.num = num
        return this
    }
}

class NumPrim(spNumPrim: SpNumPrim): Primitive(spNumPrim)
