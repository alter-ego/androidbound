package solutions.alterego.androidbound.parsers;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.binding.data.BindingSpecification;
import solutions.alterego.androidbound.converters.ValueConverterService;
import solutions.alterego.androidbound.resources.ResourceService;

public class BindingSpecificationListParserTest {

    private final static String binding0 = "{Text =@ LocalizedFullName(Profile)}; {Typeface @- ToFont(this)}";

    private final static String binding1 = "{SourceUri @= OptimizedImageUrl(ImageUrl, '70, 1.0, png, 80, fill')};";

    private final static String binding2 = "{Text @= Name}; {Typeface @- ToFont(this, 'semibold')}";

    private final static String binding3 = "{Progress @= CurrentSteps}; {MaxProgress @= TotalSteps}; {ProgressColor @= Color}";

    private final static String binding4
            = "{Visibility @= ToVisibility(ChallengeViewModel.ChallengeVisible)}; { Click @= ChallengeViewModel.ChallengeDetails };";

    private final static String binding5 = "{Visibility @= ToVisibility(ChallengeViewModel.ChallengeAvatarVisible)};\n"
            + "                    {SourceUriWithDynamicRatio @= OptimizedImageUrl(ChallengeViewModel.ChallengeAvatar, '360, 0, jpg, 75, fill')}";

    private final static String binding6
            = "{Visibility @= ToVisibility(ChallengeViewModel.CurrentMealsSharedVisible)}; {Text @= ChallengeViewModel.CurrentMealsShared}; ; {Typeface @- ToFont(this)}";

    private final static String binding7 = "{Text @= ListDescription}; {Click @- ShowDetail}; {Typeface @- ToFont(this)}";

    private final static String binding8 = "{Text @=@ TeamLabel}; {Typeface @- ToFont(this)}; {Click @- OpenTeamSearch}";

    private final static String binding9
            = "{Click @- ExecutePrimaryAction}; {BackgroundResource @- PrimaryAction.BackgroundResource}; {Visibility @= ToVisibility(PrimaryActionVisible)}";

    private final static List<String> bindingStringList = Arrays
            .asList(binding0, binding1, binding2, binding3, binding4, binding5, binding6, binding7, binding8, binding9);

    private static BindingSpecificationListParser mListParser;

    private static long tickTime;

    public BindingSpecificationListParserTest() {
        ValueConverterService converterService = new ValueConverterService(NullLogger.instance);
        ResourceService resourceService = new ResourceService(NullLogger.instance);

        BindingSpecificationParser bindingParser = new BindingSpecificationParser(converterService, resourceService, NullLogger.instance);
        mListParser = new BindingSpecificationListParser(bindingParser, NullLogger.instance);
    }

    private static void tick() {
        tickTime = System.nanoTime();
    }

    private static void tock(String action) {
        long mstime = (System.nanoTime() - tickTime) / 1000000;
        System.out.println(action + ": " + mstime + "ms");
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parseCorrectlyParses() throws Exception {
        List<BindingSpecification> specList;

        specList = mListParser.parse(binding0);
        Assertions.assertThat(specList).hasSize(2);

        specList = mListParser.parse(binding1);
        Assertions.assertThat(specList).hasSize(1);

        specList = mListParser.parse(binding2);
        Assertions.assertThat(specList).hasSize(2);

        specList = mListParser.parse(binding3);
        Assertions.assertThat(specList).hasSize(3);

        specList = mListParser.parse(binding4);
        Assertions.assertThat(specList).hasSize(2);

        specList = mListParser.parse(binding5);
        Assertions.assertThat(specList).hasSize(2);

        specList = mListParser.parse(binding6);
        Assertions.assertThat(specList).hasSize(3);

        specList = mListParser.parse(binding7);
        Assertions.assertThat(specList).hasSize(3);

        specList = mListParser.parse(binding8);
        Assertions.assertThat(specList).hasSize(3);

        specList = mListParser.parse(binding9);
        Assertions.assertThat(specList).hasSize(3);
    }

    @Test
    public void parseBenchmark() throws Exception {

        List<String> items = generateParsingData(1000000);
        parseWithStringBuilder(items);

        assert true;
    }

    private static List<String> generateParsingData(int itemCount) {
        Random random = new Random();
        List<String> items = new ArrayList<>();

        for (int n = 0; n < itemCount; ++n) {
            StringBuilder s = new StringBuilder();
            int bindingStringIndex = random.nextInt(9);
            items.add(bindingStringList.get(bindingStringIndex));
        }

        System.out.println("generated data");

        return items;
    }

    static void parseWithStringBuilder(List<String> items) {
        tick();

        List<BindingSpecification> specList;

        for (String item : items) {
            specList = mListParser.parse(item);
        }

        tock("parseWithStringBuilder finished");
    }

}