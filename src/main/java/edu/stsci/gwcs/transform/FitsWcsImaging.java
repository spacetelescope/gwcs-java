package edu.stsci.gwcs.transform;

import lombok.NonNull;

public class FitsWcsImaging implements Transform {
    private final double[] crpix;
    private final double[] crval;
    private final double[][] cd;
    private final double[][] cdInverse;

    public FitsWcsImaging(@NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[] cdelt,
                          @NonNull final double[][] pc) {
        if (crpix.length != 2) {
            throw new IllegalArgumentException("crpix must have exactly 2 elements");
        }
        if (crval.length != 2) {
            throw new IllegalArgumentException("crval must have exactly 2 elements");
        }
        if (cdelt.length != 2) {
            throw new IllegalArgumentException("cdelt must have exactly 2 elements");
        }
        if (pc.length != 2 || pc[0].length != 2 || pc[1].length != 2) {
            throw new IllegalArgumentException("pc must be a 2x2 matrix");
        }

        this.crpix = crpix.clone();
        this.crval = crval.clone();

        this.cd = new double[][]{
                {cdelt[0] * pc[0][0], cdelt[0] * pc[0][1]},
                {cdelt[1] * pc[1][0], cdelt[1] * pc[1][1]}
        };

        final double det = this.cd[0][0] * this.cd[1][1] - this.cd[0][1] * this.cd[1][0];
        if (det == 0.0) {
            throw new IllegalArgumentException("CD matrix is singular (determinant is zero)");
        }

        this.cdInverse = new double[][]{
                {this.cd[1][1] / det, -this.cd[0][1] / det},
                {-this.cd[1][0] / det, this.cd[0][0] / det}
        };
    }

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 2;
    }

    @Override
    public void evaluate(final double[] inputs, final int inputOffset, final double[] outputs, final int outputOffset) {
        final double u = inputs[inputOffset] - crpix[0];
        final double v = inputs[inputOffset + 1] - crpix[1];

        final double xDeg = Math.fma(cd[0][0], u, cd[0][1] * v);
        final double yDeg = Math.fma(cd[1][0], u, cd[1][1] * v);

        final double xRad = Math.toRadians(xDeg);
        final double yRad = Math.toRadians(yDeg);

        final double phi = Math.atan2(xRad, -yRad);
        final double theta = Math.atan2(1.0, Math.sqrt(xRad * xRad + yRad * yRad));

        nativeTocelestial(phi, theta, outputs, outputOffset);
    }

    private void nativeTocelestial(final double phi, final double theta,
                                   final double[] outputs, final int outputOffset) {
        final double alphaP = Math.toRadians(crval[0]);
        final double deltaP = Math.toRadians(crval[1]);
        final double phiP = Math.toRadians(180.0);

        final double sinTheta = Math.sin(theta);
        final double cosTheta = Math.cos(theta);
        final double sinDeltaP = Math.sin(deltaP);
        final double cosDeltaP = Math.cos(deltaP);
        final double sinPhiDiff = Math.sin(phi - phiP);
        final double cosPhiDiff = Math.cos(phi - phiP);

        final double x = sinTheta * cosDeltaP - cosTheta * sinDeltaP * cosPhiDiff;
        final double y = -cosTheta * sinPhiDiff;
        final double alphaOffset = Math.atan2(y, x);
        double alpha = Math.toDegrees(alphaP + alphaOffset);

        final double sinDelta = sinTheta * sinDeltaP + cosTheta * cosDeltaP * cosPhiDiff;
        double delta = Math.toDegrees(Math.asin(sinDelta));

        alpha = alpha % 360.0;
        if (alpha < 0.0) {
            alpha += 360.0;
        }

        outputs[outputOffset] = alpha;
        outputs[outputOffset + 1] = delta;
    }

    private void celestialToNative(final double lonDeg, final double latDeg,
                                   final double[] phiTheta) {
        final double alphaP = Math.toRadians(crval[0]);
        final double deltaP = Math.toRadians(crval[1]);
        final double phiP = Math.toRadians(180.0);

        final double alpha = Math.toRadians(lonDeg);
        final double delta = Math.toRadians(latDeg);

        final double sinDelta = Math.sin(delta);
        final double cosDelta = Math.cos(delta);
        final double sinDeltaP = Math.sin(deltaP);
        final double cosDeltaP = Math.cos(deltaP);
        final double sinAlphaDiff = Math.sin(alpha - alphaP);
        final double cosAlphaDiff = Math.cos(alpha - alphaP);

        final double x = sinDelta * cosDeltaP - cosDelta * sinDeltaP * cosAlphaDiff;
        final double y = -cosDelta * sinAlphaDiff;
        final double phi = phiP + Math.atan2(y, x);

        final double sinTheta = sinDelta * sinDeltaP + cosDelta * cosDeltaP * cosAlphaDiff;
        final double theta = Math.asin(sinTheta);

        phiTheta[0] = phi;
        phiTheta[1] = theta;
    }

    private void evaluateInverse(final double[] inputs, final int inputOffset,
                                 final double[] outputs, final int outputOffset) {
        final double[] phiTheta = new double[2];
        celestialToNative(inputs[inputOffset], inputs[inputOffset + 1], phiTheta);
        final double phi = phiTheta[0];
        final double theta = phiTheta[1];

        final double sinTheta = Math.sin(theta);
        final double xRad = Math.cos(theta) * Math.sin(phi) / sinTheta;
        final double yRad = -Math.cos(theta) * Math.cos(phi) / sinTheta;

        final double xDeg = Math.toDegrees(xRad);
        final double yDeg = Math.toDegrees(yRad);

        final double u = Math.fma(cdInverse[0][0], xDeg, cdInverse[0][1] * yDeg);
        final double v = Math.fma(cdInverse[1][0], xDeg, cdInverse[1][1] * yDeg);

        outputs[outputOffset] = u + crpix[0];
        outputs[outputOffset + 1] = v + crpix[1];
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        final Transform inverse = new Transform() {
            @Override
            public int getInputCount() {
                return 2;
            }

            @Override
            public int getOutputCount() {
                return 2;
            }

            @Override
            public void evaluate(final double[] inputs, final int inputOffset,
                                 final double[] outputs, final int outputOffset) {
                FitsWcsImaging.this.evaluateInverse(inputs, inputOffset, outputs, outputOffset);
            }
        };
        return new ExplicitInverseWrapper(inverse, this);
    }
}
