package bot.entity.word;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("word")
    @Indexed
    private String word;

    @SerializedName("etymology")
    private String etymology;

    @SerializedName("pronunciations")
    private WordPronunciation pronunciations;
}
