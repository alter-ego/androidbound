package solutions.alterego.androidbound.lint;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

public final class LintRegistryTest {

    @Test
    public void issues() throws Exception {
        Assertions.assertThat(new LintRegistry().getIssues()).contains(MissingViewIdDetector.ISSUE);
    }
}
