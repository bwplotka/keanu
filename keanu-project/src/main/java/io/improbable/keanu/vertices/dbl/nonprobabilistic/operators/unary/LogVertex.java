package io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.unary;

import java.util.HashMap;
import java.util.Map;

import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.PartialDerivatives;

public class LogVertex extends DoubleUnaryOpVertex {

    /**
     * Returns the natural logarithm, base e, of a vertex
     *
     * @param inputVertex the vertex
     */
    public LogVertex(DoubleVertex inputVertex) {
        super(inputVertex);
    }

    @Override
    protected DoubleTensor op(DoubleTensor value) {
        return value.log();
    }

    @Override
    protected PartialDerivatives dualOp(PartialDerivatives partialDerivatives) {

        if (partialDerivatives.isEmpty()) {
            return PartialDerivatives.OF_CONSTANT;
        } else {
            return partialDerivatives.divideBy(inputVertex.getValue());
        }
    }

    @Override
    public Map<Vertex, PartialDerivatives> reverseModeAutoDifferentiation(PartialDerivatives derivativeOfOutputsWithRespectToSelf) {
        Map<Vertex, PartialDerivatives> partials = new HashMap<>();
        partials.put(inputVertex, derivativeOfOutputsWithRespectToSelf
            .multiplyAlongWrtDimensions(inputVertex.getValue().reciprocal(), this.getShape()));
        return partials;
    }
}
