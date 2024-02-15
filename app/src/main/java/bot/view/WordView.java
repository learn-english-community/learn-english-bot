package bot.view;

import bot.Constants;
import bot.entity.word.WiktionaryWord;
import bot.entity.word.WordDefinition;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class WordView {
    private static final Pattern PATTERN_EXAMPLE =
            Pattern.compile("[A-Z].+\\.", Pattern.CASE_INSENSITIVE);

    /**
     * Gets a WordsAPI response and converts it into a fancy Discord embed.
     *
     * @param word The WordsAPI response
     * @return An EmbedBuilder instance of the embed
     */
    public EmbedBuilder getDefinitionEmbed(WiktionaryWord word) {
        EmbedBuilder embed = new EmbedBuilder();
        AtomicInteger counter = new AtomicInteger();

        // Constant data
        embed.setTitle(word.getWord());
        embed.setColor(Constants.EMBED_COLOR);
        embed.setDescription(getPronunciationText(word));

        // Add word etymology as field
        embed.addField(new MessageEmbed.Field("Etymology", word.getEtymology(), false));

        if (word.getEtymology() == null) {
            return embed;
        }

        for (WordDefinition definition : word.getDefinitions()) {
            if (embed.getFields().size() >= Constants.MAX_DEFINITION_FIELDS) break;

            String name = definition.getPartOfSpeech();
            StringBuilder value = new StringBuilder();

            definition
                    .getText()
                    .forEach(
                            text ->
                                    value.append("> ")
                                            .append(counter.incrementAndGet())
                                            .append(". ")
                                            .append(text));

            embed.addField(new MessageEmbed.Field(name, value.toString(), false));
        }

        counter.set(0);

        // Create examples field
        // We are using the first one only since they are all the same type
        final List<String> examples = word.getDefinitions().get(0).getExamples();
        final StringBuilder stringBuilder = new StringBuilder();
        examples.stream()
                .parallel()
                .filter(example -> PATTERN_EXAMPLE.matcher(example).matches())
                .limit(5)
                .forEach(
                        example -> {
                            String boldWord = "**" + word.getWord() + "**";
                            String highlightedExample = example.replace(word.getWord(), boldWord);

                            stringBuilder
                                    .append(counter.incrementAndGet())
                                    .append(". ")
                                    .append(highlightedExample)
                                    .append("\n");
                        });
        embed.addField(new MessageEmbed.Field("Examples", stringBuilder.toString(), false));

        return embed;
    }

    private static String getPronunciationText(WiktionaryWord word) {
        StringBuilder sb = new StringBuilder();

        word.getPronunciations()
                .getText()
                .forEach(
                        text -> {
                            sb.append(text).append("\n");
                        });

        return sb.toString().trim();
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
