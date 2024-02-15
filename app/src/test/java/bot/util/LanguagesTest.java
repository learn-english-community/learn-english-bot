package bot.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.deepl.api.Language;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class LanguagesTest {

    private final List<Language> languages = new ArrayList<>();

    @Before
    public void setUp() {
        final List<String> languages =
                List.of(
                        "Bulgarian",
                        "Czech",
                        "Danish",
                        "German",
                        "Greek",
                        "English (British)",
                        "English (American)",
                        "Spanish",
                        "Estonian",
                        "Finnish",
                        "French",
                        "Hungarian",
                        "Indonesian",
                        "Italian",
                        "Japanese",
                        "Korean",
                        "Lithuanian",
                        "Latvian",
                        "Norwegian (Bokmål)",
                        "Dutch",
                        "Polish",
                        "Portuguese (Brazilian)",
                        "Portuguese",
                        "Romanian",
                        "Russian",
                        "Slovak",
                        "Slovenian",
                        "Swedish",
                        "Turkish",
                        "Ukrainian",
                        "Chinese (simplified)");

        languages.forEach(
                language -> {
                    int idx = languages.indexOf(language);
                    Language deeplLanguage = mock(Language.class);
                    String languageCode = Languages.LangEmoji.values()[idx].getCode();

                    when(deeplLanguage.getName()).thenReturn(language);
                    when(deeplLanguage.getCode()).thenReturn(languageCode);

                    this.languages.add(deeplLanguage);
                });
    }

    @Test
    public void testGetCodeFromDisplay() {
        assertEquals("en", Languages.getCodeFromDisplay(languages, "English"));
        assertEquals("de", Languages.getCodeFromDisplay(languages, "German"));
        assertEquals("en-gb", Languages.getCodeFromDisplay(languages, "English (British)"));
        assertEquals("pt-br", Languages.getCodeFromDisplay(languages, "Portuguese (Brazilian)"));
        assertEquals("en", Languages.getCodeFromDisplay(languages, null));
        assertEquals("en", Languages.getCodeFromDisplay(languages, "Unknown"));
        assertEquals("en", Languages.getCodeFromDisplay(languages, ""));
    }

    @Test
    public void testGetEmojiFromCode() {
        assertEquals(":flag_de:", Languages.getEmojiFromCode("de"));
        assertEquals(":flag_gr:", Languages.getEmojiFromCode("el"));
        assertEquals(":flag_us:", Languages.getEmojiFromCode("en-US"));

        assertEquals(":pirate_flag:", Languages.getEmojiFromCode(null));
        assertEquals(":pirate_flag:", Languages.getEmojiFromCode(""));
        assertEquals(":pirate_flag:", Languages.getEmojiFromCode(" "));
    }
}
