package bot.cmd;

import bot.entity.JournalDisplay;
import bot.view.JournalView;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JournalCommand extends BotCommand {

    private static final int WORDS_COUNT = 5;
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
        JournalDisplay journalDisplay = journalView.getUserJournalDisplay(user, 0, WORDS_COUNT);

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
                .addActionRow(journalDisplay.getActionButtons())
                .queue();
        } else {
            event.reply("You can only send this command as a direct message! :blue_book:")
                .setEphemeral(true)
                .queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId();
        User user = event.getUser();

        event.deferEdit().queue();

        if (!id.startsWith("journal-")) return;
        int page = Integer.parseInt(id.split(":")[1]);
        JournalDisplay journalDisplay = journalView.getUserJournalDisplay(user, page, WORDS_COUNT);

        event.getHook().editOriginal(journalDisplay.getMessage())
            .setEmbeds(journalDisplay.getWords())
            .setActionRow(journalDisplay.getActionButtons())
            .queue();
    }
}
