package bot.cmd;

import bot.TOTDHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Represents the "topic" command.
 *
 * <p>It is used to simply send a message to the channel that this command was executed in and lets
 * the user know what the topic of the day is.
 */
public class TOTDCommand extends BotCommand {

    public TOTDCommand() {
        super("topic", "Get today's topic to talk about!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(TOTDHandler.getTotd().constructTopicMessage()).queue();
    }
}
