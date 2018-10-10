package io.improbable.keanu.e2e.regression;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import io.improbable.keanu.DeterministicRule;
import io.improbable.keanu.model.LinearModelScore;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.UniformVertex;

public class LinearModelScoreTest {
    @Rule
    public DeterministicRule deterministicRule = new DeterministicRule();

    @Test
    public void coefficientOfDeterminationIsVeryLowForRandomData() {
        DoubleVertex generator = new UniformVertex(new long[]{1, 1000}, -1000, 1000);
        assertThat(LinearModelScore.coefficientOfDetermination(generator.sample(), generator.sample()), lessThan(0.0));
    }

    @Test
    public void coefficientOfDeterminationIs1ForSameData() {
        DoubleVertex generator = new UniformVertex(new long[]{1, 1000}, -1000, 1000);
        DoubleTensor sample = generator.sample();
        assertEquals(1, LinearModelScore.coefficientOfDetermination(sample, sample), 1e-8);
    }

    @Test
    public void coefficientOfDeterminationIs1HighForSimilarData() {
        DoubleVertex input = new UniformVertex(new long[]{1, 1000}, -1000, 1000);
        DoubleVertex noisy = new GaussianVertex(input, 50);
        DoubleTensor inputSample = input.sample();
        input.setAndCascade(inputSample);
        assertThat(LinearModelScore.coefficientOfDetermination(inputSample, noisy.sample()), greaterThan(0.9));
    }
}
