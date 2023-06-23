package bot;

import bot.model.WordsAPIResponse;
import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.stream.Collectors;

public class Dictionary {
    private static Dictionary dictionary;

    private Dictionary() {}

    private EmbedBuilder getDefinitionEmbed(WordsAPIResponse responseGson) {
        EmbedBuilder embed = new EmbedBuilder();
        String responseWord = responseGson.getWord();

        String ipaContent = null;
        String ipa = "";
        WordsAPIResponse.Pronunciation pronunciation = responseGson.getPronunciation();
        if (pronunciation != null) {
            ipaContent = pronunciation.getAll();
            ipa = String.format("[%s]", ipaContent);
        }

        // Constant data
        embed.setTitle("Learn English™ Dictionary");
        embed.setFooter("• Data provided by WordsAPI");
        embed.setColor(39129);
        embed.setDescription(ipaContent != null ?
            responseWord + " – " + ipa : responseWord
        );

        final String emptyFieldLine = "> \u200E";

        if (responseGson.getResults() != null) {
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
        }

        return embed;
    }

    private WordsAPIResponse processResponse(HttpResponse<String> response) {
        String body = response.getBody();
        JSONObject jsonObject = new JSONObject(body);
        WordsAPIResponse responseGson = new Gson().fromJson(body, WordsAPIResponse.class);

        if (jsonObject.has("success") && !responseGson.isSuccess())
            return null;

        return responseGson;
    }

    public EmbedBuilder getWordDefinition(String word) {
        HttpResponse<String> response = Unirest.get(Constants.WORDS_API_URL + word)
            .header("X-RapidAPI-Key", App.getenv("KEY_RAPID_API"))
            .asString();
        WordsAPIResponse wordsObject = processResponse(response);

        if (wordsObject == null)
            return null;

        return getDefinitionEmbed(wordsObject);
    }

    public EmbedBuilder getRandomWord() {
        WordsAPIResponse wordsObject;
        do {
            HttpResponse<String> response = Unirest.get(Constants.WORDS_API_URL)
                .header("X-RapidAPI-Key", App.getenv("KEY_RAPID_API"))
                .queryString("random", true)
                .asString();
            wordsObject = processResponse(response);

            if (wordsObject == null)
                return null;
        } while (wordsObject.getResults() == null || wordsObject.getResults().isEmpty());

        return getDefinitionEmbed(wordsObject);
    }

    private static String toHeader(String title) {
        return "__**" + title + "**__: ";
    }

    public static Dictionary getDictionary() {
        if (dictionary == null)
            dictionary = new Dictionary();

        return dictionary;
    }
}
