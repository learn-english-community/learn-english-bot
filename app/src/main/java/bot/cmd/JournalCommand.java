package bot.cmd;

import bot.entity.JournalDisplay;
import bot.view.JournalView;
import bot.view.paginator.JournalPaginator;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JournalCommand extends BotCommand {

    private final JournalView journalView;

    @Autowired
    public JournalCommand(JournalView journalView) {
        super("journal", "Check your journal!", true);
        this.journalView = journalView;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        ChannelType channelType = event.getChannelType();
        JournalDisplay journalDisplay = journalView.getUserJournalDisplay(user, 0);

        if (channelType.equals(ChannelType.PRIVATE)) {
            if (journalDisplay.getErrorMessage() != null) {
                event.reply(journalDisplay.getErrorMessage())
                    .setEphemeral(true)
                    .queue();
                return;
            }

            event.reply(journalDisplay.getMessage())
                .addEmbeds(journalDisplay.getWords())
                .setEphemeral(true)
                .queue();
        } else {
            event.reply("You can only send this command as a direct message! :blue_book:")
                .setEphemeral(true)
                .queue();
        }
    }
}
