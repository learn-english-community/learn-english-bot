package bot.util;

import com.deepl.api.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class Languages {

    public static List<Language> languages = new ArrayList<>();

    public static String getCodeFromDisplay(String language) {
        return languages.stream()
            .filter(l -> l.getName().equalsIgnoreCase(language))
            .map(Language::getCode)
            .findFirst().orElse("en");
    }

    public static String getEmojiFromCode(String code) {

        for (LangEmoji le : EnumSet.allOf(LangEmoji.class)) {
            if (le.getCode().equalsIgnoreCase(code)) return le.getEmoji();
        }
        return ":pirate_flag:";
    }

    public enum LangEmoji {
        BULGARIAN("bg", ":flag_bg:"),
        CZECH("cs", ":flag_cz:"),
        DANISH("da", ":flag_dk:"),
        GERMAN("de", ":flag_de:"),
        GREEK("el", ":flag_gr:"),
        ENGLISH_GB("en-gb", ":flag_gb:"),
        ENGLISH_US("en-us", ":flag_us:"),
        SPANISH("es", ":flag_es:"),
        ESTONIAN("et", ":flag_ee:"),
        FINNISH("fi", ":flag_fl:"),
        FRENCH("fr", ":flag_fr:"),
        HUNGARIAN("hu", ":flag_hu:"),
        INDONESIAN("id", ":flag_id:"),
        ITALIAN("it", ":flag_it:"),
        JAPANESE("ja", ":flag_jp:"),
        KOREAN("ko", ":flag_kr:"),
        LITHUANIAN("lt", ":flag_lt:"),
        LATVIAN("lv", ":flag_lv:"),
        NORWEGIAN("nb", ":flag_no:"),
        DUTCH("nl", ":flag_nl:"),
        POLISH("pl", ":flag_pl:"),
        PORTUGUESE_BR("pt-br", ":flag_br:"),
        PORTUGUESE_PT("pt-pt", ":flag_pt:"),
        ROMANIAN("ro", ":flag_ro"),
        RUSSIAN("ru", ":flag_ru:"),
        SLOVAK("sk", ":flag_sk:"),
        SLOVENIAN("sl", ":flag_si:"),
        SWEDISH("sv", ":flag_se:"),
        TURKISH("tr", ":flag_tr:"),
        UKRAINIAN("uk", ":flag_ua:"),
        CHINESE("zh", ":flag_cn:");

        private final String code;
        private final String emoji;

        LangEmoji(String code, String emoji) {
            this.code = code;
            this.emoji = emoji;
        }

        public String getCode() {
            return code;
        }

        public String getEmoji() {
            return emoji;
        }
    }
}