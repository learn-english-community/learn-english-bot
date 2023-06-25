package bot.cmd;

import bot.Dictionary;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * Represents the "define" command.
 */
public class DefineCommand extends BotCommand {

    public DefineCommand() {
        super("define", "Get a word definition!");

        getArguments().put("word", new CommandArgument(
            OptionType.STRING,
            "word",
            "The word you want to get the definition of!",
            true, false
        ));
    }
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String word = event.getOption("word").getAsString();
        MessageEmbed embed = Dictionary.getDictionary().getWordDefinition(word).build();

        event.deferReply().queue();

        if (embed == null) {
            event.getHook().editOriginal("I am not familiar with that word! :pensive:")
                .queue();
            return;
        }

        event.getHook().editOriginalEmbeds(embed).queue();
    }

}

