package solutions.alterego.androidbound.parsers;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.android.converters.BooleanToVisibilityConverter;
import solutions.alterego.androidbound.binding.data.BindingMode;
import solutions.alterego.androidbound.binding.data.BindingSpecification;
import solutions.alterego.androidbound.converters.ValueConverterService;
import solutions.alterego.androidbound.resources.ResourceService;

public class BindingSpecificationParserTest {

    //TODO add test for fallbackValue

    private final static String binding_converter_with_params
            = "{SourceUriWithDynamicRatio @= ToVisibility(ChallengeViewModel.ChallengeAvatar, '360, 0, jpg, 75, fill')}";

    private final static String binding_converter_simple = "{Visibility @= ToVisibility(ChallengeViewModel.CurrentMealsSharedVisible)}";

    private final static String binding_hierarchical_source = "{BackgroundResource @- PrimaryAction.BackgroundResource}";

    private final String binding_mode_Default = "{Text = TeamLabel}";

    private final String binding_mode_OneWayToSourceOneTime = "{Text -@ TeamLabel}";

    private final String binding_mode_OneWayToSource = "{Text =@ TeamLabel}";

    private final String binding_mode_OneWayOneTime = "{Text @- TeamLabel}";

    private final String binding_mode_OneWay = "{Text @= TeamLabel}";

    private final String binding_mode_TwoWay = "{Text @=@ TeamLabel}";

    private final String binding_mode_Accumulate = "{Text @+ TeamLabel}";

    private final String binding_mode_AccumulateToSource = "{Text +@ TeamLabel}";

    private final String binding_mode_AccumulateTwoWay = "{Text @+@ TeamLabel}";

    private final String binding_mode_RemoveSource = "{Text !@ TeamLabel}";

    private final String binding_mode_Error = "{Text x@ TeamLabel}";

    private final String binding_mode_Error2 = "{Text TeamLabel}";

    private final String binding_with_double_curly_braces = "{{Text = TeamLabel}}";

    private final String binding_with_missing_starting_curly_brace = "Text = TeamLabel}";

    private final String binding_with_missing_ending_curly_brace = "{Text = TeamLabel";

    private final String binding_with_trailing_semicolon = "{Text = TeamLabel};";

    private final String binding_with_trailing_semicolons = "{Text = TeamLabel};;";

    private final ValueConverterService mValueConverterService;

    private BindingSpecificationParser mParser;

