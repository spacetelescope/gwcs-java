package edu.stsci.gwcs.transform.rotation;

import lombok.NonNull;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.util.WcsMath;

/**
 * Three-axis Euler rotation applied to a celestial coordinate pair (alpha, delta) in degrees.
 *
 * <p>The angles and axes order are stored as fields so {@link #getInverse()} can express the
 * inverse as negated/reversed angles rather than just a transposed matrix. This preserves the
 * original structure for ASDF serialization.
 */
public class EulerAngleRotation implements Transform {

    /**
     * Order in which the three Euler rotations are composed. Each value stores the canonical
     * three-character lowercase code (e.g. {@code "zxz"}) used by astropy and ASDF for round-trip.
     */
    public enum AxesOrder {
        // Proper Euler (first and last axis match)
        XYX("xyx"),
        XZX("xzx"),
        YXY("yxy"),
        YZY("yzy"),
        ZXZ("zxz"),
        ZYZ("zyz"),
        // Tait-Bryan (all three axes distinct)
        XYZ("xyz"),
        XZY("xzy"),
        YXZ("yxz"),
        YZX("yzx"),
        ZXY("zxy"),
        ZYX("zyx");

        private final String code;

        AxesOrder(final String code) {
            this.code = code;
        }

        /** Three-character lowercase code used by astropy and ASDF (e.g. {@code "zxz"}). */
        public String code() {
            return code;
        }

        /**
         * Returns the reversed axes order, used when composing the parametric inverse. For
         * example, {@code XYZ.reverse()} is {@code ZYX}.
         */
        public AxesOrder reverse() {
            final char[] chars = code.toCharArray();
            final String reversed = "" + chars[2] + chars[1] + chars[0];
            return fromCode(reversed);
        }

        /**
         * Parse a three-character axes-order code (case-insensitive). Throws
         * {@link IllegalArgumentException} for unknown codes.
         */
        public static AxesOrder fromCode(@NonNull final String code) {
            final String normalized = code.toLowerCase();
            for (final AxesOrder value : values()) {
                if (value.code.equals(normalized)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unknown axes order: " + code);
        }
    }

    private final double phi;
    private final double theta;
    private final double psi;
    private final AxesOrder axesOrder;

    private final double m00, m01, m02;
    private final double m10, m11, m12;
    private final double m20, m21, m22;

    public EulerAngleRotation(final double phi, final double theta, final double psi,
                              @NonNull final AxesOrder axesOrder) {
        if (!Double.isFinite(phi) || !Double.isFinite(theta) || !Double.isFinite(psi)) {
            throw new IllegalArgumentException("All angles must be finite");
        }

        this.phi = phi;
        this.theta = theta;
        this.psi = psi;
        this.axesOrder = axesOrder;

        final double[][] m = RotationMatrix.build(new double[]{phi, theta, psi}, axesOrder.code());
        this.m00 = m[0][0]; this.m01 = m[0][1]; this.m02 = m[0][2];
        this.m10 = m[1][0]; this.m11 = m[1][1]; this.m12 = m[1][2];
        this.m20 = m[2][0]; this.m21 = m[2][1]; this.m22 = m[2][2];
    }

    /**
     * Convenience overload that accepts a three-character {@link AxesOrder} code (used by
     * ASDF deserializers that read the order as a plain string).
     */
    public EulerAngleRotation(final double phi, final double theta, final double psi,
                              @NonNull final String axesOrder) {
        this(phi, theta, psi, AxesOrder.fromCode(axesOrder));
    }

    public double getPhi() {
        return phi;
    }

    public double getTheta() {
        return theta;
    }

    public double getPsi() {
        return psi;
    }

    public AxesOrder getAxesOrder() {
        return axesOrder;
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
        final double alphaDeg = inputs[inputOffset];
        final double deltaDeg = inputs[inputOffset + 1];

        final double cosDelta = WcsMath.cosd(deltaDeg);
        final double x = cosDelta * WcsMath.cosd(alphaDeg);
        final double y = cosDelta * WcsMath.sind(alphaDeg);
        final double z = WcsMath.sind(deltaDeg);

        final double xr = Math.fma(m00, x, Math.fma(m01, y, m02 * z));
        final double yr = Math.fma(m10, x, Math.fma(m11, y, m12 * z));
        final double zr = Math.fma(m20, x, Math.fma(m21, y, m22 * z));

        final double r = Math.hypot(xr, yr);
        outputs[outputOffset] = WcsMath.atan2d(yr, xr);
        if (Math.abs(zr) > 0.99) {
            outputs[outputOffset + 1] = Math.copySign(WcsMath.acosd(r), zr);
        } else {
            outputs[outputOffset + 1] = WcsMath.atan2d(zr, r);
        }
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    /**
     * Returns the parametric inverse {@code EulerAngleRotation(-psi, -theta, -phi, axesOrder.reverse())}.
     * This identity follows from {@code R = R(psi)·R(theta)·R(phi)} so that
     * {@code R^{-1} = R(-phi)·R(-theta)·R(-psi)} — re-expressed in constructor form, that's
     * {@code phi' = -psi}, {@code theta' = -theta}, {@code psi' = -phi} with the order reversed.
     */
    @Override
    public EulerAngleRotation getInverse() {
        return new EulerAngleRotation(-psi, -theta, -phi, axesOrder.reverse());
    }
}
