package edu.stsci.gwcs.transform.fits;

import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.projection.Projection.Direction;
import edu.stsci.gwcs.transform.util.WcsMath;
import lombok.NonNull;
import edu.stsci.gwcs.transform.projection.zenithal.Gnomonic;
import edu.stsci.gwcs.transform.rotation.RotateCelestial2Native;
import edu.stsci.gwcs.transform.rotation.RotateNative2Celestial;
import edu.stsci.gwcs.transform.Transform;

/**
 * Composes the standard FITS-WCS imaging pipeline:
 * <ol>
 *   <li>CRPIX shift,</li>
 *   <li>CD matrix multiplication (CDELT x PC),</li>
 *   <li>Celestial projection (e.g. {@link Gnomonic} for {@code TAN}), and</li>
 *   <li>{@link RotateNative2Celestial} rotation to celestial coordinates.</li>
 * </ol>
 *
 * <p>When {@code lonPole} is not supplied, it is derived using the FITS WCS Paper II §2.4 default:
 * {@code lonPole = 180} when {@code crval[1] < 90}, else {@code lonPole = 0} (matching wcslib
 * {@code cel.c}). This only matters near the celestial pole where longitude is ambiguous.
 *
 * <p>{@code crpix}, {@code crval}, and the CD matrix are stored as individual scalar fields
 * rather than arrays to avoid per-call indexing overhead.
 */
public class FitsWcsImaging implements Transform {
    /**
     * Threshold below which the CD matrix determinant is treated as singular. The inverse
     * is computed by dividing by det, so a near-zero det produces infinities or NaNs.
     */
    private static final double SINGULAR_DET_THRESHOLD = Double.MIN_NORMAL;

    private final double crpix1;
    private final double crpix2;
    private final double crval1;
    private final double crval2;
    private final double cd11;
    private final double cd12;
    private final double cd21;
    private final double cd22;
    private final double cdInverse11;
    private final double cdInverse12;
    private final double cdInverse21;
    private final double cdInverse22;
    private final double lonPole;
    private final Direction direction;

    private final Projection projection;
    private final Transform celestialRotation;

    /**
     * Construct a FITS-WCS imaging pipeline with a specified projection and the FITS-default
     * {@code lonPole} derivation (FITS WCS Paper II §2.4 / wcslib {@code cel.c:282}).
     * {@code crpix} uses 0-based pixel coordinates (the center of the first pixel is 0.0).
     */
    public FitsWcsImaging(@NonNull final Projection projection,
                          @NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[] cdelt,
                          @NonNull final double[][] pc) {
        this(projection, crpix, crval, cdelt, pc, defaultLonPole(crval, projection));
    }

    /**
     * Construct a FITS-WCS imaging pipeline with a specified projection and explicit {@code lonPole}.
     */
    public FitsWcsImaging(@NonNull final Projection projection,
                          @NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[] cdelt,
                          @NonNull final double[][] pc,
                          final double lonPole) {
        this(projection, crpix, crval, buildCd(cdelt, pc), lonPole, Direction.PIX2SKY);
    }

    /**
     * Construct a FITS-WCS imaging pipeline with TAN projection using CDELT/PC and default lonPole.
     */
    public FitsWcsImaging(@NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[] cdelt,
                          @NonNull final double[][] pc) {
        this(new Gnomonic(), crpix, crval, cdelt, pc);
    }

    /**
     * Construct a FITS-WCS imaging pipeline with TAN projection using CDELT/PC and explicit lonPole.
     */
    public FitsWcsImaging(@NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[] cdelt,
                          @NonNull final double[][] pc,
                          final double lonPole) {
        this(new Gnomonic(), crpix, crval, buildCd(cdelt, pc), lonPole, Direction.PIX2SKY);
    }

    /**
     * Construct a FITS-WCS imaging pipeline with a specified projection from a pre-built CD matrix.
     */
    public FitsWcsImaging(@NonNull final Projection projection,
                          @NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[][] cd) {
        this(projection, crpix, crval, cd, defaultLonPole(crval, projection), Direction.PIX2SKY);
    }

