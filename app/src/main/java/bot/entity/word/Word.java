package bot.entity.word;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("words")
public abstract class Word {
    @Id private ObjectId id;

    /**
     * The word text.
     */
    private String word;

    public ObjectId getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
