package bsh;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Run the old test scripts inherited from beanshell.
 * It's not always clear what the test cases do so this will need some more investigations for failing tests.
 */
public class OldScriptsTest {

	private static final Set<String> KNOWN_FAILING_TESTS;

	static {
		KNOWN_FAILING_TESTS = new HashSet<String>();
		KNOWN_FAILING_TESTS.add("class13.bsh");
		KNOWN_FAILING_TESTS.add("class3.bsh");
		KNOWN_FAILING_TESTS.add("classinterf1.bsh");
		KNOWN_FAILING_TESTS.add("commands.bsh");
		KNOWN_FAILING_TESTS.add("run.bsh");
        KNOWN_FAILING_TESTS.add("accessibility.bsh");
	}

	public static junit.framework.Test suite() throws Exception {
		final TestSuite suite = new TestSuite();
        String path = OldScriptsTest.class.getResource("/test-scripts/Fail.bsh").getPath();
        final File baseFailScript = new File(path);
		final File baseDir = new File(baseFailScript.getParent());
		try {
			new TestBshScript(baseFailScript).runTest();
			Assert.fail("Fail.bsh should fail!");
		} catch (final AssertionError e) {
			// expected
		}
		addTests(baseDir, suite);
		return suite;
	}


	private static void addTests(File baseDir, TestSuite suite) {
		final File[] files = baseDir.listFiles();
		if (files != null) {
			for (final File file : files) {
				final String name = file.getName();
				if (file.isFile() && name.endsWith(".bsh") && ! "TestHarness.bsh".equals(name) && ! "RunAllTests.bsh".equals(name) && ! "Fail.bsh".equals(name)) {
					if (KnownIssue.SKIP_KOWN_ISSUES && KNOWN_FAILING_TESTS.contains(name)) {
						System.out.println("skipping test " + file);
						continue;
					}
					suite.addTest(new TestBshScript(file));
				}
			}
		}
	}


	static class TestBshScript extends TestCase {

		private File _file;

		public TestBshScript(final File file) {
			_file = file;
		}


		@Override
		public String getName() {
			return _file.getName();
		}


		@Override
		public void runTest() throws Exception {
			System.out.println("file is " + _file.getAbsolutePath());
			final Interpreter interpreter = new Interpreter();
			final String path = '\"' + _file.getParentFile().getAbsolutePath().replace('\\', '/') + '\"';
			interpreter.eval("path=" + path + ';');
			interpreter.eval("cd(" + path + ");");
			interpreter.eval(new FileReader(_file));
			assertEquals("'test_completed' flag check", Boolean.TRUE, interpreter.get("test_completed"));
			assertEquals("'test_failed' flag check", Boolean.FALSE, interpreter.get("test_failed"));
		}

	}
}
