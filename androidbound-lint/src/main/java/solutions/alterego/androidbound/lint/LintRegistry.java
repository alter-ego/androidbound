package solutions.alterego.androidbound.lint;

import com.google.common.collect.ImmutableList;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import java.util.List;

/**
 * Contains references to all custom lint checks for androidbound.
 */
public class LintRegistry extends IssueRegistry {

    @Override
    public List<Issue> getIssues() {
        return ImmutableList.of(MissingViewIdDetector.ISSUE);
    }
}
