package bot.entity.session;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a session.
 *
 * <p>This is mostly used to record past sessions, primarily to keep track of user streaks and for
 * future metric aggregation.
 */
@Getter
public class Session {
    private int index;

    private final Type type;

    private final long timestamp;

    @Builder
    public Session(Type type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public enum Type {
        JOURNAL_QUIZ
    }
}
