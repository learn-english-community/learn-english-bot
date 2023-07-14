package bot.task;

import bot.entity.User;
import bot.service.UserService;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StreakResetTask implements Runnable {

    private final UserService userService;
    private final String ACTIVITY_NAME = "JOURNAL_QUIZ";

    private final long DAY_AMOUNT = 1000 * 60 * 60 * 24;
    private final long HOUR_AMOUNT = 1000 * 60 * 60;
    private final String STREAK_RESET_MSG =
            "Your streak is going to be reset! " + "Collect a few points to maintain it! 🔥";

    public StreakResetTask(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run() {
        List<User> users = userService.getAllUsers();
        long now = System.currentTimeMillis();

        log.info("Executing task");

        users.forEach(
                user -> {
                    long lastActivity = user.getLastActivity().get(ACTIVITY_NAME);
                    long lastRelative = now - lastActivity;
                    boolean hasStreak = user.getCurrentStreak() != 0;

                    if (lastRelative >= DAY_AMOUNT) {
                        userService.resetStreak(user);
                    } else if (lastRelative >= DAY_AMOUNT - HOUR_AMOUNT && hasStreak) {
                        // Notify user an hour before the streak is about to get reset
                        user.sendPrivateTemporaryMessage(STREAK_RESET_MSG);
                    }
                });
    }
}
