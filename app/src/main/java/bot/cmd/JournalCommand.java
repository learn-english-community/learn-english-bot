package bot.cmd;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class JournalCommand extends BotCommand {

    public JournalCommand() {
        super("journal", "Check your journal!", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        if (!event.getChannelType().equals(ChannelType.PRIVATE)) {
            event.reply("I sent your journal as a direct message! :blue_book:")
                .setEphemeral(true)
                .queue();
        }

        user.openPrivateChannel().queue(channel -> {
            // Send journal here
        });
    }
}