    /**
     * Construct a FITS-WCS imaging pipeline with a specified projection, pre-built CD matrix,
     * and explicit lonPole.
     */
    public FitsWcsImaging(@NonNull final Projection projection,
                          @NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[][] cd,
                          final double lonPole) {
        this(projection, crpix, crval, cd, lonPole, Direction.PIX2SKY);
    }

    /**
     * Construct a FITS-WCS imaging pipeline with TAN projection from a pre-built CD matrix.
     */
    public FitsWcsImaging(@NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[][] cd) {
        this(new Gnomonic(), crpix, crval, cd);
    }

    /**
     * Construct a FITS-WCS imaging pipeline with TAN projection, pre-built CD matrix,
     * and explicit lonPole.
     */
    public FitsWcsImaging(@NonNull final double[] crpix,
                          @NonNull final double[] crval,
                          @NonNull final double[][] cd,
                          final double lonPole) {
        this(new Gnomonic(), crpix, crval, cd, lonPole, Direction.PIX2SKY);
    }

    private FitsWcsImaging(@NonNull final Projection projectionTemplate,
                           @NonNull final double[] crpix,
                           @NonNull final double[] crval,
                           @NonNull final double[][] cd,
                           final double lonPole,
                           final Direction direction) {
        if (crpix.length != 2) {
            throw new IllegalArgumentException("crpix must have exactly 2 elements, got " + crpix.length);
        }
        if (crval.length != 2) {
            throw new IllegalArgumentException("crval must have exactly 2 elements, got " + crval.length);
        }
        if (cd.length != 2 || cd[0].length != 2 || cd[1].length != 2) {
            throw new IllegalArgumentException(
                    "cd must be a 2x2 matrix, got " + cd.length + "x"
                            + (cd.length > 0 ? cd[0].length : 0)
            );
        }
        if (!Double.isFinite(lonPole)) {
            throw new IllegalArgumentException("lonPole must be finite, got " + lonPole);
        }

        this.crpix1 = crpix[0];
        this.crpix2 = crpix[1];
        this.crval1 = crval[0];
        this.crval2 = crval[1];
        this.cd11 = cd[0][0];
        this.cd12 = cd[0][1];
        this.cd21 = cd[1][0];
        this.cd22 = cd[1][1];
        this.lonPole = lonPole;
        this.direction = direction;

        final double det = this.cd11 * this.cd22 - this.cd12 * this.cd21;
        if (!Double.isFinite(det) || Math.abs(det) <= SINGULAR_DET_THRESHOLD) {
            throw new IllegalArgumentException(
                    String.format("CD matrix is singular (determinant = %s)", det)
            );
        }

        this.cdInverse11 = this.cd22 / det;
        this.cdInverse12 = -this.cd12 / det;
        this.cdInverse21 = -this.cd21 / det;
        this.cdInverse22 = this.cd11 / det;

        final double[] celestialPole = celset(this.crval1, this.crval2, this.lonPole, projectionTemplate);

        if (direction == Direction.PIX2SKY) {
            this.projection = projectionTemplate.getDirection() == Direction.PIX2SKY
                    ? projectionTemplate : (Projection) projectionTemplate.getInverse();
            this.celestialRotation = new RotateNative2Celestial(celestialPole[0], celestialPole[1], this.lonPole);
        } else {
            this.projection = projectionTemplate.getDirection() == Direction.SKY2PIX
                    ? projectionTemplate : (Projection) projectionTemplate.getInverse();
            this.celestialRotation = new RotateCelestial2Native(celestialPole[0], celestialPole[1], this.lonPole);
        }
    }

    /** Default lonPole per FITS WCS Paper II §2.4, generalized for all projection families. */
    private static double defaultLonPole(final double[] crval, final Projection projection) {
        if (crval.length != 2) {
            throw new IllegalArgumentException("crval must have exactly 2 elements, got " + crval.length);
        }
        return (crval[1] < projection.getTheta0() ? 180.0 : 0.0) + projection.getPhi0();
    }

