package edu.stsci.gwcs.testing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class ReferenceFileUtils {
    private static final String PYTHON_PATH = System.getenv("GWCS_JAVA_TESTS_PYTHON_PATH");
    private static final Path GENERATOR_SCRIPT_PATH = extractGeneratorScript();

    private static final Map<String, Path> CACHE = new HashMap<>();

    private static Path extractGeneratorScript() {
        try {
            final Path path = Files.createTempFile("reference_file_generator_", ".py");
            path.toFile().deleteOnExit();

            try (
                    final InputStream input = Optional.ofNullable(
                                    ReferenceFileUtils.class.getResourceAsStream("/testing/reference_file_generator.py"))
                            .orElseThrow(() -> new RuntimeException("Missing testing/reference_file_generator.py"));
                    final OutputStream output = Files.newOutputStream(path, StandardOpenOption.CREATE)
            ) {
                input.transferTo(output);
            }

            return path;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to extract generator script", e);
        }
    }

    public static Path getPath(final ReferenceFile referenceFile) {
        final String key = referenceFile.getName();

        if (!CACHE.containsKey(key)) {
            CACHE.put(key, generateTestFile(referenceFile));
        }

        return CACHE.get(key);
    }

    private static Path generateTestFile(final ReferenceFile referenceFile) {
        assumeTrue(
                Optional.ofNullable(PYTHON_PATH)
                        .filter(p -> !p.isEmpty())
                        .map(p -> Files.exists(Paths.get(p)))
                        .orElse(false),
                "GWCS_JAVA_TESTS_PYTHON_PATH missing or unset"
        );

        try {
            final InputStream scriptInput = referenceFile.openScript();
            assumeTrue(scriptInput != null,
                    "Reference script not found for " + referenceFile.getName());

            final Path outputPath = Files.createTempFile(referenceFile.getName() + "-", ".asdf");
            outputPath.toFile().deleteOnExit();

            final Process process = new ProcessBuilder(PYTHON_PATH, GENERATOR_SCRIPT_PATH.toString()).start();

            try (scriptInput; final OutputStream processInput = process.getOutputStream()) {
                scriptInput.transferTo(processInput);
            }

            try (
                    final InputStream processOutput = process.getInputStream();
                    final OutputStream fileOutput = Files.newOutputStream(outputPath, StandardOpenOption.CREATE)
            ) {
                processOutput.transferTo(fileOutput);
            }

            final int exitCode = process.waitFor();
            if (exitCode != 0) {
                final String stderr = new String(process.getErrorStream().readAllBytes());
                throw new RuntimeException(
                        "Python generator failed for " + referenceFile.getName()
                                + " (exit code " + exitCode + "): " + stderr);
            }

            return outputPath;
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException("Failed to generate reference file for " + referenceFile.getName(), e);
        }
    }
}
