package bot.entity.word;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public abstract class Word {
    @Id private ObjectId id;

    /** The word text. */
    @Indexed private String word;
}
