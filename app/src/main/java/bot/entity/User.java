package bot.entity;

import bot.entity.session.Session;
import bot.entity.word.JournalWord;
import java.util.*;
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

    /** Holds an object with points accumulated for each day of the week. */
    @Builder.Default private List<Integer> weeklyPoints = generateWeeklyPoints();

    private List<Session> sessions;

    private Map<String, Long> lastActivity;

    private static List<Integer> generateWeeklyPoints() {
        return new ArrayList<>(Collections.nCopies(7, 0));
    }
}
