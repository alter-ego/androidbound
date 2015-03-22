package solutions.alterego.androidbound.parsers;

import java.util.ArrayList;
import java.util.List;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import solutions.alterego.androidbound.binds.BindingSpecification;
import solutions.alterego.androidbound.interfaces.IParser;

public class BindingSpecificationListParser implements IParser<List<BindingSpecification>> {
	private IAndroidLogger logger;
	private IParser<BindingSpecification> singleParser;

	public BindingSpecificationListParser(IParser<BindingSpecification> singleParser, IAndroidLogger logger) {
		this.singleParser = singleParser;
		setLogger(logger);
	}
	
	public void setLogger(IAndroidLogger logger) {
		this.logger = logger.getLogger(this);
	}

	public List<BindingSpecification> parse(String content) {
		logger.verbose("Parse content for BindingSpecificationList");
		List<BindingSpecification> result = new ArrayList<BindingSpecification>();

		if (content != null) {
			StringBuffer buffer = new StringBuffer();
			boolean inGroup = false;
			boolean inQuote = false;
			boolean inEscape = false;
			for (int i = 0; i < content.length(); i++) {
				char code = content.charAt(i);
				if(inEscape) {
					buffer.append(code);
					inEscape = false;
					continue;
				}
				switch (code) {
					case '{':
						if(!inQuote)
							inGroup = true;
						break;
					case '}':
						if(!inQuote) {
							inGroup = false;
							logger.debug("Found possible binding specification. Sending to single parser");
							result.add(singleParser.parse(buffer.toString()));
							buffer = new StringBuffer();
						}
						break;
					case '\'':
						inQuote = !inQuote;
						break;
					case '\\':
						inEscape = true;
						break;
				}
				if(inGroup)
					buffer.append(code);
			}
		}
		return result;
	}
}
