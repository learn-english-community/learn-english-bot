package bot.view;

import bot.Constants;
import bot.entity.word.CachedWord;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class WordCacheView {

    /**
     * Gets a WordsAPI response and converts it into a fancy Discord embed.
     *
     * @param cachedWord The WordsAPI response
     * @return An EmbedBuilder instance of the embed
     */
    public EmbedBuilder getDefinitionEmbed(CachedWord cachedWord) {
        EmbedBuilder embed = new EmbedBuilder();
        String responseWord = cachedWord.getWord();

        String ipa = "";
        String pronunciation = cachedWord.getPronunciation();

        if (pronunciation != null) {
            ipa = String.format("[%s]", pronunciation);
        }

        // Constant data
        embed.setTitle("Learn English™ Dictionary");
        embed.setFooter("• Data provided by WordsAPI");
        embed.setColor(Constants.EMBED_COLOR);
        embed.setDescription(pronunciation != null ? responseWord + " – " + ipa : responseWord);

        final String emptyFieldLine = "> \u200E";

        if (cachedWord.getResults() != null) {
            for (CachedWord.Definition definition : cachedWord.getResults()) {
                if (embed.getFields().size() >= Constants.MAX_DEFINITION_FIELDS) break;

                String name = definition.getPartOfSpeech();
                StringBuilder value = new StringBuilder();
                String synonyms =
                        definition.getSynonyms() != null
                                ? definition.getSynonyms().stream()
                                        .distinct()
                                        .collect(Collectors.joining(", "))
                                : null;
                String similarTo =
                        definition.getSimilarTo() != null
                                ? definition.getSimilarTo().stream()
                                        .distinct()
                                        .collect(Collectors.joining(", "))
                                : null;

                List<String> exampleSentences = definition.getExamples();
                String exampleLine = "";
                if (exampleSentences != null) {
                    exampleLine = "> *\"" + exampleSentences.get(0) + "\"*";
                }

                value.append("> " + definition.getDefinition() + System.lineSeparator());

                if (synonyms != null && similarTo != null) {
                    value.append(emptyFieldLine + System.lineSeparator());
                    value.append("> " + toHeader("Synonyms") + synonyms + System.lineSeparator());
                    value.append(
                            "> " + toHeader("Similar to") + similarTo + System.lineSeparator());
                } else if (synonyms != null) {
                    value.append(emptyFieldLine + System.lineSeparator());
                    value.append("> " + toHeader("Synonyms") + synonyms + System.lineSeparator());
                } else if (similarTo != null) {
                    value.append(emptyFieldLine + System.lineSeparator());
                    value.append(
                            "> " + toHeader("Similar to") + similarTo + System.lineSeparator());
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

    /**
     * Converts text to a Markdown style header.
     *
     * @param title The title to convert
     * @return A Markdown style header of the title
     */
    private static String toHeader(String title) {
        return "__**" + title + "**__: ";
    }
}
