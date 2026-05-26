package edu.stsci.gwcs.asdf.converter.transform.projection;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectionConverterTest {

    private final GwcsAsdfSupport support = new GwcsAsdfSupport();

    private static AsdfNode directionNode(final String direction) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.asString()).thenReturn(direction);
        return node;
    }

    private static AsdfNode doubleNode(final double value) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.asDouble()).thenReturn(value);
        return node;
    }

    private static void stubNoMetadata(final AsdfNode node) {
        doReturn(Optional.empty()).when(node).getOptional("name");
        doReturn(Optional.empty()).when(node).getOptional("inputs");
        doReturn(Optional.empty()).when(node).getOptional("outputs");
    }

    private static void stubOptionalDouble(final AsdfNode node, final String key, final double value) {
        final AsdfNode valNode = doubleNode(value);
        doReturn(Optional.of(valNode)).when(node).getOptional(key);
    }

    private static void stubOptionalEmpty(final AsdfNode node, final String key) {
        doReturn(Optional.empty()).when(node).getOptional(key);
    }

    private AsdfNode projectionNode(final String tagName, final String direction) {
        final AsdfNode dirNode = directionNode(direction);
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/" + tagName);
        doReturn(Optional.of(dirNode)).when(node).getOptional("direction");
        stubNoMetadata(node);
        return node;
    }

    private AsdfNode projectionNodeDefaultDirection(final String tagName) {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/" + tagName);
        doReturn(Optional.empty()).when(node).getOptional("direction");
        stubNoMetadata(node);
        return node;
    }

    // --- No-parameter zenithal projections ---

    @Test
    void deserializeGnomonic_pix2sky() {
        final AsdfNode node = projectionNode("gnomonic-1.2.0", "pix2sky");
        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(Gnomonic.class, t);
        assertEquals(Direction.PIX2SKY, ((Projection) t).getDirection());
    }

    @Test
    void deserializeGnomonic_sky2pix() {
        final AsdfNode node = projectionNode("gnomonic-1.3.0", "sky2pix");
        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(Gnomonic.class, t);
        assertEquals(Direction.SKY2PIX, ((Projection) t).getDirection());
    }

    @Test
    void deserializeStereographic() {
        final AsdfNode node = projectionNode("stereographic-1.2.0", "pix2sky");
        assertInstanceOf(Stereographic.class, support.deserializeTransform(node));
    }

    @Test
    void deserializeZenithalEqualArea() {
        final AsdfNode node = projectionNode("zenithal_equal_area-1.3.0", "pix2sky");
        assertInstanceOf(ZenithalEqualArea.class, support.deserializeTransform(node));
    }

    @Test
    void deserializeZenithalEquidistant() {
        final AsdfNode node = projectionNode("zenithal_equidistant-1.2.0", "sky2pix");
        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(ZenithalEquidistant.class, t);
        assertEquals(Direction.SKY2PIX, ((Projection) t).getDirection());
    }

    // --- Parameterized zenithal projections ---

    @Test
    void deserializeZenithalPerspective() {
        final AsdfNode node = projectionNode("zenithal_perspective-1.2.0", "sky2pix");
        stubOptionalDouble(node, "mu", 2.0);
        stubOptionalDouble(node, "gamma", 15.0);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(ZenithalPerspective.class, t);
        assertEquals(Direction.SKY2PIX, ((Projection) t).getDirection());
        assertArrayEquals(
                new ZenithalPerspective(2.0, 15.0, Direction.SKY2PIX).evaluate(30.0, 45.0),
                t.evaluate(30.0, 45.0), 1e-12);
    }

    @Test
    void deserializeZenithalPerspective_defaults() {
        final AsdfNode node = projectionNode("zenithal_perspective-1.3.0", "pix2sky");
        stubOptionalEmpty(node, "mu");
        stubOptionalEmpty(node, "gamma");

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(ZenithalPerspective.class, t);
        assertArrayEquals(
                new ZenithalPerspective(0.0, 0.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeSlantZenithalPerspective() {
        final AsdfNode node = projectionNode("slant_zenithal_perspective-1.2.0", "pix2sky");
        stubOptionalDouble(node, "mu", 1.5);
        stubOptionalDouble(node, "phi0", 30.0);
        stubOptionalDouble(node, "theta0", 60.0);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(SlantZenithalPerspective.class, t);
        assertArrayEquals(
                new SlantZenithalPerspective(1.5, 30.0, 60.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeSlantZenithalPerspective_defaults() {
        final AsdfNode node = projectionNode("slant_zenithal_perspective-1.3.0", "pix2sky");
        stubOptionalEmpty(node, "mu");
        stubOptionalEmpty(node, "phi0");
        stubOptionalEmpty(node, "theta0");

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(SlantZenithalPerspective.class, t);
        assertArrayEquals(
                new SlantZenithalPerspective(0.0, 0.0, 90.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeSlantOrthographic() {
        final AsdfNode node = projectionNode("slant_orthographic-1.2.0", "pix2sky");
        stubOptionalDouble(node, "xi", 0.1);
        stubOptionalDouble(node, "eta", 0.2);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(SlantOrthographic.class, t);
        assertArrayEquals(
                new SlantOrthographic(0.1, 0.2, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeAiry() {
        final AsdfNode node = projectionNode("airy-1.2.0", "pix2sky");
        stubOptionalDouble(node, "theta_b", 45.0);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(Airy.class, t);
        assertArrayEquals(
                new Airy(45.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeAiry_default() {
        final AsdfNode node = projectionNode("airy-1.3.0", "pix2sky");
        stubOptionalEmpty(node, "theta_b");

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(Airy.class, t);
        assertArrayEquals(
                new Airy(90.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    // --- Conic projections ---

    @Test
    void deserializeConicPerspective() {
        final AsdfNode node = projectionNode("conic_perspective-1.2.0", "pix2sky");
        when(node.getDouble("sigma")).thenReturn(45.0);
        stubOptionalDouble(node, "delta", 10.0);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(ConicPerspective.class, t);
        assertArrayEquals(
                new ConicPerspective(45.0, 10.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeConicEqualArea() {
        final AsdfNode node = projectionNode("conic_equal_area-1.3.0", "pix2sky");
        when(node.getDouble("sigma")).thenReturn(45.0);
        stubOptionalDouble(node, "delta", 30.0);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(ConicEqualArea.class, t);
        assertArrayEquals(
                new ConicEqualArea(45.0, 30.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeConicEquidistant() {
        final AsdfNode node = projectionNode("conic_equidistant-1.2.0", "pix2sky");
        when(node.getDouble("sigma")).thenReturn(60.0);
        stubOptionalEmpty(node, "delta");

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(ConicEquidistant.class, t);
        assertArrayEquals(
                new ConicEquidistant(60.0, 0.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeConicOrthomorphic() {
        final AsdfNode node = projectionNode("conic_orthomorphic-1.3.0", "sky2pix");
        when(node.getDouble("sigma")).thenReturn(50.0);
        stubOptionalDouble(node, "delta", 5.0);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(ConicOrthomorphic.class, t);
        assertEquals(Direction.SKY2PIX, ((Projection) t).getDirection());
    }

    // --- Cylindrical projections ---

    @Test
    void deserializeCylindricalPerspective() {
        final AsdfNode node = projectionNode("cylindrical_perspective-1.2.0", "pix2sky");
        stubOptionalDouble(node, "mu", 2.0);
        stubOptionalDouble(node, "lambda", 1.5);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(CylindricalPerspective.class, t);
        assertArrayEquals(
                new CylindricalPerspective(2.0, 1.5, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeCylindricalEqualArea() {
        final AsdfNode node = projectionNode("cylindrical_equal_area-1.3.0", "pix2sky");
        stubOptionalDouble(node, "lambda", 0.5);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(CylindricalEqualArea.class, t);
        assertArrayEquals(
                new CylindricalEqualArea(0.5, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeMercator() {
        final AsdfNode node = projectionNode("mercator-1.2.0", "pix2sky");
        assertInstanceOf(Mercator.class, support.deserializeTransform(node));
    }

    @Test
    void deserializePlateCarree() {
        final AsdfNode node = projectionNode("plate_carree-1.3.0", "sky2pix");
        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(PlateCarree.class, t);
        assertEquals(Direction.SKY2PIX, ((Projection) t).getDirection());
    }

    // --- Pseudoconic projections ---

    @Test
    void deserializeBonneEqualArea() {
        final AsdfNode node = projectionNode("bonne_equal_area-1.2.0", "pix2sky");
        when(node.getDouble("theta1")).thenReturn(45.0);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(BonneEqualArea.class, t);
        assertArrayEquals(
                new BonneEqualArea(45.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializePolyconic() {
        final AsdfNode node = projectionNode("polyconic-1.2.0", "pix2sky");
        assertInstanceOf(Polyconic.class, support.deserializeTransform(node));
    }

    // --- Pseudocylindrical projections ---

    @Test
    void deserializeHammerAitoff() {
        final AsdfNode node = projectionNode("hammer_aitoff-1.2.0", "pix2sky");
        assertInstanceOf(HammerAitoff.class, support.deserializeTransform(node));
    }

    @Test
    void deserializeMollweide() {
        final AsdfNode node = projectionNode("mollweide-1.3.0", "pix2sky");
        assertInstanceOf(Mollweide.class, support.deserializeTransform(node));
    }

    @Test
    void deserializeParabolic() {
        final AsdfNode node = projectionNode("parabolic-1.2.0", "sky2pix");
        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(Parabolic.class, t);
        assertEquals(Direction.SKY2PIX, ((Projection) t).getDirection());
    }

    @Test
    void deserializeSansonFlamsteed() {
        final AsdfNode node = projectionNode("sanson_flamsteed-1.3.0", "pix2sky");
        assertInstanceOf(SansonFlamsteed.class, support.deserializeTransform(node));
    }

    // --- HEALPix projections ---

    @Test
    void deserializeHEALPix() {
        final AsdfNode node = projectionNode("healpix-1.3.0", "pix2sky");
        stubOptionalDouble(node, "H", 4.0);
        stubOptionalDouble(node, "X", 3.0);

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(HEALPix.class, t);
        assertArrayEquals(
                new HEALPix(4.0, 3.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeHEALPix_defaults() {
        final AsdfNode node = projectionNode("healpix-1.2.0", "pix2sky");
        stubOptionalEmpty(node, "H");
        stubOptionalEmpty(node, "X");

        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(HEALPix.class, t);
        assertArrayEquals(
                new HEALPix(4.0, 3.0, Direction.PIX2SKY).evaluate(1.0, 1.0),
                t.evaluate(1.0, 1.0), 1e-12);
    }

    @Test
    void deserializeHEALPixPolar() {
        final AsdfNode node = projectionNode("healpix_polar-1.2.0", "pix2sky");
        assertInstanceOf(HEALPixPolar.class, support.deserializeTransform(node));
    }

    // --- Quad-cube projections ---

    @Test
    void deserializeQuadSphericalCube() {
        final AsdfNode node = projectionNode("quad_spherical_cube-1.2.0", "pix2sky");
        assertInstanceOf(QuadSphericalCube.class, support.deserializeTransform(node));
    }

    @Test
    void deserializeCOBEQuadSphericalCube() {
        final AsdfNode node = projectionNode("cobe_quad_spherical_cube-1.3.0", "pix2sky");
        assertInstanceOf(COBEQuadSphericalCube.class, support.deserializeTransform(node));
    }

    @Test
    void deserializeTangentialSphericalCube() {
        final AsdfNode node = projectionNode("tangential_spherical_cube-1.2.0", "sky2pix");
        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(TangentialSphericalCube.class, t);
        assertEquals(Direction.SKY2PIX, ((Projection) t).getDirection());
    }

    // --- Direction and defaults ---

    @Test
    void defaultDirectionIsPix2Sky() {
        final AsdfNode node = projectionNodeDefaultDirection("gnomonic-1.2.0");
        final Transform t = support.deserializeTransform(node);
        assertInstanceOf(Gnomonic.class, t);
        assertEquals(Direction.PIX2SKY, ((Projection) t).getDirection());
    }

    @Test
    void invalidDirectionThrows() {
        final AsdfNode dirNode = directionNode("forward");
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/gnomonic-1.2.0");
        doReturn(Optional.of(dirNode)).when(node).getOptional("direction");
        stubNoMetadata(node);

        final IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> support.deserializeTransform(node));
        assertTrue(e.getMessage().contains("forward"));
    }

    // --- extractProjectionName ---

    @Test
    void extractProjectionName() {
        assertEquals("gnomonic", ProjectionConverter.extractProjectionName(
                "tag:stsci.edu:asdf/transform/gnomonic-1.2.0"));
        assertEquals("zenithal_perspective", ProjectionConverter.extractProjectionName(
                "tag:stsci.edu:asdf/transform/zenithal_perspective-1.3.0"));
        assertEquals("cobe_quad_spherical_cube", ProjectionConverter.extractProjectionName(
                "tag:stsci.edu:asdf/transform/cobe_quad_spherical_cube-1.2.0"));
    }
}
