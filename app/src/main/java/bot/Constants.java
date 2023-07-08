package bot;

public class Constants {

    public static final int MAX_TRANSLATION_LENGTH = 100;
    public static final int MAX_TRANSLATION_USAGES = 25;

    public static final String CRON_DAILY_MORNING = "0 7 * * *";
    public static final String CRON_DAILY_MIDDLE = "0 15 * * *";
    public static final String CRON_TEST = "* * * * *";

    public static final String TOTD_API_URL = "https://conversation-starter1.p.rapidapi.com/";
    public static final String WORDS_API_URL = "https://wordsapiv1.p.rapidapi.com/words/";
    public static final String WORDS_API_HOST = "wordsapiv1.p.rapidapi.com";
    public static final int MAX_DEFINITION_FIELDS = 8;

    public static final int MAX_JOURNAL_WORD_QUALITY = 4;

    public static final String DATABASE_NAME = "learn_english";
}
