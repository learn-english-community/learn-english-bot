package bot.entity;

import bot.entity.word.JournalWord;
import java.util.List;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Builder
@Getter
@Setter
public class User {
    @Id private ObjectId id;

    /** The Discord ID of the user. */
    private String discordId;

    /** A list of all the words the user has saved. */
    private List<JournalWord> words;
}
