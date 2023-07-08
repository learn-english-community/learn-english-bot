package bot.entity.word;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class JournalWord extends Word {
    /**
     * A timestamp that indicates the time this word was added.
     */
    private long timeAdded;

    /**
     * The index of the definition that the user has saved.
     */
    private int definitionIndex;

    /**
     * SuperMemo algorithm value.
     */
    private int repetitions;

    /**
     * SuperMemo algorithm value.
     */
    private int quality;

    /**
     * SuperMemo algorithm value.
     */
    private float easiness;

    /**
     * SuperMemo algorithm value.
     */
    private int interval;

    /**
     * A timestamp that indicates the time this word was added.
     */
    private long lastPracticed;

    /**
     * SuperMemo algorithm value.
     */
    private long nextPractice;
}
