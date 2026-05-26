package edu.stsci.gwcs.asdf.converter.transform.projection;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.asdf.converter.AsdfNodeUtils;
import edu.stsci.gwcs.asdf.converter.ConverterBase;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.projection.Projection.Direction;
import edu.stsci.gwcs.transform.projection.conic.ConicEqualArea;
import edu.stsci.gwcs.transform.projection.conic.ConicEquidistant;
import edu.stsci.gwcs.transform.projection.conic.ConicOrthomorphic;
import edu.stsci.gwcs.transform.projection.conic.ConicPerspective;
import edu.stsci.gwcs.transform.projection.cylindrical.CylindricalEqualArea;
import edu.stsci.gwcs.transform.projection.cylindrical.CylindricalPerspective;
import edu.stsci.gwcs.transform.projection.cylindrical.Mercator;
import edu.stsci.gwcs.transform.projection.cylindrical.PlateCarree;
import edu.stsci.gwcs.transform.projection.healpix.HEALPix;
import edu.stsci.gwcs.transform.projection.healpix.HEALPixPolar;
import edu.stsci.gwcs.transform.projection.pseudoconic.BonneEqualArea;
import edu.stsci.gwcs.transform.projection.pseudoconic.Polyconic;
import edu.stsci.gwcs.transform.projection.pseudocylindrical.HammerAitoff;
import edu.stsci.gwcs.transform.projection.pseudocylindrical.Mollweide;
import edu.stsci.gwcs.transform.projection.pseudocylindrical.Parabolic;
import edu.stsci.gwcs.transform.projection.pseudocylindrical.SansonFlamsteed;
import edu.stsci.gwcs.transform.projection.quadcube.COBEQuadSphericalCube;
import edu.stsci.gwcs.transform.projection.quadcube.QuadSphericalCube;
import edu.stsci.gwcs.transform.projection.quadcube.TangentialSphericalCube;
import edu.stsci.gwcs.transform.projection.zenithal.Airy;
import edu.stsci.gwcs.transform.projection.zenithal.Gnomonic;
import edu.stsci.gwcs.transform.projection.zenithal.SlantOrthographic;
import edu.stsci.gwcs.transform.projection.zenithal.SlantZenithalPerspective;
import edu.stsci.gwcs.transform.projection.zenithal.Stereographic;
import edu.stsci.gwcs.transform.projection.zenithal.ZenithalEqualArea;
import edu.stsci.gwcs.transform.projection.zenithal.ZenithalEquidistant;
import edu.stsci.gwcs.transform.projection.zenithal.ZenithalPerspective;
import org.asdfformat.asdf.node.AsdfNode;

import java.util.Set;

public class ProjectionConverter extends ConverterBase {
    private static final String TAG_PREFIX = "tag:stsci.edu:asdf/transform/";

    private static final Set<String> TAGS = Set.of(
            TAG_PREFIX + "gnomonic-1.2.0",
            TAG_PREFIX + "gnomonic-1.3.0",
            TAG_PREFIX + "stereographic-1.2.0",
            TAG_PREFIX + "stereographic-1.3.0",
            TAG_PREFIX + "zenithal_equal_area-1.2.0",
            TAG_PREFIX + "zenithal_equal_area-1.3.0",
            TAG_PREFIX + "zenithal_equidistant-1.2.0",
            TAG_PREFIX + "zenithal_equidistant-1.3.0",
            TAG_PREFIX + "zenithal_perspective-1.2.0",
            TAG_PREFIX + "zenithal_perspective-1.3.0",
            TAG_PREFIX + "slant_zenithal_perspective-1.2.0",
            TAG_PREFIX + "slant_zenithal_perspective-1.3.0",
            TAG_PREFIX + "slant_orthographic-1.2.0",
            TAG_PREFIX + "slant_orthographic-1.3.0",
            TAG_PREFIX + "airy-1.2.0",
            TAG_PREFIX + "airy-1.3.0",
            TAG_PREFIX + "conic_perspective-1.2.0",
            TAG_PREFIX + "conic_perspective-1.3.0",
            TAG_PREFIX + "conic_equal_area-1.2.0",
            TAG_PREFIX + "conic_equal_area-1.3.0",
            TAG_PREFIX + "conic_equidistant-1.2.0",
            TAG_PREFIX + "conic_equidistant-1.3.0",
            TAG_PREFIX + "conic_orthomorphic-1.2.0",
            TAG_PREFIX + "conic_orthomorphic-1.3.0",
            TAG_PREFIX + "cylindrical_perspective-1.2.0",
            TAG_PREFIX + "cylindrical_perspective-1.3.0",
            TAG_PREFIX + "cylindrical_equal_area-1.2.0",
            TAG_PREFIX + "cylindrical_equal_area-1.3.0",
            TAG_PREFIX + "mercator-1.2.0",
            TAG_PREFIX + "mercator-1.3.0",
            TAG_PREFIX + "plate_carree-1.2.0",
            TAG_PREFIX + "plate_carree-1.3.0",
            TAG_PREFIX + "bonne_equal_area-1.2.0",
            TAG_PREFIX + "bonne_equal_area-1.3.0",
            TAG_PREFIX + "polyconic-1.2.0",
            TAG_PREFIX + "polyconic-1.3.0",
            TAG_PREFIX + "hammer_aitoff-1.2.0",
            TAG_PREFIX + "hammer_aitoff-1.3.0",
            TAG_PREFIX + "mollweide-1.2.0",
            TAG_PREFIX + "mollweide-1.3.0",
            TAG_PREFIX + "parabolic-1.2.0",
            TAG_PREFIX + "parabolic-1.3.0",
            TAG_PREFIX + "sanson_flamsteed-1.2.0",
            TAG_PREFIX + "sanson_flamsteed-1.3.0",
            TAG_PREFIX + "healpix-1.2.0",
            TAG_PREFIX + "healpix-1.3.0",
            TAG_PREFIX + "healpix_polar-1.2.0",
            TAG_PREFIX + "healpix_polar-1.3.0",
            TAG_PREFIX + "quad_spherical_cube-1.2.0",
            TAG_PREFIX + "quad_spherical_cube-1.3.0",
            TAG_PREFIX + "cobe_quad_spherical_cube-1.2.0",
            TAG_PREFIX + "cobe_quad_spherical_cube-1.3.0",
            TAG_PREFIX + "tangential_spherical_cube-1.2.0",
            TAG_PREFIX + "tangential_spherical_cube-1.3.0"
    );

