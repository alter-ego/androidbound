package com.alterego.androidbound.parsers;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.binds.BindingMode;
import com.alterego.androidbound.binds.BindingSpecification;
import com.alterego.androidbound.helpers.Matcher;
import com.alterego.androidbound.helpers.Pattern;
import com.alterego.androidbound.interfaces.IParser;
import com.alterego.androidbound.interfaces.IResourceProvider;
import com.alterego.androidbound.interfaces.IValueConverterProvider;

import java.util.Map;

public class BindingSpecificationParser implements IParser<BindingSpecification> {

    private IAndroidLogger logger = NullAndroidLogger.instance;

    private IValueConverterProvider converterProvider;

    private IResourceProvider resourceProvider;

    public BindingSpecificationParser(IValueConverterProvider converterProvider, IResourceProvider resourceProvider, IAndroidLogger logger) {
        this.converterProvider = converterProvider;
        this.resourceProvider = resourceProvider;
        setLogger(logger);
    }

    private final static Pattern pattern = Pattern
            .compile("\\s*(?<target>(?:[a-zA-Z][a-zA-Z0-9]*(?:\\.[a-zA-Z]" +
                    "[a-zA-Z0-9]*)*)*)\\s*(?<mode><=>|<-|->|=>|=|<=)\\s*(" +
                    "?:(?<converter>[a-zA-Z][a-zA-Z0-9]*)\\s*\\()?\\s*(?<" +
                    "path>(?:[a-zA-Z][a-zA-Z0-9]*(?:\\.[a-zA-Z][a-zA-Z0-9" +
                    "]*)*)*)\\s*(?:,\\s*(?:(?<parameterName>[a-zA-Z][a-zA" +
                    "-Z0-9]*)+?|(?:'(?<parameterString>(?:[^'\\\\]|\\\\.)" +
                    "*)')+?))?\\s*\\)?(?:\\s*\\|\\|\\s*(?:(?:(?<fallbackN" +
                    "ame>[a-zA-Z][a-zA-Z0-9]*)+?|(?:'(?<fallbackString>(?" +
                    ":[^'\\\\]|\\\\.)*)')+?))?)?");

    public void setLogger(IAndroidLogger logger) {
        this.logger = logger.getLogger(this);
    }

    public BindingSpecification parse(String content) {
        logger.verbose("Parse content for BindingSpecification");
        Matcher matcher = pattern.matcher(content);
        Map<String, String> groups = matcher.namedGroups();

        BindingSpecification result = new BindingSpecification();
        result.setTarget(groups.get("target"));
        result.setPath(groups.get("path"));
        result.setMode(parseMode(groups.get("mode")));
        result.setValueConverter(converterProvider.find(groups.get("converter")));
        result.setConverterParameter(resolveResource(groups.get("parameterString"), groups.get("parameterName")));
        result.setFallbackValue(resolveResource(groups.get("fallbackString"), groups.get("fallbackName")));
        return result;
    }

    private Object resolveResource(String constValue, String namedValue) {
        if (namedValue != null) {
            return resourceProvider.find(namedValue);
        }
        return unescape(constValue);
    }

    final static String unescape(String value) {
        if (value == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(value.length());
        boolean appendBackSlash = false;

        for (int i = 0; i < value.length(); i++) {
            int code = value.codePointAt(i);
            if (value.codePointAt(i) > Character.MAX_VALUE) {
                i++;
            }

            if (!appendBackSlash) {
                if (code == '\\') {
                    appendBackSlash = true;
                } else {
                    result.append(Character.toChars(code));
                }
                continue;
            }

            if (code == '\\') {
                appendBackSlash = false;
                result.append('\\');
                result.append('\\');
                continue;
            }

            switch (code) {
                case 'r':
                    result.append('\r');
                    break;
                case 'n':
                    result.append('\n');
                    break;
                case 'f':
                    result.append('\f');
                    break;
                case 't':
                    result.append('\t');
                    break;
                default:
                    result.append(Character.toChars(code));
                    break;
            }
            appendBackSlash = false;
        }
        if (appendBackSlash) {
            result.append('\\');
        }
        return result.toString();
    }

    private BindingMode parseMode(String value) {
        if (value.equals("=")) {
            return BindingMode.Default;
        }
        if (value.equals("->")) {
            return BindingMode.OneWayToSourceOneTime;
        }
        if (value.equals("=>")) {
            return BindingMode.OneWayToSource;
        }
        if (value.equals("<-")) {
            return BindingMode.OneWayOneTime;
        }
        if (value.equals("<=")) {
            return BindingMode.OneWay;
        }
        if (value.equals("<=>")) {
            return BindingMode.TwoWay;
        }

        logger.warning("Invalid value '" + value + "' found for BindingMode. Switching to Default.");
        return BindingMode.Default;
    }
}
