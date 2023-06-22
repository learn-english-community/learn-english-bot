package bot.cmd;

import bot.TOTD;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TOTDCommand extends BotCommand {

    public TOTDCommand() {
        super("topic", "Get today's topic to talk about!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(TOTD.getTotd().constructTopicMessage()).queue();
    }
}
