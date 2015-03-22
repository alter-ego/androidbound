package solutions.alterego.androidbound.helpers;

import java.util.List;
import java.util.Map;

public interface MatchResult extends java.util.regex.MatchResult {

    public List<String> orderedGroups();

    public Map<String, String> namedGroups();

    public String group(String groupName);

    public int start(String groupName);

    public int end(String groupName);
}