    /**
     * Compute the celestial coordinates of the native pole following wcslib cel.c:celset().
     * Returns {lngp, latp} — the longitude and latitude of the celestial pole in the
     * native coordinate system, which are used to construct the celestial rotation.
     *
     * <p>For zenithal projections (theta0 == 90), this reduces to lngp = lng0, latp = lat0.
     * For other projections (e.g. conics where theta0 = sigma), the native latitude of the
     * celestial pole must be solved from FITS Paper II equation 2.
     */
    private static double[] celset(final double lng0, final double lat0, final double phip,
                                   final Projection projection) {
        final double tol = 1.0e-10;
        final double theta0 = projection.getTheta0();
        final double phi0 = projection.getPhi0();

        if (theta0 == 90.0) {
            return new double[]{lng0, lat0};
        }

        final double slat0 = WcsMath.sind(lat0);
        final double clat0 = WcsMath.cosd(lat0);
        final double sthe0 = WcsMath.sind(theta0);
        final double cthe0 = WcsMath.cosd(theta0);

        final double sphip = WcsMath.sind(phip - phi0);
        final double cphip = WcsMath.cosd(phip - phi0);

        final double x = cthe0 * cphip;
        final double y = sthe0;
        final double z = Math.sqrt(x * x + y * y);

        double latp;
        if (z == 0.0) {
            if (slat0 != 0.0) {
                throw new IllegalArgumentException(
                        "Invalid coordinate description: lat0 == 0 required for |phip - phi0| = 90 and theta0 == 0");
            }
            latp = 90.0;
        } else {
            double slz = slat0 / z;
            if (Math.abs(slz) > 1.0) {
                if (Math.abs(slz) - 1.0 < tol) {
                    slz = Math.copySign(1.0, slz);
                } else {
                    throw new IllegalArgumentException(
                            "Invalid coordinate description: |lat0| <= " + WcsMath.asind(z) +
                                    " required for these values of phip, phi0, and theta0");
                }
            }

            final double u = WcsMath.atan2d(y, x);
            final double v = WcsMath.acosd(slz);

            final double latp1 = normalizeLatp(u + v);
            final double latp2 = normalizeLatp(u - v);

            // FITS Paper II default: latpole = 90.  Choose the solution closest to it.
            final double latpoleDefault = 90.0;
            if (Math.abs(latpoleDefault - latp1) < Math.abs(latpoleDefault - latp2)) {
                latp = Math.abs(latp1) < 90.0 + tol ? latp1 : latp2;
            } else {
                latp = Math.abs(latp2) < 90.0 + tol ? latp2 : latp1;
            }

            if (latp > 90.0) {
                latp = 90.0;
            } else if (latp < -90.0) {
                latp = -90.0;
            }
        }

        double lngp;
        final double clatp = WcsMath.cosd(latp);
        final double zz = clatp * clat0;
        if (Math.abs(zz) < tol) {
            if (Math.abs(clat0) < tol) {
                lngp = lng0;
            } else if (latp > 0.0) {
                lngp = lng0 + phip - phi0 - 180.0;
            } else {
                lngp = lng0 - phip + phi0;
            }
        } else {
            final double xx = (sthe0 - WcsMath.sind(latp) * slat0) / zz;
            final double yy = sphip * cthe0 / clat0;
            lngp = lng0 - WcsMath.atan2d(yy, xx);
        }

        if (lng0 >= 0.0) {
            if (lngp < 0.0) {
                lngp += 360.0;
            } else if (lngp > 360.0) {
                lngp -= 360.0;
            }
        } else {
            if (lngp > 0.0) {
                lngp -= 360.0;
            } else if (lngp < -360.0) {
                lngp += 360.0;
            }
        }

        return new double[]{lngp, latp};
    }

    private static double normalizeLatp(final double value) {
        if (value > 180.0) {
            return value - 360.0;
        } else if (value < -180.0) {
            return value + 360.0;
        }
        return value;
    }

