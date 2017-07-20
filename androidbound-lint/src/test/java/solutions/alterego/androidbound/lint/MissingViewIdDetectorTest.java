package solutions.alterego.androidbound.lint;

import com.google.common.collect.ImmutableList;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.tools.lint.checks.infrastructure.TestFiles;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;

import java.util.List;

public final class MissingViewIdDetectorTest extends LintDetectorTest {

    private static final TestFile LAYOUT_BUTTON = TestFiles
            .xml("layout.xml", "<ScrollView xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
                    + "            xmlns:tools=\"http://schemas.android.com/tools\"\n"
                    + "            android:id=\"@+id/drawer_layout\"\n"
                    + "            android:layout_width=\"match_parent\"\n"
                    + "            android:layout_height=\"match_parent\"\n"
                    + "            tools:ignore=\"MissingPrefix\"\n"
                    + "            android:padding=\"16dp\"\n"
                    + "            binding=\"{{ BackgroundColor @= MainActivityBackgroundColor }}\"><!-- these double {{ and }} are just here for testing the parser - don't do this! -->\n"
                    + "\n"
                    + "    <LinearLayout\n"
                    + "            android:layout_width=\"match_parent\"\n"
                    + "            android:layout_height=\"wrap_content\"\n"
                    + "            android:orientation=\"vertical\"\n"
                    + "            android:gravity=\"center\">\n"
                    + "\n"
                    + "        <Button\n"
                    + "                android:layout_width=\"wrap_content\"\n"
                    + "                android:layout_height=\"wrap_content\"\n"
                    + "                android:textSize=\"16sp\"\n"
                    + "                android:layout_margin=\"10dp\"\n"
                    + "                android:text=\"open fragment activity\"\n"
                    + "                binding=\"{ Click @- OpenFragmentActivity }\"/>\n"
                    + "\n"
                    + "    </LinearLayout>\n"
                    + "\n"
                    + "</ScrollView>");

    private static final String NO_WARNINGS = "No warnings.";

    @Override
    protected Detector getDetector() {
        return new MissingViewIdDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return ImmutableList.of(MissingViewIdDetector.ISSUE);
    }

    public void testMissingButtonId() throws Exception {
        String lintOutput = lintFiles(LAYOUT_BUTTON);
        assertNotSame(NO_WARNINGS, lintOutput);
        assertTrue(lintOutput.contains("2 errors, 0 warnings"));
    }

    public void testMissingLinearLayoutId() throws Exception {
        String lintOutput = lintFiles(LAYOUT_BUTTON);
        assertNotSame(NO_WARNINGS, lintOutput);
        assertTrue(lintOutput.contains("2 errors, 0 warnings"));
    }

}
