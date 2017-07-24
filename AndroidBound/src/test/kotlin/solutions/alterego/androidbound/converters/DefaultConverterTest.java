package solutions.alterego.androidbound.converters;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;

import java.math.BigDecimal;
import java.util.Locale;

public class DefaultConverterTest {

    @Test
    public void converterDoesntUseParameters() {
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, "String", null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, 1, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, 1.0d, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, true, null)).isEqualTo(false);
    }

    @SuppressLint("NewApi")
    @Test
    public void converterDoesntUseLocale() {
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, null, Locale.UK)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, null, Locale.US)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, null, Locale.CHINA)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(0, Boolean.class, null, Locale.forLanguageTag("ar-001"))).isEqualTo(false);
    }

    @Test
    public void convertsIntegerToBoolean() {
        int falseInt = 0;
        int trueInt = 1;
        int anyPositiveInt = 55;
        int anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(falseInt, Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(trueInt, Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, Boolean.class, null, null)).isEqualTo(true);

        Assertions.assertThat(DefaultConverter.instance.convertBack(falseInt, Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convertBack(trueInt, Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyPositiveInt, Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyNegativeInt, Boolean.class, null, null)).isEqualTo(true);

        Assertions.assertThat(DefaultConverter.instance.convert(falseInt, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.integerToBoolean(falseInt));
        Assertions.assertThat(DefaultConverter.instance.convert(trueInt, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.integerToBoolean(trueInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.integerToBoolean(anyPositiveInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.integerToBoolean(anyNegativeInt));
    }

    @Test
    public void convertsBooleanToInteger() {
        boolean trueBool = true;
        boolean falseBool = false;

        Assertions.assertThat(DefaultConverter.instance.convert(falseBool, Integer.class, null, null)).isEqualTo(0);
        Assertions.assertThat(DefaultConverter.instance.convert(trueBool, Integer.class, null, null)).isEqualTo(1);
        Assertions.assertThat(DefaultConverter.instance.convert(false, Integer.class, null, null)).isEqualTo(0);
        Assertions.assertThat(DefaultConverter.instance.convert(true, Integer.class, null, null)).isEqualTo(1);

        Assertions.assertThat(DefaultConverter.instance.convertBack(falseBool, Integer.class, null, null)).isEqualTo(0);
        Assertions.assertThat(DefaultConverter.instance.convertBack(trueBool, Integer.class, null, null)).isEqualTo(1);
        Assertions.assertThat(DefaultConverter.instance.convertBack(false, Integer.class, null, null)).isEqualTo(0);
        Assertions.assertThat(DefaultConverter.instance.convertBack(true, Integer.class, null, null)).isEqualTo(1);

        Assertions.assertThat(DefaultConverter.instance.convert(falseBool, Integer.class, null, null))
                .isEqualTo(DefaultConverter.booleanToInteger(false));
        Assertions.assertThat(DefaultConverter.instance.convert(trueBool, Integer.class, null, null))
                .isEqualTo(DefaultConverter.booleanToInteger(true));
        Assertions.assertThat(DefaultConverter.instance.convert(false, Integer.class, null, null))
                .isEqualTo(DefaultConverter.booleanToInteger(false));
        Assertions.assertThat(DefaultConverter.instance.convert(true, Integer.class, null, null))
                .isEqualTo(DefaultConverter.booleanToInteger(true));
    }

    @Test
    public void convertsDoubleToBoolean() {
        double falseInt = 0;
        double trueInt = 1;
        double anyPositiveInt = 55;
        double anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(falseInt, Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert(trueInt, Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, Boolean.class, null, null)).isEqualTo(true);

        Assertions.assertThat(DefaultConverter.instance.convertBack(falseInt, Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convertBack(trueInt, Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyPositiveInt, Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyNegativeInt, Boolean.class, null, null)).isEqualTo(true);

        Assertions.assertThat(DefaultConverter.instance.convert(falseInt, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.doubleToBoolean(falseInt));
        Assertions.assertThat(DefaultConverter.instance.convert(trueInt, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.doubleToBoolean(trueInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.doubleToBoolean(anyPositiveInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.doubleToBoolean(anyNegativeInt));
    }

    @Test
    public void convertsBooleanToDouble() {
        boolean trueBool = true;
        boolean falseBool = false;

        Assertions.assertThat(DefaultConverter.instance.convert(falseBool, Double.class, null, null)).isEqualTo(0.0d);
        Assertions.assertThat(DefaultConverter.instance.convert(trueBool, Double.class, null, null)).isEqualTo(1.0d);
        Assertions.assertThat(DefaultConverter.instance.convert(false, Double.class, null, null)).isEqualTo(0.0d);
        Assertions.assertThat(DefaultConverter.instance.convert(true, Double.class, null, null)).isEqualTo(1.0d);

        Assertions.assertThat(DefaultConverter.instance.convertBack(falseBool, Double.class, null, null)).isEqualTo(0.0d);
        Assertions.assertThat(DefaultConverter.instance.convertBack(trueBool, Double.class, null, null)).isEqualTo(1.0d);
        Assertions.assertThat(DefaultConverter.instance.convertBack(false, Double.class, null, null)).isEqualTo(0.0d);
        Assertions.assertThat(DefaultConverter.instance.convertBack(true, Double.class, null, null)).isEqualTo(1.0d);

        Assertions.assertThat(DefaultConverter.instance.convert(falseBool, Double.class, null, null))
                .isEqualTo(DefaultConverter.booleanToDouble(false));
        Assertions.assertThat(DefaultConverter.instance.convert(trueBool, Double.class, null, null))
                .isEqualTo(DefaultConverter.booleanToDouble(true));
        Assertions.assertThat(DefaultConverter.instance.convert(false, Double.class, null, null))
                .isEqualTo(DefaultConverter.booleanToDouble(false));
        Assertions.assertThat(DefaultConverter.instance.convert(true, Double.class, null, null))
                .isEqualTo(DefaultConverter.booleanToDouble(true));
    }

    @Test
    public void convertsStringToBoolean() {
        Assertions.assertThat(DefaultConverter.instance.convert("false", Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert("FALSE", Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert("FaLse", Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convert("true", Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convert("TRUE", Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convert("TrUe", Boolean.class, null, null)).isEqualTo(true);

        Assertions.assertThat(DefaultConverter.instance.convertBack("false", Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convertBack("FALSE", Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convertBack("FaLse", Boolean.class, null, null)).isEqualTo(false);
        Assertions.assertThat(DefaultConverter.instance.convertBack("true", Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convertBack("TRUE", Boolean.class, null, null)).isEqualTo(true);
        Assertions.assertThat(DefaultConverter.instance.convertBack("TrUe", Boolean.class, null, null)).isEqualTo(true);

        Assertions.assertThat(DefaultConverter.instance.convert("false", Boolean.class, null, null))
                .isEqualTo(DefaultConverter.stringToBoolean("false"));
        Assertions.assertThat(DefaultConverter.instance.convert("FALSE", Boolean.class, null, null))
                .isEqualTo(DefaultConverter.stringToBoolean("false"));
        Assertions.assertThat(DefaultConverter.instance.convert("FaLse", Boolean.class, null, null))
                .isEqualTo(DefaultConverter.stringToBoolean("false"));
        Assertions.assertThat(DefaultConverter.instance.convert("true", Boolean.class, null, null))
                .isEqualTo(DefaultConverter.stringToBoolean("true"));
        Assertions.assertThat(DefaultConverter.instance.convert("TRUE", Boolean.class, null, null))
                .isEqualTo(DefaultConverter.stringToBoolean("true"));
        Assertions.assertThat(DefaultConverter.instance.convert("TrUe", Boolean.class, null, null))
                .isEqualTo(DefaultConverter.stringToBoolean("true"));
    }

    @Test
    public void convertsBooleanToString() {
        boolean trueBool = true;
        boolean falseBool = false;

        Assertions.assertThat(DefaultConverter.instance.convert(falseBool, String.class, null, null)).isEqualTo("false");
        Assertions.assertThat(DefaultConverter.instance.convert(trueBool, String.class, null, null)).isEqualTo("true");
        Assertions.assertThat(DefaultConverter.instance.convert(false, String.class, null, null)).isEqualTo("false");
        Assertions.assertThat(DefaultConverter.instance.convert(true, String.class, null, null)).isEqualTo("true");

        Assertions.assertThat(DefaultConverter.instance.convertBack(falseBool, String.class, null, null)).isEqualTo("false");
        Assertions.assertThat(DefaultConverter.instance.convertBack(trueBool, String.class, null, null)).isEqualTo("true");
        Assertions.assertThat(DefaultConverter.instance.convertBack(false, String.class, null, null)).isEqualTo("false");
        Assertions.assertThat(DefaultConverter.instance.convertBack(true, String.class, null, null)).isEqualTo("true");

        Assertions.assertThat(DefaultConverter.instance.convert(falseBool, String.class, null, null))
                .isEqualTo(DefaultConverter.booleanToString(false));
        Assertions.assertThat(DefaultConverter.instance.convert(trueBool, String.class, null, null))
                .isEqualTo(DefaultConverter.booleanToString(true));
        Assertions.assertThat(DefaultConverter.instance.convert(false, String.class, null, null))
                .isEqualTo(DefaultConverter.booleanToString(false));
        Assertions.assertThat(DefaultConverter.instance.convert(true, String.class, null, null))
                .isEqualTo(DefaultConverter.booleanToString(true));
    }

    @Test
    public void convertsIntegerToString() {
        String zero = "0";
        String one = "1";
        String positive = "55";
        String negative = "-55";
        int zeroInt = 0;
        int oneInt = 1;
        int anyPositiveInt = 55;
        int anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(zeroInt, String.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convert(oneInt, String.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, String.class, null, null)).isEqualTo(positive);
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, String.class, null, null)).isEqualTo(negative);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zeroInt, String.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convertBack(oneInt, String.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyPositiveInt, String.class, null, null)).isEqualTo(positive);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyNegativeInt, String.class, null, null)).isEqualTo(negative);

        Assertions.assertThat(DefaultConverter.instance.convert(zeroInt, String.class, null, null))
                .isEqualTo(DefaultConverter.integerToString(zeroInt));
        Assertions.assertThat(DefaultConverter.instance.convert(oneInt, String.class, null, null))
                .isEqualTo(DefaultConverter.integerToString(oneInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, String.class, null, null))
                .isEqualTo(DefaultConverter.integerToString(anyPositiveInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, String.class, null, null))
                .isEqualTo(DefaultConverter.integerToString(anyNegativeInt));
    }

    @Test
    public void convertsStringToInteger() {
        String zero = "0";
        String one = "1";
        String positive = "55";
        String negative = "-55";
        int zeroInt = 0;
        int oneInt = 1;
        int anyPositiveInt = 55;
        int anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(zero, Integer.class, null, null)).isEqualTo(zeroInt);
        Assertions.assertThat(DefaultConverter.instance.convert(one, Integer.class, null, null)).isEqualTo(oneInt);
        Assertions.assertThat(DefaultConverter.instance.convert(positive, Integer.class, null, null)).isEqualTo(anyPositiveInt);
        Assertions.assertThat(DefaultConverter.instance.convert(negative, Integer.class, null, null)).isEqualTo(anyNegativeInt);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zero, Integer.class, null, null)).isEqualTo(zeroInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(one, Integer.class, null, null)).isEqualTo(oneInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(positive, Integer.class, null, null)).isEqualTo(anyPositiveInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(negative, Integer.class, null, null)).isEqualTo(anyNegativeInt);

        Assertions.assertThat(DefaultConverter.instance.convert(zero, Integer.class, null, null))
                .isEqualTo(DefaultConverter.stringToInteger(zero));
        Assertions.assertThat(DefaultConverter.instance.convert(one, Integer.class, null, null))
                .isEqualTo(DefaultConverter.stringToInteger(one));
        Assertions.assertThat(DefaultConverter.instance.convert(positive, Integer.class, null, null))
                .isEqualTo(DefaultConverter.stringToInteger(positive));
        Assertions.assertThat(DefaultConverter.instance.convert(negative, Integer.class, null, null))
                .isEqualTo(DefaultConverter.stringToInteger(negative));
    }

    @Test
    public void convertsIntegerToCharSequence() {
        CharSequence zero = "0";
        CharSequence one = "1";
        CharSequence positive = "55";
        CharSequence negative = "-55";
        int zeroInt = 0;
        int oneInt = 1;
        int anyPositiveInt = 55;
        int anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(zeroInt, CharSequence.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convert(oneInt, CharSequence.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, CharSequence.class, null, null)).isEqualTo(positive);
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, CharSequence.class, null, null)).isEqualTo(negative);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zeroInt, CharSequence.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convertBack(oneInt, CharSequence.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyPositiveInt, CharSequence.class, null, null)).isEqualTo(positive);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyNegativeInt, CharSequence.class, null, null)).isEqualTo(negative);

        Assertions.assertThat(DefaultConverter.instance.convert(zeroInt, CharSequence.class, null, null))
                .isEqualTo(DefaultConverter.integerToCharSequence(zeroInt));
        Assertions.assertThat(DefaultConverter.instance.convert(oneInt, CharSequence.class, null, null))
                .isEqualTo(DefaultConverter.integerToCharSequence(oneInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, CharSequence.class, null, null))
                .isEqualTo(DefaultConverter.integerToCharSequence(anyPositiveInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, CharSequence.class, null, null))
                .isEqualTo(DefaultConverter.integerToCharSequence(anyNegativeInt));
    }

    @Test
    public void convertsCharSequenceToInteger() {
        CharSequence zero = "0";
        CharSequence one = "1";
        CharSequence positive = "55";
        CharSequence negative = "-55";
        int zeroInt = 0;
        int oneInt = 1;
        int anyPositiveInt = 55;
        int anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(zero, Integer.class, null, null)).isEqualTo(zeroInt);
        Assertions.assertThat(DefaultConverter.instance.convert(one, Integer.class, null, null)).isEqualTo(oneInt);
        Assertions.assertThat(DefaultConverter.instance.convert(positive, Integer.class, null, null)).isEqualTo(anyPositiveInt);
        Assertions.assertThat(DefaultConverter.instance.convert(negative, Integer.class, null, null)).isEqualTo(anyNegativeInt);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zero, Integer.class, null, null)).isEqualTo(zeroInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(one, Integer.class, null, null)).isEqualTo(oneInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(positive, Integer.class, null, null)).isEqualTo(anyPositiveInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(negative, Integer.class, null, null)).isEqualTo(anyNegativeInt);

        Assertions.assertThat(DefaultConverter.instance.convert(zero, Integer.class, null, null))
                .isEqualTo(DefaultConverter.charSequenceToInteger(zero));
        Assertions.assertThat(DefaultConverter.instance.convert(one, Integer.class, null, null))
                .isEqualTo(DefaultConverter.charSequenceToInteger(one));
        Assertions.assertThat(DefaultConverter.instance.convert(positive, Integer.class, null, null))
                .isEqualTo(DefaultConverter.charSequenceToInteger(positive));
        Assertions.assertThat(DefaultConverter.instance.convert(negative, Integer.class, null, null))
                .isEqualTo(DefaultConverter.charSequenceToInteger(negative));
    }

    @Test
    public void convertsIntegerToColorStateList() {
        int zeroInt = 0;
        int oneInt = 1;
        ColorStateList zero = new ColorStateList(new int[][]{
                new int[]{}
        }, new int[]{
                zeroInt
        });
        ColorStateList one = new ColorStateList(new int[][]{
                new int[]{}
        }, new int[]{
                oneInt
        });

        Assertions.assertThat(((ColorStateList) DefaultConverter.instance.convert(zeroInt, ColorStateList.class, null, null)).getDefaultColor())
                .isEqualTo(zero.getDefaultColor());
        Assertions.assertThat(((ColorStateList) DefaultConverter.instance.convert(oneInt, ColorStateList.class, null, null)).getDefaultColor())
                .isEqualTo(one.getDefaultColor());

        Assertions.assertThat(((ColorStateList) DefaultConverter.instance.convertBack(zeroInt, ColorStateList.class, null, null)).getDefaultColor())
                .isEqualTo(zero.getDefaultColor());
        Assertions.assertThat(((ColorStateList) DefaultConverter.instance.convertBack(oneInt, ColorStateList.class, null, null)).getDefaultColor())
                .isEqualTo(one.getDefaultColor());

        Assertions.assertThat(((ColorStateList) DefaultConverter.instance.convert(zeroInt, ColorStateList.class, null, null)).getDefaultColor())
                .isEqualTo(DefaultConverter.integerToColorStateList(zeroInt).getDefaultColor());
        Assertions.assertThat(((ColorStateList) DefaultConverter.instance.convert(oneInt, ColorStateList.class, null, null)).getDefaultColor())
                .isEqualTo(DefaultConverter.integerToColorStateList(oneInt).getDefaultColor());
    }

    @Test
    public void convertsDoubleToBigDecimal() {
        double zero = 0;
        double one = 1;
        double anyPositive = 55.45;
        double anyNegative = -55.45;

        Assertions.assertThat(DefaultConverter.instance.convert(zero, BigDecimal.class, null, null)).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(DefaultConverter.instance.convert(one, BigDecimal.class, null, null)).isEqualTo(BigDecimal.ONE);
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositive, BigDecimal.class, null, null)).isEqualTo(new BigDecimal(anyPositive));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegative, BigDecimal.class, null, null)).isEqualTo(new BigDecimal(anyNegative));

        Assertions.assertThat(DefaultConverter.instance.convertBack(zero, BigDecimal.class, null, null)).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(DefaultConverter.instance.convertBack(one, BigDecimal.class, null, null)).isEqualTo(BigDecimal.ONE);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyPositive, BigDecimal.class, null, null))
                .isEqualTo(new BigDecimal(anyPositive));
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyNegative, BigDecimal.class, null, null))
                .isEqualTo(new BigDecimal(anyNegative));

        Assertions.assertThat(DefaultConverter.instance.convert(zero, BigDecimal.class, null, null))
                .isEqualTo(DefaultConverter.doubleToBigDecimal(zero));
        Assertions.assertThat(DefaultConverter.instance.convert(one, BigDecimal.class, null, null))
                .isEqualTo(DefaultConverter.doubleToBigDecimal(one));
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositive, BigDecimal.class, null, null))
                .isEqualTo(DefaultConverter.doubleToBigDecimal(anyPositive));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegative, BigDecimal.class, null, null))
                .isEqualTo(DefaultConverter.doubleToBigDecimal(anyNegative));
    }

    @Test
    public void convertsBigDecimalToDouble() {
        double zero = 0;
        double one = 1;
        double anyPositive = 55.45;
        double anyNegative = -55.45;

        Assertions.assertThat(DefaultConverter.instance.convert(BigDecimal.ZERO, Double.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convert(BigDecimal.ONE, Double.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convert(new BigDecimal(anyPositive), Double.class, null, null)).isEqualTo(anyPositive);
        Assertions.assertThat(DefaultConverter.instance.convert(new BigDecimal(anyNegative), Double.class, null, null)).isEqualTo(anyNegative);

        Assertions.assertThat(DefaultConverter.instance.convertBack(BigDecimal.ZERO, Double.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convertBack(BigDecimal.ONE, Double.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convertBack(new BigDecimal(anyPositive), Double.class, null, null)).isEqualTo(anyPositive);
        Assertions.assertThat(DefaultConverter.instance.convertBack(new BigDecimal(anyNegative), Double.class, null, null)).isEqualTo(anyNegative);

        Assertions.assertThat(DefaultConverter.instance.convert(BigDecimal.ZERO, Double.class, null, null))
                .isEqualTo(DefaultConverter.bigDecimalToDouble(BigDecimal.ZERO));
        Assertions.assertThat(DefaultConverter.instance.convert(BigDecimal.ONE, Double.class, null, null))
                .isEqualTo(DefaultConverter.bigDecimalToDouble(BigDecimal.ONE));
        Assertions.assertThat(DefaultConverter.instance.convert(new BigDecimal(anyPositive), Double.class, null, null))
                .isEqualTo(DefaultConverter.bigDecimalToDouble(new BigDecimal(anyPositive)));
        Assertions.assertThat(DefaultConverter.instance.convert(new BigDecimal(anyNegative), Double.class, null, null))
                .isEqualTo(DefaultConverter.bigDecimalToDouble(new BigDecimal(anyNegative)));
    }

    @Test
    public void convertsDoubleToString() {
        String zeroString = "0.0";
        String oneString = "1.0";
        String positiveString = "55.45";
        String negativeString = "-55.45";
        double zero = 0;
        double one = 1;
        double anyPositive = 55.45;
        double anyNegative = -55.45;

        Assertions.assertThat(DefaultConverter.instance.convert(zero, String.class, null, null)).isEqualTo(zeroString);
        Assertions.assertThat(DefaultConverter.instance.convert(one, String.class, null, null)).isEqualTo(oneString);
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositive, String.class, null, null)).isEqualTo(positiveString);
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegative, String.class, null, null)).isEqualTo(negativeString);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zero, String.class, null, null)).isEqualTo(zeroString);
        Assertions.assertThat(DefaultConverter.instance.convertBack(one, String.class, null, null)).isEqualTo(oneString);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyPositive, String.class, null, null)).isEqualTo(positiveString);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyNegative, String.class, null, null)).isEqualTo(negativeString);

        Assertions.assertThat(DefaultConverter.instance.convert(zero, String.class, null, null))
                .isEqualTo(DefaultConverter.doubleToString(zero));
        Assertions.assertThat(DefaultConverter.instance.convert(one, String.class, null, null))
                .isEqualTo(DefaultConverter.doubleToString(one));
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositive, String.class, null, null))
                .isEqualTo(DefaultConverter.doubleToString(anyPositive));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegative, String.class, null, null))
                .isEqualTo(DefaultConverter.doubleToString(anyNegative));
    }

    @Test
    public void convertsStringToDouble() {
        String zeroString = "0.0";
        String oneString = "1.0";
        String positiveString = "55.45";
        String negativeString = "-55.45";
        double zero = 0;
        double one = 1;
        double anyPositive = 55.45;
        double anyNegative = -55.45;

        Assertions.assertThat(DefaultConverter.instance.convert(zeroString, Double.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convert(oneString, Double.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convert(positiveString, Double.class, null, null)).isEqualTo(anyPositive);
        Assertions.assertThat(DefaultConverter.instance.convert(negativeString, Double.class, null, null)).isEqualTo(anyNegative);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zeroString, Double.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convertBack(oneString, Double.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convertBack(positiveString, Double.class, null, null)).isEqualTo(anyPositive);
        Assertions.assertThat(DefaultConverter.instance.convertBack(negativeString, Double.class, null, null)).isEqualTo(anyNegative);

        Assertions.assertThat(DefaultConverter.instance.convert(zeroString, Double.class, null, null))
                .isEqualTo(DefaultConverter.stringToDouble(zeroString));
        Assertions.assertThat(DefaultConverter.instance.convert(oneString, Double.class, null, null))
                .isEqualTo(DefaultConverter.stringToDouble(oneString));
        Assertions.assertThat(DefaultConverter.instance.convert(positiveString, Double.class, null, null))
                .isEqualTo(DefaultConverter.stringToDouble(positiveString));
        Assertions.assertThat(DefaultConverter.instance.convert(negativeString, Double.class, null, null))
                .isEqualTo(DefaultConverter.stringToDouble(negativeString));
    }

    @Test
    public void convertsLongToString() {
        String zero = "0";
        String one = "1";
        String positive = "55";
        String negative = "-55";
        long zeroInt = 0;
        long oneInt = 1;
        long anyPositiveInt = 55;
        long anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(zeroInt, String.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convert(oneInt, String.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, String.class, null, null)).isEqualTo(positive);
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, String.class, null, null)).isEqualTo(negative);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zeroInt, String.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convertBack(oneInt, String.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyPositiveInt, String.class, null, null)).isEqualTo(positive);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyNegativeInt, String.class, null, null)).isEqualTo(negative);

        Assertions.assertThat(DefaultConverter.instance.convert(zeroInt, String.class, null, null))
                .isEqualTo(DefaultConverter.longToString(zeroInt));
        Assertions.assertThat(DefaultConverter.instance.convert(oneInt, String.class, null, null))
                .isEqualTo(DefaultConverter.longToString(oneInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, String.class, null, null))
                .isEqualTo(DefaultConverter.longToString(anyPositiveInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, String.class, null, null))
                .isEqualTo(DefaultConverter.longToString(anyNegativeInt));
    }

    @Test
    public void convertsStringToLong() {
        String zero = "0";
        String one = "1";
        String positive = "55";
        String negative = "-55";
        long zeroInt = 0;
        long oneInt = 1;
        long anyPositiveInt = 55;
        long anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(zero, Long.class, null, null)).isEqualTo(zeroInt);
        Assertions.assertThat(DefaultConverter.instance.convert(one, Long.class, null, null)).isEqualTo(oneInt);
        Assertions.assertThat(DefaultConverter.instance.convert(positive, Long.class, null, null)).isEqualTo(anyPositiveInt);
        Assertions.assertThat(DefaultConverter.instance.convert(negative, Long.class, null, null)).isEqualTo(anyNegativeInt);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zero, Long.class, null, null)).isEqualTo(zeroInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(one, Long.class, null, null)).isEqualTo(oneInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(positive, Long.class, null, null)).isEqualTo(anyPositiveInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(negative, Long.class, null, null)).isEqualTo(anyNegativeInt);

        Assertions.assertThat(DefaultConverter.instance.convert(zero, Long.class, null, null))
                .isEqualTo(DefaultConverter.stringToLong(zero));
        Assertions.assertThat(DefaultConverter.instance.convert(one, Long.class, null, null))
                .isEqualTo(DefaultConverter.stringToLong(one));
        Assertions.assertThat(DefaultConverter.instance.convert(positive, Long.class, null, null))
                .isEqualTo(DefaultConverter.stringToLong(positive));
        Assertions.assertThat(DefaultConverter.instance.convert(negative, Long.class, null, null))
                .isEqualTo(DefaultConverter.stringToLong(negative));
    }

    @Test
    public void convertsLongToCharSequence() {
        CharSequence zero = "0";
        CharSequence one = "1";
        CharSequence positive = "55";
        CharSequence negative = "-55";
        long zeroInt = 0;
        long oneInt = 1;
        long anyPositiveInt = 55;
        long anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(zeroInt, CharSequence.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convert(oneInt, CharSequence.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, CharSequence.class, null, null)).isEqualTo(positive);
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, CharSequence.class, null, null)).isEqualTo(negative);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zeroInt, CharSequence.class, null, null)).isEqualTo(zero);
        Assertions.assertThat(DefaultConverter.instance.convertBack(oneInt, CharSequence.class, null, null)).isEqualTo(one);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyPositiveInt, CharSequence.class, null, null)).isEqualTo(positive);
        Assertions.assertThat(DefaultConverter.instance.convertBack(anyNegativeInt, CharSequence.class, null, null)).isEqualTo(negative);

        Assertions.assertThat(DefaultConverter.instance.convert(zeroInt, CharSequence.class, null, null))
                .isEqualTo(DefaultConverter.longToCharSequence(zeroInt));
        Assertions.assertThat(DefaultConverter.instance.convert(oneInt, CharSequence.class, null, null))
                .isEqualTo(DefaultConverter.longToCharSequence(oneInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyPositiveInt, CharSequence.class, null, null))
                .isEqualTo(DefaultConverter.longToCharSequence(anyPositiveInt));
        Assertions.assertThat(DefaultConverter.instance.convert(anyNegativeInt, CharSequence.class, null, null))
                .isEqualTo(DefaultConverter.longToCharSequence(anyNegativeInt));
    }

    @Test
    public void convertsCharSequenceToLong() {
        CharSequence zero = "0";
        CharSequence one = "1";
        CharSequence positive = "55";
        CharSequence negative = "-55";
        long zeroInt = 0;
        long oneInt = 1;
        long anyPositiveInt = 55;
        long anyNegativeInt = -55;

        Assertions.assertThat(DefaultConverter.instance.convert(zero, Long.class, null, null)).isEqualTo(zeroInt);
        Assertions.assertThat(DefaultConverter.instance.convert(one, Long.class, null, null)).isEqualTo(oneInt);
        Assertions.assertThat(DefaultConverter.instance.convert(positive, Long.class, null, null)).isEqualTo(anyPositiveInt);
        Assertions.assertThat(DefaultConverter.instance.convert(negative, Long.class, null, null)).isEqualTo(anyNegativeInt);

        Assertions.assertThat(DefaultConverter.instance.convertBack(zero, Long.class, null, null)).isEqualTo(zeroInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(one, Long.class, null, null)).isEqualTo(oneInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(positive, Long.class, null, null)).isEqualTo(anyPositiveInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(negative, Long.class, null, null)).isEqualTo(anyNegativeInt);

        Assertions.assertThat(DefaultConverter.instance.convert(zero, Long.class, null, null))
                .isEqualTo(DefaultConverter.charSequenceToLong(zero));
        Assertions.assertThat(DefaultConverter.instance.convert(one, Long.class, null, null))
                .isEqualTo(DefaultConverter.charSequenceToLong(one));
        Assertions.assertThat(DefaultConverter.instance.convert(positive, Long.class, null, null))
                .isEqualTo(DefaultConverter.charSequenceToLong(positive));
        Assertions.assertThat(DefaultConverter.instance.convert(negative, Long.class, null, null))
                .isEqualTo(DefaultConverter.charSequenceToLong(negative));
    }

    //TODO spannable tests

}