package bot.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import bot.entity.User;
import com.deepl.api.Language;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LanguagesTest {

    @Before
    public void setUp() {
        final List<String> languages = List.of(
            "Bulgarian", "Czech", "Danish",
            "German", "Greek", "English (British)",
            "English (American)", "Spanish", "Estonian",
            "Finnish", "French", "Hungarian", "Indonesian",
            "Italian", "Japanese", "Korean", "Lithuanian",
            "Latvian", "Norwegian (Bokmål)", "Dutch",
            "Polish", "Portuguese (Brazilian)", "Portuguese",
            "Romanian", "Russian", "Slovak", "Slovenian",
            "Swedish", "Turkish", "Ukrainian", "Chinese (simplified)"
        );

        Languages.languages.clear();

        languages.forEach(language -> {
            int idx = languages.indexOf(language);
            mock(User.class);
            Language deeplLanguage = mock(Language.class);
            String languageCode = Languages.LangEmoji.values()[idx].getCode();

            when(deeplLanguage.getName()).thenReturn(language);
            when(deeplLanguage.getCode()).thenReturn(languageCode);

            Languages.languages.add(deeplLanguage);
        });
    }

    @Test
    public void testGetCodeFromDisplay() {
        assertEquals("en", Languages.getCodeFromDisplay("English"));
        assertEquals("de", Languages.getCodeFromDisplay("German"));
        assertEquals("en-gb", Languages.getCodeFromDisplay("English (British)"));
        assertEquals("pt-br", Languages.getCodeFromDisplay("Portuguese (Brazilian)"));
        assertEquals("en", Languages.getCodeFromDisplay(null));
        assertEquals("en", Languages.getCodeFromDisplay("Unknown"));
        assertEquals("en", Languages.getCodeFromDisplay(""));
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
