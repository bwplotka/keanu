package io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.binary;

import java.util.HashMap;
import java.util.Map;

import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.PartialDerivatives;

public class MultiplicationVertex extends DoubleBinaryOpVertex {

    /**
     * Multiplies one vertex by another
     *
     * @param left  vertex to be multiplied
     * @param right vertex to be multiplied
     */
    public MultiplicationVertex(DoubleVertex left, DoubleVertex right) {
        super(left, right);
    }

    @Override
    protected DoubleTensor op(DoubleTensor l, DoubleTensor r) {
        return l.times(r);
    }

    @Override
    public Map<Vertex, PartialDerivatives> reverseModeAutoDifferentiation(PartialDerivatives derivativeOfOutputsWithRespectToSelf) {
        Map<Vertex, PartialDerivatives> partials = new HashMap<>();

        PartialDerivatives rightPartial = derivativeOfOutputsWithRespectToSelf.multiplyAlongWrtDimensions(right.getValue(), this.getShape());
        PartialDerivatives leftPartial = derivativeOfOutputsWithRespectToSelf.multiplyAlongWrtDimensions(left.getValue(), this.getShape());

        partials.put(left, rightPartial);
        partials.put(right, leftPartial);

        return partials;
    }

    @Override
    protected PartialDerivatives dualOp(PartialDerivatives l, PartialDerivatives r) {

        // dc = A * db + da * B;
        PartialDerivatives thisInfMultiplied;
        PartialDerivatives thatInfMultiplied;

        if (l.isEmpty()) {
            thisInfMultiplied = PartialDerivatives.OF_CONSTANT;
        } else {
            thisInfMultiplied = l.multiplyAlongOfDimensions(right.getValue(), left.getValue().getShape());
        }

        if (r.isEmpty()) {
            thatInfMultiplied = PartialDerivatives.OF_CONSTANT;
        } else {
            thatInfMultiplied = r.multiplyAlongOfDimensions(left.getValue(), right.getValue().getShape());
        }

        return thisInfMultiplied.add(thatInfMultiplied);
    }
}
