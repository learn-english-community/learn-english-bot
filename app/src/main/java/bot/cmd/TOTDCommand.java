package bot.cmd;

import bot.TOTD;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TOTDCommand extends BotCommand {

    public TOTDCommand() {
        super("topic", "Get today's topic to talk about!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String topic = TOTD.getTotd().getTopic();
        String message = "**Today's topic is**: " + topic + " :thinking:";

        event.reply(message).queue();
    }
}
