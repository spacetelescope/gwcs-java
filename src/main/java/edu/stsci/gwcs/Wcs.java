package edu.stsci.gwcs;

import edu.stsci.gwcs.coordinate.Frame;
import edu.stsci.gwcs.transform.compound.Compose;
import edu.stsci.gwcs.transform.Transform;
import lombok.NonNull;

public class Wcs {
    private final String name;
    private final Step[] steps;
    private final int[] pixelShape;
    private final Transform forwardTransform;
    private final Transform backwardTransform;

    public Wcs(@NonNull final String name,
               @NonNull final Step[] steps,
               final int[] pixelShape,
               final Transform backwardTransform) {
        if (steps.length < 2) {
            throw new IllegalArgumentException("Wcs requires at least 2 steps");
        }

        if (steps[steps.length - 1].getTransform() != null) {
            throw new IllegalArgumentException("Last step must have a null transform (terminal step)");
        }

        for (int i = 0; i < steps.length - 1; i++) {
            if (steps[i].getTransform() == null) {
                throw new IllegalArgumentException(
                        "Non-terminal step " + i + " must have a non-null transform"
                );
            }
        }

        final Transform firstTransform = steps[0].getTransform();
        if (firstTransform.getInputCount() != steps[0].getFrame().getAxisCount()) {
            throw new IllegalArgumentException(
                    "First transform input count (" + firstTransform.getInputCount()
                            + ") does not match input frame axis count ("
                            + steps[0].getFrame().getAxisCount() + ")"
            );
        }

        for (int i = 0; i < steps.length - 2; i++) {
            final int outputCount = steps[i].getTransform().getOutputCount();
            final int nextInputCount = steps[i + 1].getTransform().getInputCount();
            if (outputCount != nextInputCount) {
                throw new IllegalArgumentException(
                        "Transform output count (" + outputCount + ") at step " + i
                                + " does not match transform input count (" + nextInputCount
                                + ") at step " + (i + 1)
                );
            }
        }

        final Transform lastTransform = steps[steps.length - 2].getTransform();
        final Frame outputFrame = steps[steps.length - 1].getFrame();
        if (lastTransform.getOutputCount() != outputFrame.getAxisCount()) {
            throw new IllegalArgumentException(
                    "Last transform output count (" + lastTransform.getOutputCount()
                            + ") does not match output frame axis count ("
                            + outputFrame.getAxisCount() + ")"
            );
        }

        this.name = name;
        this.steps = steps.clone();
        if (pixelShape != null && pixelShape.length != steps[0].getFrame().getAxisCount()) {
            throw new IllegalArgumentException(
                    "pixelShape length (" + pixelShape.length
                            + ") does not match input frame axis count ("
                            + steps[0].getFrame().getAxisCount() + ")"
            );
        }
        this.pixelShape = pixelShape != null ? pixelShape.clone() : null;

        final Transform[] stepTransforms = new Transform[steps.length - 1];
        for (int i = 0; i < stepTransforms.length; i++) {
            stepTransforms[i] = steps[i].getTransform();
        }
        this.forwardTransform = new Compose(stepTransforms);

        if (backwardTransform != null) {
            if (backwardTransform.getInputCount() != outputFrame.getAxisCount()) {
                throw new IllegalArgumentException(
                        "Backward transform input count (" + backwardTransform.getInputCount()
                                + ") does not match output frame axis count ("
                                + outputFrame.getAxisCount() + ")"
                );
            }
            if (backwardTransform.getOutputCount() != steps[0].getFrame().getAxisCount()) {
                throw new IllegalArgumentException(
                        "Backward transform output count (" + backwardTransform.getOutputCount()
                                + ") does not match input frame axis count ("
                                + steps[0].getFrame().getAxisCount() + ")"
                );
            }
            this.backwardTransform = backwardTransform;
        } else if (this.forwardTransform.hasInverse()) {
            this.backwardTransform = this.forwardTransform.getInverse();
        } else {
            this.backwardTransform = null;
        }
    }

    public String getName() {
        return name;
    }

    public Step[] getSteps() {
        return steps.clone();
    }

    public int[] getPixelShape() {
        return pixelShape != null ? pixelShape.clone() : null;
    }

    public Frame getInputFrame() {
        return steps[0].getFrame();
    }

    public Frame getOutputFrame() {
        return steps[steps.length - 1].getFrame();
    }

    public Transform getForwardTransform() {
        return forwardTransform;
    }

    public Transform getBackwardTransform() {
        if (backwardTransform == null) {
            throw new UnsupportedOperationException("This Wcs does not have an inverse");
        }
        return backwardTransform;
    }

    public boolean hasInverse() {
        return backwardTransform != null;
    }

    public double[] evaluate(final double... inputs) {
        return forwardTransform.evaluate(inputs);
    }

    public void evaluate(final double[] inputs, final int inputOffset,
                         final double[] outputs, final int outputOffset) {
        forwardTransform.evaluate(inputs, inputOffset, outputs, outputOffset);
    }

    public double[] evaluateInverse(final double... inputs) {
        if (backwardTransform == null) {
            throw new UnsupportedOperationException("This Wcs does not have an inverse");
        }
        return backwardTransform.evaluate(inputs);
    }

    public void evaluateInverse(final double[] inputs, final int inputOffset,
                                final double[] outputs, final int outputOffset) {
        if (backwardTransform == null) {
            throw new UnsupportedOperationException("This Wcs does not have an inverse");
        }
        backwardTransform.evaluate(inputs, inputOffset, outputs, outputOffset);
    }
}