    public BindingSpecificationParserTest() {
        mValueConverterService = new ValueConverterService(NullLogger.instance);
        ResourceService resourceService = new ResourceService(NullLogger.instance);

        mParser = new BindingSpecificationParser(mValueConverterService, resourceService, NullLogger.instance, false);
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parseCorrectlyParsesTarget() throws Exception {
        BindingSpecification Default = mParser.parse(binding_mode_Default);
        Assertions.assertThat(Default.getTarget()).isEqualTo("Text");
    }

    @Test
    public void parseCorrectlyParsesSource() throws Exception {
        BindingSpecification Default = mParser.parse(binding_mode_Default);
        Assertions.assertThat(Default.getSource()).isEqualTo("TeamLabel");
    }

    @Test
    public void parseCorrectlyParsesHierarchicalSource() throws Exception {
        BindingSpecification Default = mParser.parse(binding_hierarchical_source);
        Assertions.assertThat(Default.getSource()).isEqualTo("PrimaryAction.BackgroundResource");
    }

    @Test
    public void parseDefaultsToDefaultConverter() throws Exception {
        BindingSpecification Default = mParser.parse(binding_converter_simple);
        Assertions.assertThat(Default.getValueConverter().getClass().toString()).contains("DefaultConverter");

        BindingSpecification WithParams = mParser.parse(binding_converter_with_params);
        Assertions.assertThat(WithParams.getValueConverter().getClass().toString()).contains("DefaultConverter");
    }

    @Test
    public void parseCorrectlyParsesSimpleConverter() throws Exception {
        mValueConverterService.registerConverter(new BooleanToVisibilityConverter());
        BindingSpecification Default = mParser.parse(binding_converter_simple);

        Assertions.assertThat(Default.getTarget()).isEqualTo("Visibility");
        Assertions.assertThat(Default.getSource()).isEqualTo("ChallengeViewModel.CurrentMealsSharedVisible");
        Assertions.assertThat(Default.getValueConverter().getClass().toString()).contains("ToVisibility");
    }

    @Test
    public void parseCorrectlyParsesConverterWithParams() throws Exception {
        mValueConverterService.registerConverter(new BooleanToVisibilityConverter());
        BindingSpecification Default = mParser.parse(binding_converter_with_params);

        Assertions.assertThat(Default.getTarget()).isEqualTo("SourceUriWithDynamicRatio");
        Assertions.assertThat(Default.getSource()).isEqualTo("ChallengeViewModel.ChallengeAvatar");
        Assertions.assertThat(Default.getConverterParameter()).isEqualTo("360, 0, jpg, 75, fill");
        Assertions.assertThat(Default.getValueConverter().getClass().toString()).contains("ToVisibility");
    }

    @Test
    public void parseHasConverterDefaultFallbackValueAsNull() throws Exception {
        mValueConverterService.registerConverter(new BooleanToVisibilityConverter());
        BindingSpecification Default = mParser.parse(binding_converter_simple);

        Assertions.assertThat(Default.getTarget()).isEqualTo("Visibility");
        Assertions.assertThat(Default.getSource()).isEqualTo("ChallengeViewModel.CurrentMealsSharedVisible");
        Assertions.assertThat(Default.getValueConverter().getClass().toString()).contains("ToVisibility");
        Assertions.assertThat(Default.getFallbackValue()).isNull();
    }

    @Test
    public void parseCorrectlyParsesBindingModes() throws Exception {
        BindingSpecification Default = mParser.parse(binding_mode_Default);
        Assertions.assertThat(Default.getMode()).isEqualTo(BindingMode.Default);

        BindingSpecification OneWayToSourceOneTime = mParser.parse(binding_mode_OneWayToSourceOneTime);
        Assertions.assertThat(OneWayToSourceOneTime.getMode()).isEqualTo(BindingMode.OneWayToSourceOneTime);

        BindingSpecification OneWayToSource = mParser.parse(binding_mode_OneWayToSource);
        Assertions.assertThat(OneWayToSource.getMode()).isEqualTo(BindingMode.OneWayToSource);

        BindingSpecification OneWayOneTime = mParser.parse(binding_mode_OneWayOneTime);
        Assertions.assertThat(OneWayOneTime.getMode()).isEqualTo(BindingMode.OneWayOneTime);

        BindingSpecification OneWay = mParser.parse(binding_mode_OneWay);
        Assertions.assertThat(OneWay.getMode()).isEqualTo(BindingMode.OneWay);

        BindingSpecification TwoWay = mParser.parse(binding_mode_TwoWay);
        Assertions.assertThat(TwoWay.getMode()).isEqualTo(BindingMode.TwoWay);

        BindingSpecification Accumulate = mParser.parse(binding_mode_Accumulate);
        Assertions.assertThat(Accumulate.getMode()).isEqualTo(BindingMode.Accumulate);

        BindingSpecification AccumulateToSource = mParser.parse(binding_mode_AccumulateToSource);
        Assertions.assertThat(AccumulateToSource.getMode()).isEqualTo(BindingMode.AccumulateToSource);

        BindingSpecification AccumulateTwoWay = mParser.parse(binding_mode_AccumulateTwoWay);
        Assertions.assertThat(AccumulateTwoWay.getMode()).isEqualTo(BindingMode.AccumulateTwoWay);

        BindingSpecification RemoveSource = mParser.parse(binding_mode_RemoveSource);
        Assertions.assertThat(RemoveSource.getMode()).isEqualTo(BindingMode.RemoveSource);

        BindingSpecification Error = mParser.parse(binding_mode_Error);
        Assertions.assertThat(Error.getMode()).isEqualTo(BindingMode.Default);

        BindingSpecification Error2 = mParser.parse(binding_mode_Error2);
        Assertions.assertThat(Error2.getMode()).isEqualTo(BindingMode.Default);
    }

    @Test
    public void parseIgnoresDoubleCurlyBraces() throws Exception {
        BindingSpecification Default = mParser.parse(binding_with_double_curly_braces);
        Assertions.assertThat(Default.getTarget()).isEqualTo("Text");
        Assertions.assertThat(Default.getSource()).isEqualTo("TeamLabel");
        Assertions.assertThat(Default.getMode()).isEqualTo(BindingMode.Default);
    }

    @Test
    public void parseIgnoresTrailingSemicolon() throws Exception {
        BindingSpecification Default = mParser.parse(binding_with_trailing_semicolon);
        Assertions.assertThat(Default.getTarget()).isEqualTo("Text");
        Assertions.assertThat(Default.getSource()).isEqualTo("TeamLabel");
        Assertions.assertThat(Default.getMode()).isEqualTo(BindingMode.Default);
    }

    @Test
    public void parseIgnoresTrailingSemicolons() throws Exception {
        BindingSpecification Default = mParser.parse(binding_with_trailing_semicolons);
        Assertions.assertThat(Default.getTarget()).isEqualTo("Text");
        Assertions.assertThat(Default.getSource()).isEqualTo("TeamLabel");
        Assertions.assertThat(Default.getMode()).isEqualTo(BindingMode.Default);
    }

    @Test
    public void parsIgnoresMissingEndingCurlyBrace() throws Exception {
        BindingSpecification Default = mParser.parse(binding_with_missing_ending_curly_brace);
        Assertions.assertThat(Default.getTarget()).isEqualTo("Text");
        Assertions.assertThat(Default.getSource()).isEqualTo("TeamLabel");
        Assertions.assertThat(Default.getMode()).isEqualTo(BindingMode.Default);
    }

    @Test
    public void parseIgnoresMissingStartingCurlyBrace() throws Exception {
        BindingSpecification Default = mParser.parse(binding_with_missing_starting_curly_brace);
        Assertions.assertThat(Default.getTarget()).isEqualTo("Text");
        Assertions.assertThat(Default.getSource()).isEqualTo("TeamLabel");
        Assertions.assertThat(Default.getMode()).isEqualTo(BindingMode.Default);
    }

}