    private static double[][] buildCd(final double[] cdelt, final double[][] pc) {
        if (cdelt.length != 2) {
            throw new IllegalArgumentException("cdelt must have exactly 2 elements, got " + cdelt.length);
        }
        if (pc.length != 2 || pc[0].length != 2 || pc[1].length != 2) {
            throw new IllegalArgumentException(
                    "pc must be a 2x2 matrix, got " + pc.length + "x"
                            + (pc.length > 0 ? pc[0].length : 0)
            );
        }
        return new double[][]{
                {cdelt[0] * pc[0][0], cdelt[0] * pc[0][1]},
                {cdelt[1] * pc[1][0], cdelt[1] * pc[1][1]}
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
        if (direction == Direction.SKY2PIX) {
            evaluateInverse(inputs, inputOffset, outputs, outputOffset);
        } else {
            evaluateForward(inputs, inputOffset, outputs, outputOffset);
        }
    }

    private void evaluateForward(final double[] inputs, final int inputOffset,
                                 final double[] outputs, final int outputOffset) {
        final double inX = inputs[inputOffset];
        final double inY = inputs[inputOffset + 1];
        if (Double.isNaN(inX) || Double.isNaN(inY)) {
            outputs[outputOffset] = Double.NaN;
            outputs[outputOffset + 1] = Double.NaN;
            return;
        }
        final double u = inX - crpix1;
        final double v = inY - crpix2;

        outputs[outputOffset] = Math.fma(cd11, u, cd12 * v);
        outputs[outputOffset + 1] = Math.fma(cd21, u, cd22 * v);

        projection.evaluate(outputs, outputOffset, outputs, outputOffset);

        celestialRotation.evaluate(outputs, outputOffset, outputs, outputOffset);
    }

    private void evaluateInverse(final double[] inputs, final int inputOffset,
                                 final double[] outputs, final int outputOffset) {
        final double inLon = inputs[inputOffset];
        final double inLat = inputs[inputOffset + 1];
        if (Double.isNaN(inLon) || Double.isNaN(inLat)) {
            outputs[outputOffset] = Double.NaN;
            outputs[outputOffset + 1] = Double.NaN;
            return;
        }

        celestialRotation.evaluate(inputs, inputOffset, outputs, outputOffset);

        double phi = outputs[outputOffset];
        final double theta = outputs[outputOffset + 1];

        if (Double.isNaN(phi) || Double.isNaN(theta)) {
            outputs[outputOffset] = Double.NaN;
            outputs[outputOffset + 1] = Double.NaN;
            return;
        }

        // Normalize native phi to (-180, 180] to match wcslib's sphs2x convention.
        // Projections like conics use phi linearly (alpha = c * phi), so values
        // near 360 produce wrong intermediate coordinates.
        phi %= 360.0;
        if (phi > 180.0) {
            phi -= 360.0;
        } else if (phi <= -180.0) {
            phi += 360.0;
        }
        outputs[outputOffset] = phi;

        projection.evaluate(outputs, outputOffset, outputs, outputOffset);

        final double xDeg = outputs[outputOffset];
        final double yDeg = outputs[outputOffset + 1];

        if (!Double.isFinite(xDeg) || !Double.isFinite(yDeg)) {
            outputs[outputOffset] = Double.NaN;
            outputs[outputOffset + 1] = Double.NaN;
            return;
        }

        final double u = Math.fma(cdInverse11, xDeg, cdInverse12 * yDeg);
        final double v = Math.fma(cdInverse21, xDeg, cdInverse22 * yDeg);

        outputs[outputOffset] = u + crpix1;
        outputs[outputOffset + 1] = v + crpix2;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Transform getInverse() {
        final Direction oppositeDirection = (direction == Direction.PIX2SKY)
                ? Direction.SKY2PIX : Direction.PIX2SKY;
        // Pass the current projection — the private constructor will flip its direction
        return new FitsWcsImaging(
                this.projection,
                new double[]{crpix1, crpix2},
                new double[]{crval1, crval2},
                new double[][]{
                        {cd11, cd12},
                        {cd21, cd22}
                },
                lonPole,
                oppositeDirection
        );
    }
}
