package solutions.alterego.androidbound.converters;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

public class DefaultConverterBoxingAndUnboxingTest {

    @Test
    public void boxesBooleanToBoolean() {
        boolean primitiveBoolean = true;
        Boolean classBoolean = true;

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveBoolean, Boolean.class, null, null)).isEqualTo(classBoolean);
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveBoolean, Boolean.class, null, null)).isInstanceOf(Boolean.class);

        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveBoolean, Boolean.class, null, null)).isEqualTo(classBoolean);
        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveBoolean, Boolean.class, null, null)).isInstanceOf(Boolean.class);

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveBoolean, Boolean.class, null, null))
                .isEqualTo(DefaultConverter.box(primitiveBoolean));
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveBoolean, Boolean.class, null, null))
                .isInstanceOf(DefaultConverter.box(primitiveBoolean).getClass());
    }

    @Test
    public void unboxesBooleanToBoolean() {
        boolean primitiveBoolean = true;
        Boolean classBoolean = true;

        Assertions.assertThat(DefaultConverter.instance.convert(classBoolean, boolean.class, null, null)).isEqualTo(primitiveBoolean);
        Assertions.assertThat(DefaultConverter.instance.convertBack(classBoolean, boolean.class, null, null)).isEqualTo(primitiveBoolean);
        Assertions.assertThat(DefaultConverter.instance.convert(classBoolean, boolean.class, null, null))
                .isEqualTo(DefaultConverter.unbox(classBoolean));
    }

    @Test
    public void boxesShortToShort() {
        short primitiveShort = 0;
        Short classShort = 0;

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveShort, Short.class, null, null)).isEqualTo(classShort);
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveShort, Short.class, null, null)).isInstanceOf(Short.class);

        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveShort, Short.class, null, null)).isEqualTo(classShort);
        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveShort, Short.class, null, null)).isInstanceOf(Short.class);

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveShort, Short.class, null, null))
                .isEqualTo(DefaultConverter.box(primitiveShort));
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveShort, Short.class, null, null))
                .isInstanceOf(DefaultConverter.box(primitiveShort).getClass());
    }

    @Test
    public void unboxesShortToShort() {
        short primitiveShort = 0;
        Short classShort = 0;

        Assertions.assertThat(DefaultConverter.instance.convert(classShort, short.class, null, null)).isEqualTo(primitiveShort);
        Assertions.assertThat(DefaultConverter.instance.convertBack(classShort, short.class, null, null)).isEqualTo(primitiveShort);
        Assertions.assertThat(DefaultConverter.instance.convert(classShort, short.class, null, null))
                .isEqualTo(DefaultConverter.unbox(classShort));
    }

    @Test
    public void boxesByteToByte() {
        byte primitiveByte = 0;
        Byte classByte = 0;

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveByte, Byte.class, null, null)).isEqualTo(classByte);
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveByte, Byte.class, null, null)).isInstanceOf(Byte.class);

        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveByte, Byte.class, null, null)).isEqualTo(classByte);
        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveByte, Byte.class, null, null)).isInstanceOf(Byte.class);

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveByte, Byte.class, null, null))
                .isEqualTo(DefaultConverter.box(primitiveByte));
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveByte, Byte.class, null, null))
                .isInstanceOf(DefaultConverter.box(primitiveByte).getClass());
    }

    @Test
    public void unboxesByteToByte() {
        byte primitiveByte = 0;
        Byte classByte = 0;

        Assertions.assertThat(DefaultConverter.instance.convert(classByte, byte.class, null, null)).isEqualTo(primitiveByte);
        Assertions.assertThat(DefaultConverter.instance.convertBack(classByte, byte.class, null, null)).isEqualTo(primitiveByte);
        Assertions.assertThat(DefaultConverter.instance.convert(classByte, byte.class, null, null))
                .isEqualTo(DefaultConverter.unbox(classByte));
    }

    @Test
    public void boxesIntToInteger() {
        int primitiveInt = 0;
        Integer classInt = 0;

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveInt, Integer.class, null, null)).isEqualTo(classInt);
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveInt, Integer.class, null, null)).isInstanceOf(Integer.class);

        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveInt, Integer.class, null, null)).isEqualTo(classInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveInt, Integer.class, null, null)).isInstanceOf(Integer.class);

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveInt, Integer.class, null, null))
                .isEqualTo(DefaultConverter.box(primitiveInt));
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveInt, Integer.class, null, null))
                .isInstanceOf(DefaultConverter.box(primitiveInt).getClass());
    }

    @Test
    public void unboxesIntegerToInt() {
        int primitiveInt = 0;
        Integer classInt = 0;

        Assertions.assertThat(DefaultConverter.instance.convert(classInt, int.class, null, null)).isEqualTo(primitiveInt);
        Assertions.assertThat(DefaultConverter.instance.convertBack(classInt, int.class, null, null)).isEqualTo(primitiveInt);
        Assertions.assertThat(DefaultConverter.instance.convert(classInt, int.class, null, null))
                .isEqualTo(DefaultConverter.unbox(classInt));
    }

    @Test
    public void boxesLongToLong() {
        long primitiveLong = 0;
        Long classLong = 0L;

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveLong, Long.class, null, null)).isEqualTo(classLong);
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveLong, Long.class, null, null)).isInstanceOf(Long.class);

        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveLong, Long.class, null, null)).isEqualTo(classLong);
        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveLong, Long.class, null, null)).isInstanceOf(Long.class);

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveLong, Long.class, null, null))
                .isEqualTo(DefaultConverter.box(primitiveLong));
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveLong, Long.class, null, null))
                .isInstanceOf(DefaultConverter.box(primitiveLong).getClass());
    }

    @Test
    public void unboxesLongToLong() {
        long primitiveLong = 0;
        Long classLong = 0L;

        Assertions.assertThat(DefaultConverter.instance.convert(classLong, long.class, null, null)).isEqualTo(primitiveLong);
        Assertions.assertThat(DefaultConverter.instance.convertBack(classLong, long.class, null, null)).isEqualTo(primitiveLong);
        Assertions.assertThat(DefaultConverter.instance.convert(classLong, long.class, null, null))
                .isEqualTo(DefaultConverter.unbox(classLong));
    }

    @Test
    public void boxesFloatToFloat() {
        float primitiveFloat = 0;
        Float classFloat = 0f;

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveFloat, Float.class, null, null)).isEqualTo(classFloat);
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveFloat, Float.class, null, null)).isInstanceOf(Float.class);

        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveFloat, Float.class, null, null)).isEqualTo(classFloat);
        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveFloat, Float.class, null, null)).isInstanceOf(Float.class);

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveFloat, Float.class, null, null))
                .isEqualTo(DefaultConverter.box(primitiveFloat));
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveFloat, Float.class, null, null))
                .isInstanceOf(DefaultConverter.box(primitiveFloat).getClass());
    }

    @Test
    public void unboxesFloatToFloat() {
        float primitiveFloat = 0;
        Float classFloat = 0f;

        Assertions.assertThat(DefaultConverter.instance.convert(classFloat, float.class, null, null)).isEqualTo(primitiveFloat);
        Assertions.assertThat(DefaultConverter.instance.convertBack(classFloat, float.class, null, null)).isEqualTo(primitiveFloat);
        Assertions.assertThat(DefaultConverter.instance.convert(classFloat, float.class, null, null))
                .isEqualTo(DefaultConverter.unbox(classFloat));
    }

    @Test
    public void boxesDoubleToDouble() {
        double primitiveDouble = 0;
        Double classDouble = 0d;

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveDouble, Double.class, null, null)).isEqualTo(classDouble);
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveDouble, Double.class, null, null)).isInstanceOf(Double.class);

        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveDouble, Double.class, null, null)).isEqualTo(classDouble);
        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveDouble, Double.class, null, null)).isInstanceOf(Double.class);

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveDouble, Double.class, null, null))
                .isEqualTo(DefaultConverter.box(primitiveDouble));
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveDouble, Double.class, null, null))
                .isInstanceOf(DefaultConverter.box(primitiveDouble).getClass());
    }

    @Test
    public void unboxesDoubleToDouble() {
        double primitiveDouble = 0;
        Double classDouble = 0d;

        Assertions.assertThat(DefaultConverter.instance.convert(classDouble, double.class, null, null)).isEqualTo(primitiveDouble);
        Assertions.assertThat(DefaultConverter.instance.convertBack(classDouble, double.class, null, null)).isEqualTo(primitiveDouble);
        Assertions.assertThat(DefaultConverter.instance.convert(classDouble, double.class, null, null))
                .isEqualTo(DefaultConverter.unbox(classDouble));
    }

    @Test
    public void boxesCharToCharacter() {
        char primitiveChar = 'c';
        Character classCharacter = Character.valueOf('c');

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveChar, Character.class, null, null)).isEqualTo(classCharacter);
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveChar, Character.class, null, null)).isInstanceOf(Character.class);

        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveChar, Character.class, null, null)).isEqualTo(classCharacter);
        Assertions.assertThat(DefaultConverter.instance.convertBack(primitiveChar, Character.class, null, null)).isInstanceOf(Character.class);

        Assertions.assertThat(DefaultConverter.instance.convert(primitiveChar, Character.class, null, null))
                .isEqualTo(DefaultConverter.box(primitiveChar));
        Assertions.assertThat(DefaultConverter.instance.convert(primitiveChar, Character.class, null, null))
                .isInstanceOf(DefaultConverter.box(primitiveChar).getClass());
    }

    @Test
    public void unboxesCharacterToChar() {
        char primitiveChar = 'c';
        Character classCharacter = Character.valueOf('c');

        Assertions.assertThat(DefaultConverter.instance.convert(classCharacter, char.class, null, null)).isEqualTo(primitiveChar);
        Assertions.assertThat(DefaultConverter.instance.convertBack(classCharacter, char.class, null, null)).isEqualTo(primitiveChar);
        Assertions.assertThat(DefaultConverter.instance.convert(classCharacter, char.class, null, null))
                .isEqualTo(DefaultConverter.unbox(classCharacter));
    }

}