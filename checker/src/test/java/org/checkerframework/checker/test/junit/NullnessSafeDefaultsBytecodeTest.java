package org.checkerframework.checker.test.junit;

import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.List;

/** JUnit tests for the Nullness checker when using safe defaults for unannotated bytecode. */
public class NullnessSafeDefaultsBytecodeTest extends CheckerFrameworkPerDirectoryTest {

    /**
     * Create a NullnessSafeDefaultsBytecodeTest.
     *
     * @param testFiles the files containing test code, which will be type-checked
     */
    public NullnessSafeDefaultsBytecodeTest(List<File> testFiles) {
        super(
                testFiles,
                org.checkerframework.checker.nullness.NullnessChecker.class,
                "nullness",
                "-AuseConservativeDefaultsForUncheckedCode=bytecode",
                "-Anomsgtext");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"nullness-safedefaultsbytecode"};
    }
}
