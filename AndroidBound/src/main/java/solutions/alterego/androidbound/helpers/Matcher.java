package solutions.alterego.androidbound.helpers;

import solutions.alterego.androidbound.zzzztoremove.DefaultValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Matcher implements MatchResult {

    private java.util.regex.Matcher mMatcher;

    private Pattern mParentPattern;

    protected Matcher() {}

    Matcher(Pattern parentPattern, java.util.regex.MatchResult matcher) {
        mParentPattern = parentPattern;
        mMatcher = (java.util.regex.Matcher) matcher;
    }

    Matcher(Pattern parentPattern, CharSequence input) {
        mParentPattern = parentPattern;
        mMatcher = parentPattern.pattern().matcher(input);
    }

    public java.util.regex.Pattern standardPattern() {
        return mMatcher.pattern();
    }

    public Pattern namedPattern() {
        return mParentPattern;
    }

    public Matcher usePattern(Pattern newPattern) {
        if (newPattern == null) {
            throw new IllegalArgumentException("newPattern cannot be null");
        }
        mParentPattern = newPattern;
        mMatcher.usePattern(newPattern.pattern());
        return this;
    }

    public Matcher reset() {
        mMatcher.reset();
        return this;
    }

    public Matcher reset(CharSequence input) {
        mMatcher.reset(input);
        return this;
    }

    public boolean matches() {
        return mMatcher.matches();
    }

    public MatchResult toMatchResult() {
        return new Matcher(this.mParentPattern, mMatcher.toMatchResult());
    }

    public boolean find() {
        return mMatcher.find();
    }

    public boolean find(int start) {
        return mMatcher.find(start);
    }

    public boolean lookingAt() {
        return mMatcher.lookingAt();
    }

    public Matcher appendReplacement(StringBuffer sb, String replacement) {
        mMatcher.appendReplacement(sb, mParentPattern.replaceProperties(replacement));
        return this;
    }

    public StringBuffer appendTail(StringBuffer sb) {
        return mMatcher.appendTail(sb);
    }

    public String group() {
        return mMatcher.group();
    }

    public String group(int group) {
        return mMatcher.group(group);
    }

    public int groupCount() {
        return mMatcher.groupCount();
    }

    public List<String> orderedGroups() {
        int groupCount = groupCount();
        List<String> groups = new ArrayList<String>(groupCount);
        for (int i = 1; i <= groupCount; i++) {
            groups.add(group(i));
        }
        return groups;
    }

    public String group(String groupName) {
        return group(groupIndex(groupName));
    }

    public Map<String, String> namedGroups() {
        Map<String, String> result = new DefaultValueMap<String, String>(null);

        if (mMatcher.find(0)) {
            for (String groupName : mParentPattern.groupNames()) {
                String groupValue = mMatcher.group(groupIndex(groupName));
                result.put(groupName, groupValue);
            }
        }
        return result;
    }

    private int groupIndex(String groupName) {
        int idx = mParentPattern.indexOf(groupName);
        return idx > -1 ? idx + 1 : -1;
    }

    public int start() {
        return mMatcher.start();
    }

    public int start(int group) {
        return mMatcher.start(group);
    }

    public int start(String groupName) {
        return start(groupIndex(groupName));
    }

    public int end() {
        return mMatcher.end();
    }

    public int end(int group) {
        return mMatcher.end(group);
    }

    public int end(String groupName) {
        return end(groupIndex(groupName));
    }

    public Matcher region(int start, int end) {
        mMatcher.region(start, end);
        return this;
    }

    public int regionEnd() {
        return mMatcher.regionEnd();
    }

    public int regionStart() {
        return mMatcher.regionStart();
    }

    public boolean hitEnd() {
        return mMatcher.hitEnd();
    }

    public boolean requireEnd() {
        return mMatcher.requireEnd();
    }

    public boolean hasAnchoringBounds() {
        return mMatcher.hasAnchoringBounds();
    }

    public boolean hasTransparentBounds() {
        return mMatcher.hasTransparentBounds();
    }

    public String replaceAll(String replacement) {
        String r = mParentPattern.replaceProperties(replacement);
        return mMatcher.replaceAll(r);
    }

    public String replaceFirst(String replacement) {
        return mMatcher.replaceFirst(mParentPattern.replaceProperties(replacement));
    }

    public Matcher useAnchoringBounds(boolean b) {
        mMatcher.useAnchoringBounds(b);
        return this;
    }

    public Matcher useTransparentBounds(boolean b) {
        mMatcher.useTransparentBounds(b);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Matcher)) {
            return false;
        }
        Matcher other = (Matcher) obj;
        if (!mParentPattern.equals(other.mParentPattern)) {
            return false;
        }
        return mMatcher.equals(other.mMatcher);
    }

    @Override
    public int hashCode() {
        return mParentPattern.hashCode() ^ mMatcher.hashCode();
    }

    @Override
    public String toString() {
        return mMatcher.toString();
    }
}
