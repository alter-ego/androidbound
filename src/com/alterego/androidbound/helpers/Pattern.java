package com.alterego.androidbound.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class Pattern {
	private static final java.util.regex.Pattern NAMED_GROUP_PATTERN = java.util.regex.Pattern
			.compile("\\(\\?<(\\w+)>");
	private static final java.util.regex.Pattern BACKREF_NAMED_GROUP_PATTERN = java.util.regex.Pattern
			.compile("\\\\k<(\\w+)>");
	private static final java.util.regex.Pattern PROPERTY_PATTERN = java.util.regex.Pattern.compile("\\$\\{(\\w+)\\}");
	private static final int INDEX_GROUP_NAME = 1;

	private java.util.regex.Pattern pattern;
	private String namedPattern;
	private List<String> groupNames;
	private Map<String, List<GroupInfo>> groupInfo;

	protected Pattern() {
	}

	protected Pattern(String regex, int flags) {
		namedPattern = regex;
		groupInfo = extractGroupInfo(regex);
		pattern = buildStandardPattern(regex, flags);
	}

	public static Pattern compile(String regex) {
		return new Pattern(regex, 0);
	}

	public static Pattern compile(String regex, int flags) {
		return new Pattern(regex, flags);
	}

	public int indexOf(String groupName) {
		return indexOf(groupName, 0);
	}

	public int indexOf(String groupName, int index) {
		int idx = -1;
		if (groupInfo.containsKey(groupName)) {
			List<GroupInfo> list = groupInfo.get(groupName);
			idx = list.get(index).getIndex();
		}
		return idx;
	}

	public int flags() {
		return pattern.flags();
	}

	public Matcher matcher(CharSequence input) {
		return new Matcher(this, input);
	}

	public java.util.regex.Pattern pattern() {
		return pattern;
	}

	public String standardPattern() {
		return pattern.pattern();
	}

	public String namedPattern() {
		return namedPattern;
	}

	public List<String> groupNames() {
		if (groupNames == null)
			groupNames = new ArrayList<String>(groupInfo.keySet());
		return groupNames;
	}

	public Map<String, List<GroupInfo>> groupInfo() {
		return groupInfo;
	}

	public String replaceProperties(String replacementPattern) {
		return replaceGroupNameWithIndex(new StringBuilder(replacementPattern), PROPERTY_PATTERN, "$").toString();
	}

	public String[] split(CharSequence input, int limit) {
		return pattern.split(input, limit);
	}

	public String[] split(CharSequence input) {
		return pattern.split(input);
	}

	public String toString() {
		return namedPattern;
	}

	static private boolean isEscapedChar(String s, int pos) {
		int numSlashes = 0;
		while (pos > 0 && (s.charAt(pos - 1) == '\\')) {
			pos--;
			numSlashes++;
		}
		return numSlashes % 2 != 0;
	}

	static private boolean isNoncapturingParen(String s, int pos) {
		boolean isLookbehind = false;
		String pre = s.substring(pos, pos + 4);
		isLookbehind = pre.equals("(?<=") || pre.equals("(?<!");
		return s.charAt(pos + 1) == '?' && (isLookbehind || s.charAt(pos + 2) != '<');
	}

	static private int countOpenParens(String s, int pos) {
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\(");
		java.util.regex.Matcher m = p.matcher(s.subSequence(0, pos));

		int numParens = 0;

		while (m.find()) {
			if (isEscapedChar(s, m.start()))
				continue;
			if (!isNoncapturingParen(s, m.start()))
				numParens++;
		}
		return numParens;
	}

	static public Map<String, List<GroupInfo>> extractGroupInfo(String namedPattern) {
		Map<String, List<GroupInfo>> groupInfo = new LinkedHashMap<String, List<GroupInfo>>();
		java.util.regex.Matcher matcher = NAMED_GROUP_PATTERN.matcher(namedPattern);
		while (matcher.find()) {
			int pos = matcher.start();
			if (isEscapedChar(namedPattern, pos))
				continue;

			String name = matcher.group(INDEX_GROUP_NAME);
			int groupIndex = countOpenParens(namedPattern, pos);

			List<GroupInfo> list;
			if (groupInfo.containsKey(name)) {
				list = groupInfo.get(name);
			} else {
				list = new ArrayList<GroupInfo>();
			}
			list.add(new GroupInfo(groupIndex, pos));
			groupInfo.put(name, list);
		}
		return groupInfo;
	}

	static private StringBuilder replace(StringBuilder input, java.util.regex.Pattern pattern, String replacement) {
		java.util.regex.Matcher m = pattern.matcher(input);
		while (m.find()) {
			if (isEscapedChar(input.toString(), m.start()))
				continue;
			input.replace(m.start(), m.end(), replacement);
			m.reset(input);
		}
		return input;
	}

	private StringBuilder replaceGroupNameWithIndex(StringBuilder input, java.util.regex.Pattern pattern, String prefix) {
		java.util.regex.Matcher m = pattern.matcher(input);
		while (m.find()) {
			if (isEscapedChar(input.toString(), m.start()))
				continue;
			int index = indexOf(m.group(INDEX_GROUP_NAME));
			if (index >= 0)
				index++;
			else
				throw new PatternSyntaxException("unknown group name", input.toString(), m.start(INDEX_GROUP_NAME));

			input.replace(m.start(), m.end(), prefix + index);
			m.reset(input);
		}
		return input;
	}

	private java.util.regex.Pattern buildStandardPattern(String namedPattern, Integer flags) {
		StringBuilder s = new StringBuilder(namedPattern);
		s = replace(s, NAMED_GROUP_PATTERN, "(");
		s = replaceGroupNameWithIndex(s, BACKREF_NAMED_GROUP_PATTERN, "\\");
		return java.util.regex.Pattern.compile(s.toString(), flags);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pattern))
			return false;
		Pattern other = (Pattern) obj;
		return namedPattern.equals(other.namedPattern) && pattern.flags() == other.pattern.flags();
	}

	@Override
	public int hashCode() {
		return namedPattern.hashCode() ^ pattern.flags();
	}
}