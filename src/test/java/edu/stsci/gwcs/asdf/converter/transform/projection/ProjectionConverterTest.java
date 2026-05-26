package edu.stsci.gwcs.asdf.converter.transform.projection;

import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import edu.stsci.gwcs.transform.projection.Projection;
import edu.stsci.gwcs.transform.projection.Projection.Direction;
import edu.stsci.gwcs.transform.projection.conic.ConicEqualArea;
import edu.stsci.gwcs.transform.projection.zenithal.Gnomonic;
import edu.stsci.gwcs.transform.projection.zenithal.ZenithalPerspective;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectionConverterTest {
    @Test
    void deserializeGnomonic_pix2sky() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/gnomonic-1.2.0");
        final AsdfNode dirNode = mock(AsdfNode.class);
        when(dirNode.asString()).thenReturn("pix2sky");
        when(node.getOptional("direction")).thenReturn(Optional.of(dirNode));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Gnomonic.class, transform);
        assertEquals(Direction.PIX2SKY, ((Projection) transform).getDirection());
    }

    @Test
    void deserializeGnomonic_sky2pix() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/gnomonic-1.3.0");
        final AsdfNode dirNode = mock(AsdfNode.class);
        when(dirNode.asString()).thenReturn("sky2pix");
        when(node.getOptional("direction")).thenReturn(Optional.of(dirNode));
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Gnomonic.class, transform);
        assertEquals(Direction.SKY2PIX, ((Projection) transform).getDirection());
    }

    @Test
    void deserializeZenithalPerspective() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/zenithal_perspective-1.2.0");
        final AsdfNode dirNode = mock(AsdfNode.class);
        when(dirNode.asString()).thenReturn("sky2pix");
        when(node.getOptional("direction")).thenReturn(Optional.of(dirNode));

        final AsdfNode muNode = mock(AsdfNode.class);
        when(muNode.asDouble()).thenReturn(2.0);
        when(node.getOptional("mu")).thenReturn(Optional.of(muNode));

        final AsdfNode gammaNode = mock(AsdfNode.class);
        when(gammaNode.asDouble()).thenReturn(0.0);
        when(node.getOptional("gamma")).thenReturn(Optional.of(gammaNode));

        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(ZenithalPerspective.class, transform);
        assertEquals(Direction.SKY2PIX, ((Projection) transform).getDirection());
    }

    @Test
    void deserializeConicEqualArea() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/conic_equal_area-1.3.0");
        final AsdfNode dirNode = mock(AsdfNode.class);
        when(dirNode.asString()).thenReturn("pix2sky");
        when(node.getOptional("direction")).thenReturn(Optional.of(dirNode));
        when(node.getDouble("sigma")).thenReturn(45.0);

        final AsdfNode deltaNode = mock(AsdfNode.class);
        when(deltaNode.asDouble()).thenReturn(30.0);
        when(node.getOptional("delta")).thenReturn(Optional.of(deltaNode));

        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(ConicEqualArea.class, transform);
        assertEquals(Direction.PIX2SKY, ((Projection) transform).getDirection());
    }

    @Test
    void deserializeGnomonic_defaultDirection() {
        final AsdfNode node = mock(AsdfNode.class);
        when(node.getTag()).thenReturn("tag:stsci.edu:asdf/transform/gnomonic-1.2.0");
        when(node.getOptional("direction")).thenReturn(Optional.empty());
        when(node.getOptional("name")).thenReturn(Optional.empty());
        when(node.getOptional("inputs")).thenReturn(Optional.empty());
        when(node.getOptional("outputs")).thenReturn(Optional.empty());

        final GwcsAsdfSupport support = new GwcsAsdfSupport();
        final Transform transform = support.deserializeTransform(node);

        assertInstanceOf(Gnomonic.class, transform);
        assertEquals(Direction.PIX2SKY, ((Projection) transform).getDirection());
    }

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
