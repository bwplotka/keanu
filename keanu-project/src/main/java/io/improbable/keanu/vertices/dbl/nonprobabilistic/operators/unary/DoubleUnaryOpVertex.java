package io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.unary;

import java.util.Map;

import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.NonProbabilistic;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.DualNumber;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.PartialDerivatives;

public abstract class DoubleUnaryOpVertex extends DoubleVertex implements NonProbabilistic<DoubleTensor> {

    protected final DoubleVertex inputVertex;

    /**
     * A vertex that performs a user defined operation on a single input vertex
     *
     * @param inputVertex the input vertex
     */
    public DoubleUnaryOpVertex(DoubleVertex inputVertex) {
        this(inputVertex.getShape(), inputVertex);
    }

    /**
     * A vertex that performs a user defined operation on a single input vertex
     *
     * @param shape       the shape of the tensor
     * @param inputVertex the input vertex
     */
    public DoubleUnaryOpVertex(int[] shape, DoubleVertex inputVertex) {
        this.inputVertex = inputVertex;
        setParents(inputVertex);
        setValue(DoubleTensor.placeHolder(shape));
    }

    @Override
    public DoubleTensor sample(KeanuRandom random) {
        return op(inputVertex.sample(random));
    }

    @Override
    public DoubleTensor calculate() {
        return op(inputVertex.getValue());
    }

    @Override
    public PartialDerivatives calculateDualNumber(Map<Vertex, PartialDerivatives> derivativeOfSelfWithRespectToInputs) {
        try {
            return dualOp(derivativeOfSelfWithRespectToInputs.get(inputVertex));
        } catch (UnsupportedOperationException e) {
            return super.calculateDualNumber(derivativeOfSelfWithRespectToInputs);
        }
    }

    protected abstract DoubleTensor op(DoubleTensor value);

    protected abstract PartialDerivatives dualOp(PartialDerivatives dualNumber);
}
