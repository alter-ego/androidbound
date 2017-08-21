package solutions.alterego.androidbound.utils;

public class StringUtils {

    public static String unescape(String value) {
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

}
