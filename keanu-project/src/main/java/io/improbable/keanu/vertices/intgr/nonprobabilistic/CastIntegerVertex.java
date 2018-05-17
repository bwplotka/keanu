package io.improbable.keanu.vertices.intgr.nonprobabilistic;

import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbltensor.KeanuRandom;

public class CastIntegerVertex extends NonProbabilisticInteger {

    private final Vertex<Integer> inputVertex;

    public CastIntegerVertex(Vertex<Integer> inputVertex) {
        this.inputVertex = inputVertex;
        setParents(inputVertex);
    }

    @Override
    public Integer sample(KeanuRandom random) {
        return inputVertex.sample(random);
    }

    @Override
    public Integer getDerivedValue() {
        return inputVertex.getValue();
    }
}
