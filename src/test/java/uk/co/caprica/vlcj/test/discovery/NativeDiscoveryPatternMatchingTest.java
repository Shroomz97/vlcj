package uk.co.caprica.vlcj.test.discovery;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.discovery.linux.DefaultLinuxNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NativeDiscoveryPatternMatchingTest {

    private static final File testDirectory = new File(System.getProperty("java.io.tmpdir") + "/vlcj-test");

    private static void setUp() {
        testDirectory.mkdirs();
        deleteTestFiles();
    }

    private static void tearDown() {
        deleteTestFiles();
        testDirectory.delete();
    }

    private static void deleteTestFiles() {
        if (testDirectory != null) {
            File[] toDelete = testDirectory.listFiles();
            if (toDelete != null) {
                for (File file : toDelete) {
                    file.delete();
                }
            }
        }
    }

    private static class TestStrategy extends DefaultLinuxNativeDiscoveryStrategy {

        private final List<File> files = new ArrayList<File>();

        private TestStrategy(String... fileNames) throws IOException {
            for (String fileName : fileNames) {
                files.add(new File(testDirectory, fileName));
            }
        }

        @Override
        protected void onGetDirectoryNames(List<String> directoryNames) {
            directoryNames.add(testDirectory.getAbsolutePath());
        }
    }

    public static void main(String[] args) throws Exception {
        if (RuntimeUtil.isNix()) {
            testSuccess();
            testFailure();
        }
    }

    private static void testSuccess() throws Exception {
        setUp();
        try {
            // There is no libvlccore, but multiple libvlc, this test must fail
            if (new NativeDiscovery(new TestStrategy("libvlc.so", "libvlc.so.5", "libvlc.so.5.6.0")).discover() != false) {
                System.out.println("Discovery must fail [FAIL]");
            }
            else {
                System.out.println("Discovery failed [OK]");
            }
        }
        finally {
            tearDown();
        }
    }

    private static void testFailure() throws Exception {
        setUp();
        try {
            // There is at least one libvlc and at least one libvlccore, this test must pass
            if (new NativeDiscovery(new TestStrategy("libvlc.so", "libvlc.so.5", "libvlc.so.5.6.0", "libvlccore.so")).discover() != false) {
                System.out.println("Discovery must succeed [FAIL]");
            }
            else {
                System.out.println("Discovery succeeded [OK]");
            }
        }
        finally {
            tearDown();
        }
    }
}
