package cpt

import basemain.Cid
import cpt.abs.Primitive
import cpt.abs.SpiritPrimitive

class SpMarkPrim(cid: Cid): SpiritPrimitive(cid) {
    override fun liveFactory(): MarkPrim {
        return MarkPrim(this)
    }
}

class MarkPrim(spMarkPrim: SpMarkPrim): Primitive(spMarkPrim) {

}

class SpStringPrim(cid: Cid): SpiritPrimitive(cid) {
    override fun liveFactory(): StringPrim {
        return StringPrim(this)
    }
}

class StringPrim(spStringPrim: SpStringPrim): Primitive(spStringPrim) {

}