package bot.view;

import bot.Constants;
import bot.entity.User;
import java.awt.*;
import java.time.DayOfWeek;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.apache.commons.lang3.StringUtils;

public class StreakView {

    private static final String emojiYes = "<:streakyes:1128942923631824906>";
    private static final String emojiNo = "<:streakno:1128943616908337182>";

    public MessageEmbed getStreak(User user) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Your streak status: " + user.getCurrentStreak() + " 🔥");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription("Maximum streak: " + user.getMaximumStreak());
        AtomicInteger counter = new AtomicInteger(1);

        user.getWeeklyPoints()
                .forEach(
                        (points -> {
                            Emoji emoji =
                                    Emoji.fromFormatted(
                                            (points >= Constants.MIN_POINTS_FOR_STREAK)
                                                    ? emojiYes
                                                    : emojiNo);
                            String value = emoji.getFormatted() + " (" + points + " pts)";
                            String day =
                                    StringUtils.capitalize(
                                            DayOfWeek.of(counter.getAndIncrement())
                                                    .name()
                                                    .toLowerCase());

                            embedBuilder.addField(day, value, true);
                        }));

        return embedBuilder.build();
    }
}
