package bot.cmd;

import bot.service.UserService;
import bot.view.StreakView;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class StreakCommand extends BotCommand {

    private final UserService userService;

    public StreakCommand(UserService userService) {
        super("streak", "Check out your streak", true);
        this.userService = userService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        StreakView view = new StreakView();

        event.reply("")
                .setEphemeral(true)
                .setEmbeds(view.getStreak(userService.getUser(userId)))
                .queue();
    }
}
