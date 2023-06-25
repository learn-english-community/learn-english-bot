package bot.cmd;

import bot.TOTDHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TOTDCommand extends BotCommand {

    public TOTDCommand() {
        super("topic", "Get today's topic to talk about!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(TOTDHandler.getTotd().constructTopicMessage()).queue();
    }
}
