package edu.stsci.gwcs.testing;

import edu.stsci.gwcs.Wcs;
import edu.stsci.gwcs.asdf.GwcsAsdfSupport;
import edu.stsci.gwcs.transform.Transform;
import org.asdfformat.asdf.Asdf;
import org.asdfformat.asdf.AsdfFile;
import org.asdfformat.asdf.node.AsdfNode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@Tag("reference-tests")
public class TransformReferenceTest {

    @ParameterizedTest(name = "{0}")
    @EnumSource(GwcsReferenceFileType.class)
    void referenceTest(final GwcsReferenceFileType referenceFileType) throws IOException {
        final Path path = ReferenceFileUtils.getPath(referenceFileType);

        try (final AsdfFile asdfFile = Asdf.open(path)) {
            final AsdfNode testCases = asdfFile.getTree().get("test_cases");

            for (int i = 0; i < testCases.size(); i++) {
                final AsdfNode testCase = testCases.get((long) i);
                final String testCaseName = testCase.getString("name");
                final String label = referenceFileType.getName() + "/" + testCaseName;

                final double tolerance = testCase.getOptional("tolerance")
                        .map(AsdfNode::asDouble)
                        .orElse(TestingUtils.DOUBLE_TOLERANCE);

                if (testCase.getOptional("fixture_path").isPresent()) {
                    verifyFixtureWcs(testCase, label, tolerance);
                } else if (testCase.getOptional("wcs").isPresent()) {
                    verifyWcs(testCase, label, tolerance);
                } else {
                    verifyTransform(testCase, label, tolerance);
                }
            }
        }
    }

    private void verifyTransform(final AsdfNode testCase, final String label,
                                 final double tolerance) {
        final Transform transform = GwcsAsdfSupport.instance().deserializeTransform(
                testCase.get("transform"));

        verifyForward(transform, testCase, label, tolerance);

        if (testCase.getOptional("has_inverse").map(AsdfNode::asBoolean).orElse(false)) {
            final Transform inverse = transform.getInverse();
            verifyInverse(inverse, testCase, label, tolerance);
        }
    }

    private void verifyWcs(final AsdfNode testCase, final String label,
                           final double tolerance) {
        final Wcs wcs = GwcsAsdfSupport.instance().deserializeWcs(testCase.get("wcs"));

        verifyForwardWcs(wcs, testCase, label, tolerance);

        if (testCase.getOptional("has_inverse").map(AsdfNode::asBoolean).orElse(false)) {
            verifyInverseWcs(wcs, testCase, label, tolerance);
        }
    }

    private void verifyFixtureWcs(final AsdfNode testCase, final String label,
                                  final double tolerance) throws IOException {
        final String fixturePath = testCase.getString("fixture_path");
        final Path fixtureFile = Path.of("src/test/resources/" + fixturePath);

        final Wcs wcs;
        try (final AsdfFile fixtureAsdf = Asdf.open(fixtureFile)) {
            final AsdfNode wcsNode = fixtureAsdf.getTree().get("roman").get("meta").get("wcs");
            wcs = GwcsAsdfSupport.instance().deserializeWcs(wcsNode);
        }

        verifyForwardWcs(wcs, testCase, label, tolerance);

        if (testCase.getOptional("has_inverse").map(AsdfNode::asBoolean).orElse(false)) {
            verifyInverseWcs(wcs, testCase, label, tolerance);
        }
    }

    private void verifyForward(final Transform transform, final AsdfNode testCase,
                               final String label, final double tolerance) {
        final double[][] inputs = readNdArray2D(testCase.get("forward_inputs"));
        final double[][] expectedOutputs = readNdArray2D(testCase.get("forward_outputs"));

        for (int i = 0; i < inputs.length; i++) {
            final double[] actual = transform.evaluate(inputs[i]);
            assertArrayEquals(
                    expectedOutputs[i], actual, tolerance,
                    String.format("%s forward[%d]: input=%s", label, i, formatArray(inputs[i]))
            );
        }
    }

    private void verifyInverse(final Transform inverse, final AsdfNode testCase,
                               final String label, final double tolerance) {
        final double[][] inputs = readNdArray2D(testCase.get("inverse_inputs"));
        final double[][] expectedOutputs = readNdArray2D(testCase.get("inverse_outputs"));

        for (int i = 0; i < inputs.length; i++) {
            final double[] actual = inverse.evaluate(inputs[i]);
            assertArrayEquals(
                    expectedOutputs[i], actual, tolerance,
                    String.format("%s inverse[%d]: input=%s", label, i, formatArray(inputs[i]))
            );
        }
    }

    private void verifyForwardWcs(final Wcs wcs, final AsdfNode testCase,
                                  final String label, final double tolerance) {
        final double[][] inputs = readNdArray2D(testCase.get("forward_inputs"));
        final double[][] expectedOutputs = readNdArray2D(testCase.get("forward_outputs"));

        for (int i = 0; i < inputs.length; i++) {
            final double[] actual = wcs.evaluate(inputs[i]);
            assertArrayEquals(
                    expectedOutputs[i], actual, tolerance,
                    String.format("%s forward[%d]: input=%s", label, i, formatArray(inputs[i]))
            );
        }
    }

    private void verifyInverseWcs(final Wcs wcs, final AsdfNode testCase,
                                  final String label, final double tolerance) {
        final double[][] inputs = readNdArray2D(testCase.get("inverse_inputs"));
        final double[][] expectedOutputs = readNdArray2D(testCase.get("inverse_outputs"));

        for (int i = 0; i < inputs.length; i++) {
            final double[] actual = wcs.evaluateInverse(inputs[i]);
            assertArrayEquals(
                    expectedOutputs[i], actual, tolerance,
                    String.format("%s inverse[%d]: input=%s", label, i, formatArray(inputs[i]))
            );
        }
    }

    private static double[][] readNdArray2D(final AsdfNode node) {
        final var ndArray = node.asNdArray().asDoubleNdArray();
        final int rows = ndArray.getShape().get(0);
        final int cols = ndArray.getShape().get(1);
        final double[][] result = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result[r][c] = ndArray.get(r, c);
            }
        }
        return result;
    }

    private static String formatArray(final double[] values) {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(values[i]);
        }
        return sb.append("]").toString();
    }
}
