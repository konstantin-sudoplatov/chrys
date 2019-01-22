package cpt

import basemain.Cid
import cpt.abs.Primitive
import cpt.abs.SpiritPrimitive
import db.SerializedConceptData

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

    override fun equals(other: Any?): Boolean {
        if(super.equals(other) == false)
            return false
        else {
            val o = other as SpStringPrim
            return string == o.string
        }
    }

    override fun serialize(stableSuccSpace: Int, tranSuccSpace: Int): SerializedConceptData {
        val stringByteArray = string.toByteArray()
        val sCD = super.serialize(
            stableSuccSpace + Int.SIZE_BYTES + stringByteArray.size,
            tranSuccSpace + 0
        )

        val stable = sCD.stable!!
        stable.putInt(stringByteArray.size)
        stable.put(stringByteArray)

        return sCD
    }

    override fun deserialize(sCD: SerializedConceptData) {
        super.deserialize(sCD)

        val stable = sCD.stable!!
        val stringByteArraySize = stable.getInt()
        val stringByteArray = ByteArray(stringByteArraySize) { 0 }
        stable.get(stringByteArray)
        string = String(stringByteArray)
    }

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

    override fun equals(other: Any?): Boolean {
        if(super.equals(other) == false)
            return false
        else {
            val o = other as SpNumPrim
            if(num.isNaN() && o.num.isNaN()) return true
            return num == o.num
        }
    }

    override fun serialize(stableSuccSpace: Int, tranSuccSpace: Int): SerializedConceptData {
        val sCD = super.serialize(
            stableSuccSpace + 8,
            tranSuccSpace + 0
        )

        val stable = sCD.stable!!
        stable.putDouble(num)

        return sCD
    }

    override fun deserialize(sCD: SerializedConceptData) {
        super.deserialize(sCD)

        val stable = sCD.stable!!
        num = stable.getDouble()
    }

    fun load(num: Double): SpNumPrim {
        this.num = num
        return this
    }
}

class NumPrim(spNumPrim: SpNumPrim): Primitive(spNumPrim)
