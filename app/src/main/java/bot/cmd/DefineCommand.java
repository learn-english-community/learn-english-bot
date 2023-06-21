package bot.cmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bot.App;
import bot.Constants;
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

        event.deferReply().queue();

        HttpResponse<String> response = Unirest.get(Constants.WORDS_API_URL + word)
            .header("X-RapidAPI-Key", App.getenv("KEY_WORDSAPI"))
            .asString();

        String body = response.getBody();
        JSONObject jsonObject = new JSONObject(body);
        WordsAPIResponse responseGson = new Gson().fromJson(body, WordsAPIResponse.class);

        if (jsonObject.has("success") && !responseGson.isSuccess()) {
            event.getHook().editOriginal("I am not familiar with that word! :pensive:")
                .queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        String responseWord = responseGson.getWord();

        String ipa = String.format("[%s]", responseGson.getPronunciation().getAll());

        // Constant data
        embed.setTitle("Learn English™ Dictionary");
        embed.setFooter("• Data provided by WordsAPI");
        embed.setColor(39129);

        embed.setDescription(responseWord + " – " + ipa);
        final String emptyFieldLine = "> \u200E";
        for (WordsAPIResponse.Definition definition : responseGson.getResults()) {
            if (embed.getFields().size() >= Constants.MAX_DEFINITION_FIELDS)
                break;

            String name = definition.getPartOfSpeech();
            StringBuilder value = new StringBuilder();
            String synonyms = definition.getSynonyms() != null ?
                definition.getSynonyms().stream()
                .distinct()
                .collect(Collectors.joining(", ")) : null;
            String similarTo = definition.getSimilarTo() != null ?
                definition.getSimilarTo().stream()
                .distinct()
                .collect(Collectors.joining(", ")) : null;

            List<String> exampleSentences = definition.getExamples();
            String exampleLine = "";
            if (exampleSentences != null) {
                exampleLine = "> *\"" + exampleSentences.get(0) + "\"*";
            }

            value.append("> " + definition.getDefinition() + System.lineSeparator());

            if (synonyms != null && similarTo != null) {
                value.append(emptyFieldLine + System.lineSeparator());
                value.append("> " + toHeader("Synonyms") + synonyms + System.lineSeparator());
                value.append("> " + toHeader("Similar to") + similarTo + System.lineSeparator());
            } else if (synonyms != null) {
                value.append(emptyFieldLine + System.lineSeparator());
                value.append("> " + toHeader("Synonyms") + synonyms + System.lineSeparator());
            } else if (similarTo != null) {
                value.append(emptyFieldLine + System.lineSeparator());
                value.append("> " + toHeader("Similar to") + similarTo + System.lineSeparator());
            }

            if (!exampleLine.isEmpty()) {
                value.append(emptyFieldLine + System.lineSeparator());
                value.append(exampleLine);
            }

            MessageEmbed.Field field = new MessageEmbed.Field(name, value.toString(), false);
            embed.addField(field);
        }

        event.getHook().editOriginalEmbeds(embed.build()).queue();
    }

    private static String toHeader(String title) {
        return "__**" + title + "**__: ";
    }
}

