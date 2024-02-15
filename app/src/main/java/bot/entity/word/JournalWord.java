package bot.entity.word;

import bot.Constants;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class JournalWord {

    @SerializedName("word")
    @Indexed
    private String word;

    /** Stores the saved definition. */
    private WordDefinition savedDefinition;

    /** A timestamp that indicates the time this word was added. */
    private long timeAdded;

    /** The index of the definition that the user has saved. */
    private int definitionIndex;

    /** The index of the text of a definition that the user has saved. */
    private int textIndex;

    /** SuperMemo algorithm value. */
    private int repetitions;

    /** SuperMemo algorithm value. */
    private int quality;

    /** SuperMemo algorithm value. */
    private float easiness;

    /** SuperMemo algorithm value. */
    private int interval;

    /** A timestamp that indicates the time this word was added. */
    private long lastPracticed;

    /** SuperMemo algorithm value. */
    private long nextPractice;

    /**
     * Calculates a user-friendly quality indicator.
     *
     * <p>The idea of this quality indicator is that it gets the last chosen quality that the user
     * picked, and as the next practice timestamp gets closer or surpassed, the quality yields
     * quality values closer to 1. The maximum quality value is 4. The quality is one if it has
     * surpassed it or has touched the timestamp.
     *
     * <p>For instance, if the word has been checked very recently, the quality should be 4, as the
     * time difference between the time practiced and the time moments later would be minimal. But
     * with the passage of time, it should lower down to a quality of 1 as we get closer to the next
     * practice time. It should be exactly 1 if we have surpassed, or we are in the timestamp that
     * indicated that the word should be practiced.
     *
     * @return The quality of this word
     */
    public int calculateQuality() {
        long now = System.currentTimeMillis();

        if (now >= this.getNextPractice()) {
            return 1;
        }

        float div = (now - this.getLastPracticed()) / (float) this.getNextPractice();
        int n = Constants.MAX_JOURNAL_WORD_QUALITY - (int) Math.floor(div * 3);

        return Math.max(1, n);
    }

    @Override
    public String toString() {
        return this.word;
    }
}
