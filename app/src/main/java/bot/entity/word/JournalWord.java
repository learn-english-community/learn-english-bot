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
     * SuperMemo algorithm value.
     */
    private int nextPractice;
}
