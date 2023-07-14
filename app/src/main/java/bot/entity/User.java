package bot.entity;

import bot.App;
import bot.entity.session.Session;
import bot.entity.word.JournalWord;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.*;
import net.dv8tion.jda.api.entities.Message;
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

    /** Holds information about the user's streaks. */
    private int currentStreak;

    private int maximumStreak;

    private static List<Integer> generateWeeklyPoints() {
        return new ArrayList<>(Collections.nCopies(7, 0));
    }

    public void sendPrivateTemporaryMessage(String content) {
        App.getJda()
                .retrieveUserById(discordId)
                .queue(
                        user -> {
                            user.openPrivateChannel()
                                    .flatMap(channel -> channel.sendMessage(content))
                                    .delay(30, TimeUnit.SECONDS)
                                    .flatMap(Message::delete)
                                    .queue();
                        });
    }
}
