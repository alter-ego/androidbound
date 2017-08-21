package solutions.alterego.androidbound.utils;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class StringUtilsTest {

    //TODO add test for fallbackValue

    private final static String escaped_string1
            = "This string\\nis not \\\"escaped\\\"\\\\'escaped'";

    private final static String unescaped_string1
            = "This string\n"
            + "is not \"escaped\"\\'escaped'";

    private final static String escaped_string2
            = "escaping \\\\";

    private final static String unescaped_string2
            = "escaping \\";

    private final static String escaped_string3
            = "This string\\nis not \\\"escaped\\\" or \\\'escaped'";

    private final static String unescaped_string3
            = "This string\n"
            + "is not \"escaped\" or \'escaped'";

    public StringUtilsTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void escaping_complex_string() throws Exception {
        Assertions.assertThat(StringUtils.unescape(escaped_string1)).isEqualTo(unescaped_string1);
    }

    @Test
    public void escaping_backslash() throws Exception {
        Assertions.assertThat(StringUtils.unescape(escaped_string2)).isEqualTo(unescaped_string2);
    }

    @Test
    public void escaping_simpler_string() throws Exception {
        Assertions.assertThat(StringUtils.unescape(escaped_string3)).isEqualTo(unescaped_string3);
    }

}