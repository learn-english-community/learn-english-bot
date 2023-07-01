package bot.entity.word;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class JournalWord extends Word {
    /**
     * A timestamp that indicates the time this word was added.
     */
    private long timeAdded;
}
