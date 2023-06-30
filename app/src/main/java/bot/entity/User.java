package bot.entity;

import bot.entity.word.JournalWord;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Entity("users")
@Builder
@Getter
@Setter
public class User {
    @Id private ObjectId id;

    /**
     * The Discord ID of the user.
     */
    private String discordId;

    /**
     * A list of all the words the user has saved.
     */
    private List<JournalWord> words;
}
