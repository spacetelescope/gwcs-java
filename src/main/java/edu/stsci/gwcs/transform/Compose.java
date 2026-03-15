package edu.stsci.gwcs.transform;

import lombok.NonNull;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Compose implements Transform {
    private final Transform[] transforms;
    private final int maxIntermediateOutputCount;

    public Compose(@NonNull final Transform[] transforms) {
        if (transforms.length == 0) {
            throw new IllegalArgumentException("Compose requires at least one child transform");
        }

        int maxOutputCount = 0;
        for (int i = 0; i < transforms.length - 1; i++) {
            final int outputCount = transforms[i].getOutputCount();

            if (outputCount != transforms[i + 1].getInputCount()) {
                throw new IllegalArgumentException("Cannot compose transforms with mismatched output and input count");
            }

            maxOutputCount = Math.max(maxOutputCount, outputCount);
        }

        this.maxIntermediateOutputCount = maxOutputCount;
        this.transforms = transforms;
    }

    @Override
    public int getInputCount() {
        return transforms[0].getInputCount();
    }

    @Override
    public int getOutputCount() {
        return transforms[transforms.length - 1].getOutputCount();
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        if (transforms.length == 1) {
            transforms[0].evaluate(inputs, inputOffset, outputs, outputOffset);
            return;
        }

        if (transforms.length == 2) {
            final double[] intermediate = new double[transforms[0].getOutputCount()];

            transforms[0].evaluate(inputs, inputOffset, intermediate, 0);
            transforms[1].evaluate(intermediate, 0, outputs, outputOffset);

            return;
        }

        final double[] bufferA = new double[maxIntermediateOutputCount];
        final double[] bufferB = new double[maxIntermediateOutputCount];

        transforms[0].evaluate(inputs, inputOffset, bufferA, 0);

        double[] currentInput = bufferA;
        double[] currentOutput = bufferB;

        for (int i = 1; i < transforms.length - 1; i++) {
            transforms[i].evaluate(currentInput, 0, currentOutput, 0);

            final double[] temp = currentInput;
            currentInput = currentOutput;
            currentOutput = temp;
        }

        transforms[transforms.length - 1].evaluate(currentInput, 0, outputs, outputOffset);
    }

    @Override
    public boolean hasInverse() {
        return Stream.of(transforms).allMatch(Transform::hasInverse);
    }

    @Override
    public Transform getInverse() {
        return new Compose(
                IntStream.range(0, transforms.length)
                        .mapToObj(i -> transforms[transforms.length - i - 1])
                        .map(Transform::getInverse)
                        .toArray(Transform[]::new)
        );
    }
}
