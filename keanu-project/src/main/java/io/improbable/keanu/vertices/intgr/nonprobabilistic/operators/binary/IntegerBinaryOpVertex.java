package io.improbable.keanu.vertices.intgr.nonprobabilistic.operators.binary;


import io.improbable.keanu.vertices.dbltensor.KeanuRandom;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.intgr.nonprobabilistic.NonProbabilisticInteger;

public abstract class IntegerBinaryOpVertex extends NonProbabilisticInteger {

    protected final IntegerVertex a;
    protected final IntegerVertex b;

    public IntegerBinaryOpVertex(IntegerVertex a, IntegerVertex b) {
        this.a = a;
        this.b = b;
        setParents(a, b);
    }

    @Override
    public Integer sample(KeanuRandom random) {
        return op(a.sample(random), b.sample(random));
    }

    public Integer getDerivedValue() {
        return op(a.getValue(), b.getValue());
    }

    protected abstract Integer op(Integer a, Integer b);
}