    public ProjectionConverter(final GwcsAsdfSupport support) {
        super(support, TAGS);
    }

    @Override
    public Transform fromAsdfNode(final AsdfNode node) {
        final String tag = node.getTag();
        final String projectionName = extractProjectionName(tag);
        final Direction direction = readDirection(node);

        final Projection projection = createProjection(projectionName, node, direction);
        return AsdfNodeUtils.wrapWithNamedTransform(projection, node);
    }

    private static Projection createProjection(final String name, final AsdfNode node, final Direction direction) {
        switch (name) {
            case "gnomonic":
                return new Gnomonic(direction);
            case "stereographic":
                return new Stereographic(direction);
            case "zenithal_equal_area":
                return new ZenithalEqualArea(direction);
            case "zenithal_equidistant":
                return new ZenithalEquidistant(direction);
            case "zenithal_perspective":
                return new ZenithalPerspective(
                        optionalDouble(node, "mu", 0.0),
                        optionalDouble(node, "gamma", 0.0),
                        direction);
            case "slant_zenithal_perspective":
                return new SlantZenithalPerspective(
                        optionalDouble(node, "mu", 0.0),
                        optionalDouble(node, "phi0", 0.0),
                        optionalDouble(node, "theta0", 90.0),
                        direction);
            case "slant_orthographic":
                return new SlantOrthographic(
                        optionalDouble(node, "xi", 0.0),
                        optionalDouble(node, "eta", 0.0),
                        direction);
            case "airy":
                return new Airy(
                        optionalDouble(node, "theta_b", 90.0),
                        direction);
            case "conic_perspective":
                return new ConicPerspective(
                        node.getDouble("sigma"),
                        optionalDouble(node, "delta", 0.0),
                        direction);
            case "conic_equal_area":
                return new ConicEqualArea(
                        node.getDouble("sigma"),
                        optionalDouble(node, "delta", 0.0),
                        direction);
            case "conic_equidistant":
                return new ConicEquidistant(
                        node.getDouble("sigma"),
                        optionalDouble(node, "delta", 0.0),
                        direction);
            case "conic_orthomorphic":
                return new ConicOrthomorphic(
                        node.getDouble("sigma"),
                        optionalDouble(node, "delta", 0.0),
                        direction);
            case "cylindrical_perspective":
                return new CylindricalPerspective(
                        optionalDouble(node, "mu", 1.0),
                        optionalDouble(node, "lambda", 1.0),
                        direction);
            case "cylindrical_equal_area":
                return new CylindricalEqualArea(
                        optionalDouble(node, "lambda", 1.0),
                        direction);
            case "mercator":
                return new Mercator(direction);
            case "plate_carree":
                return new PlateCarree(direction);
            case "bonne_equal_area":
                return new BonneEqualArea(
                        node.getDouble("theta1"),
                        direction);
            case "polyconic":
                return new Polyconic(direction);
            case "hammer_aitoff":
                return new HammerAitoff(direction);
            case "mollweide":
                return new Mollweide(direction);
            case "parabolic":
                return new Parabolic(direction);
            case "sanson_flamsteed":
                return new SansonFlamsteed(direction);
            case "healpix":
                return new HEALPix(
                        optionalDouble(node, "H", 4.0),
                        optionalDouble(node, "X", 3.0),
                        direction);
            case "healpix_polar":
                return new HEALPixPolar(direction);
            case "quad_spherical_cube":
                return new QuadSphericalCube(direction);
            case "cobe_quad_spherical_cube":
                return new COBEQuadSphericalCube(direction);
            case "tangential_spherical_cube":
                return new TangentialSphericalCube(direction);
            default:
                throw new IllegalArgumentException("Unrecognized projection: " + name);
        }
    }

    private static Direction readDirection(final AsdfNode node) {
        final String direction = node.getOptional("direction")
                .map(AsdfNode::asString)
                .orElse("pix2sky");
        if ("pix2sky".equals(direction)) {
            return Direction.PIX2SKY;
        } else if ("sky2pix".equals(direction)) {
            return Direction.SKY2PIX;
        }
        throw new IllegalArgumentException("Unrecognized projection direction: " + direction);
    }

    private static double optionalDouble(final AsdfNode node, final String key, final double defaultValue) {
        return node.getOptional(key)
                .map(AsdfNode::asDouble)
                .orElse(defaultValue);
    }

    static String extractProjectionName(final String tag) {
        final int lastSlash = tag.lastIndexOf('/');
        final int versionDash = tag.lastIndexOf('-');
        return tag.substring(lastSlash + 1, versionDash);
    }
}
