package io.improbable.keanu.vertices;

import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.vertices.bool.BoolVertex;
import io.improbable.keanu.vertices.dbl.KeanuRandom;


public class AssertVertex extends BoolVertex implements NonProbabilistic<BooleanTensor>, NonSaveableVertex {

    private final Vertex<? extends BooleanTensor> predicate;
    private final String errorMessage;

    public AssertVertex(Vertex<? extends BooleanTensor> predicate, String errorMessage) {
        super(predicate.getShape());
        this.predicate = predicate;
        this.errorMessage = errorMessage;
        setParents(predicate);
    }

    public AssertVertex(Vertex<? extends BooleanTensor> predicate) {
        this(predicate, "");
    }


    @Override
    public BooleanTensor calculate() {
        assertion();
        return predicate.getValue();
    }

    private void assertion() {
        if (!predicate.getValue().allTrue()) {
            throw new AssertionError(buildAssertMessage());
        }
    }

    private String buildAssertMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AssertVertex");
        if (getLabel() != null) {
            stringBuilder.append(" (" + getLabel().getQualifiedName() + ")");
        }
        if (!errorMessage.equals("")) {
            stringBuilder.append(": " + errorMessage);
        }
        return stringBuilder.toString();
    }

    @Override
    public BooleanTensor sample(KeanuRandom random) {
        return predicate.sample();
    }

}