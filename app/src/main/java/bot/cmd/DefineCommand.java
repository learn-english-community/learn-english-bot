package bot.cmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bot.App;
import bot.Constants;
import bot.Dictionary;
import bot.model.WordsAPIResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.json.JSONObject;

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

