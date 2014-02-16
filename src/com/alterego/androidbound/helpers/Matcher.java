package com.alterego.androidbound.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alterego.androidbound.zzzztoremove.DefaultValueMap;

public class Matcher implements MatchResult {

    private java.util.regex.Matcher matcher;
    private Pattern parentPattern;

    protected Matcher() {}

    Matcher(Pattern parentPattern, java.util.regex.MatchResult matcher) {
        this.parentPattern = parentPattern;
        this.matcher = (java.util.regex.Matcher) matcher;
    }

    Matcher(Pattern parentPattern, CharSequence input) {
        this.parentPattern = parentPattern;
        this.matcher = parentPattern.pattern().matcher(input);
    }

    public java.util.regex.Pattern standardPattern() {
        return matcher.pattern();
    }

    public Pattern namedPattern() {
        return parentPattern;
    }

    public Matcher usePattern(Pattern newPattern) {
        if (newPattern == null)
            throw new IllegalArgumentException("newPattern cannot be null");
        this.parentPattern = newPattern;
        matcher.usePattern(newPattern.pattern());
        return this;
    }

    public Matcher reset() {
        matcher.reset();
        return this;
    }

    public Matcher reset(CharSequence input) {
        matcher.reset(input);
        return this;
    }

    public boolean matches() {
        return matcher.matches();
    }

    public MatchResult toMatchResult() {
        return new Matcher(this.parentPattern, matcher.toMatchResult());
    }

    public boolean find() {
        return matcher.find();
    }

    public boolean find(int start) {
        return matcher.find(start);
    }

    public boolean lookingAt() {
        return matcher.lookingAt();
    }

    public Matcher appendReplacement(StringBuffer sb, String replacement) {
        matcher.appendReplacement(sb, parentPattern.replaceProperties(replacement));
        return this;
    }

    public StringBuffer appendTail(StringBuffer sb) {
        return matcher.appendTail(sb);
    }

    public String group() {
        return matcher.group();
    }

    public String group(int group) {
        return matcher.group(group);
    }

    public int groupCount() {
        return matcher.groupCount();
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

        if (matcher.find(0)) {
            for (String groupName : parentPattern.groupNames()) {
                String groupValue = matcher.group(groupIndex(groupName));
                result.put(groupName, groupValue);
            }
        }
        return result;
    }

    private int groupIndex(String groupName) {
        int idx = parentPattern.indexOf(groupName);
        return idx > -1 ? idx + 1 : -1;
    }

    public int start() {
        return matcher.start();
    }

    public int start(int group) {
        return matcher.start(group);
    }

    public int start(String groupName) {
        return start(groupIndex(groupName));
    }

    public int end() {
        return matcher.end();
    }

    public int end(int group) {
        return matcher.end(group);
    }

    public int end(String groupName) {
        return end(groupIndex(groupName));
    }

    public Matcher region(int start, int end) {
        matcher.region(start, end);
        return this;
    }

    public int regionEnd() {
        return matcher.regionEnd();
    }

    public int regionStart() {
        return matcher.regionStart();
    }

    public boolean hitEnd() {
        return matcher.hitEnd();
    }

    public boolean requireEnd() {
        return matcher.requireEnd();
    }

    public boolean hasAnchoringBounds() {
        return matcher.hasAnchoringBounds();
    }

    public boolean hasTransparentBounds() {
        return matcher.hasTransparentBounds();
    }

    public String replaceAll(String replacement) {
        String r = parentPattern.replaceProperties(replacement);
        return matcher.replaceAll(r);
    }

    public String replaceFirst(String replacement) {
        return matcher.replaceFirst(parentPattern.replaceProperties(replacement));
    }

    public Matcher useAnchoringBounds(boolean b) {
        matcher.useAnchoringBounds(b);
        return this;
    }

    public Matcher useTransparentBounds(boolean b) {
        matcher.useTransparentBounds(b);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null) 
            return false;
        if (!(obj instanceof Matcher)) 
            return false;
        Matcher other = (Matcher)obj;
        if (!parentPattern.equals(other.parentPattern)) 
            return false;
        return matcher.equals(other.matcher);
    }

    @Override
    public int hashCode() {
        return parentPattern.hashCode() ^ matcher.hashCode();
    }

    @Override
    public String toString() {
        return matcher.toString();
    }
}
