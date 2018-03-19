package solutions.alterego.androidbound.parsers;

import java.util.Map;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.binding.data.BindingMode;
import solutions.alterego.androidbound.binding.data.BindingSpecification;
import solutions.alterego.androidbound.converters.interfaces.IValueConverterProvider;
import solutions.alterego.androidbound.helpers.Matcher;
import solutions.alterego.androidbound.helpers.Pattern;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.resources.interfaces.IResourceProvider;
import solutions.alterego.androidbound.utils.StringUtils;

public class BindingSpecificationParser implements IParser<BindingSpecification> {

    private final static Pattern PATTERN = Pattern
            .compile("\\s*(?<target>(?:[a-zA-Z][a-zA-Z0-9]*(?:\\.[a-zA-Z][a-zA-Z0-9]*)*)*)" +
                    "\\s*(?<mode>@=@|@-|-@|=@|=|@=|@\\+@|(@\\+)|(\\+@)|\\!@)" +
                    "\\s*(?:(?<converter>[a-zA-Z][a-zA-Z0-9]*)\\" +
                    "s*\\()?\\s*(?<source>(?:[a-zA-Z][a-zA-Z0-9]*(?:\\.[a-zA-Z][a-zA-Z0-9]*)*)*)" +
                    "\\s*(?:,\\s*(?:(?<parameterName>[a-zA-Z][a-zA-Z0-9]*)" +
                    "+?|(?:'(?<parameterString>(?:[^'\\\\]|\\\\.)*)')+?))?\\s*\\)?" +
                    "(?:\\s*\\|\\|\\s*(?:(?:(?<fallbackName>[a-zA-Z][a-zA-Z0-9]*)" +
                    "+?|(?:'(?<fallbackString>(?:[^'\\\\]|\\\\.)*)')+?))?)?");

    private ILogger mLogger = NullLogger.instance;

    protected IValueConverterProvider mValueConverterProvider;

    private IResourceProvider mResourceProvider;

    private final boolean mDebugMode;

    public BindingSpecificationParser(IValueConverterProvider converterProvider, IResourceProvider resourceProvider, ILogger logger, boolean debugMode) {
        mValueConverterProvider = converterProvider;
        mResourceProvider = resourceProvider;
        mDebugMode = debugMode;
        setLogger(logger);
    }

    public void setLogger(ILogger logger) {
        mLogger = logger.getLogger(this);
    }

    public BindingSpecification parse(String content) {
        mLogger.verbose("Parse content for BindingSpecification");
        Matcher matcher = PATTERN.matcher(content);
        Map<String, String> groups = matcher.namedGroups();

        BindingSpecification result = new BindingSpecification();
        result.setTarget(groups.get("target"));
        result.setSource(groups.get("source"));
        result.setMode(parseMode(groups.get("mode")));
        result.setValueConverter(mValueConverterProvider.findConverter(groups.get("converter")));
        result.setConverterParameter(resolveResource(groups.get("parameterString"), groups.get("parameterName")));
        result.setFallbackValue(resolveResource(groups.get("fallbackString"), groups.get("fallbackName")));
        return result;
    }

    private Object resolveResource(String constValue, String namedValue) {
        if (namedValue != null) {
            return mResourceProvider.find(namedValue);
        }
        return StringUtils.unescape(constValue);
    }

    private BindingMode parseMode(String value) {
        if (value == null || value.equals("=")) {
            return BindingMode.Default;
        }
        if (value.equals("-@")) {
            return BindingMode.OneWayToSourceOneTime;
        }
        if (value.equals("=@")) {
            return BindingMode.OneWayToSource;
        }
        if (value.equals("@-")) {
            return BindingMode.OneWayOneTime;
        }
        if (value.equals("@=")) {
            return BindingMode.OneWay;
        }
        if (value.equals("@=@")) {
            return BindingMode.TwoWay;
        }
        if (value.equals("@+")) {
            return BindingMode.Accumulate;
        }
        if (value.equals("+@")) {
            return BindingMode.AccumulateToSource;
        }
        if (value.equals("@+@")) {
            return BindingMode.AccumulateTwoWay;
        }
        if (value.equals("!@")) {
            return BindingMode.RemoveSource;
        }

        String msg = "Invalid value '" + value + "' found for BindingMode. Switching to Default.";
        mLogger.warning(msg);
        if (mDebugMode) {
            throw new RuntimeException(msg);
        }
        return BindingMode.Default;
    }
}
