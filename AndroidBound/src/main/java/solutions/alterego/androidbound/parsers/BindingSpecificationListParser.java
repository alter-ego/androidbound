package solutions.alterego.androidbound.parsers;

import java.util.ArrayList;
import java.util.List;

import solutions.alterego.androidbound.binding.data.BindingSpecification;
import solutions.alterego.androidbound.interfaces.ILogger;

public class BindingSpecificationListParser implements IParser<List<BindingSpecification>> {

    private ILogger logger;

    private IParser<BindingSpecification> singleParser;

    public BindingSpecificationListParser(IParser<BindingSpecification> singleParser, ILogger logger) {
        this.singleParser = singleParser;
        setLogger(logger);
    }

    public void setLogger(ILogger logger) {
        this.logger = logger.getLogger(this);
    }

    public List<BindingSpecification> parse(String content) {
        logger.verbose("Parse content for BindingSpecificationList, content = " + content);
        List<BindingSpecification> result = new ArrayList<BindingSpecification>();

        if (content != null) {
            StringBuilder buffer = new StringBuilder();
            boolean inGroup = false;
            boolean inQuote = false;
            boolean inEscape = false;
            for (int i = 0; i < content.length(); i++) {
                char code = content.charAt(i);
                if (inEscape) {
                    buffer.append(code);
                    inEscape = false;
                    continue;
                }
                switch (code) {
                    case '{':
                        if (!inQuote && !inGroup) {
                            inGroup = true;
                        }
                        break;
                    case '}':
                        if (!inQuote && inGroup) {
                            inGroup = false;
                            buffer.append(code);
                            logger.debug("Found possible binding specification. Sending to single parser, spec = " + buffer.toString());
                            result.add(singleParser.parse(buffer.toString()));
                            buffer = new StringBuilder();
                        }
                        break;
                    case '\'':
                        inQuote = !inQuote;
                        break;
                    case '\\':
                        inEscape = true;
                        break;
                }
                if (inGroup) {
                    buffer.append(code);
                }
            }
        }
        return result;
    }

}
