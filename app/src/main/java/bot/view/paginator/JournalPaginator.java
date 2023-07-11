package bot.view.paginator;

import bot.entity.word.CachedWord;
import bot.entity.word.JournalWord;
import bot.service.UserService;
import bot.service.WordCacheService;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Builder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Builder
@Component
public class JournalPaginator extends Paginator<List<MessageEmbed>> {

    private final UserService userService;
    private final WordCacheService wordCacheService;

    @Autowired
    public JournalPaginator(UserService userService, WordCacheService wordCacheService) {
        this.userService = userService;
        this.wordCacheService = wordCacheService;
    }

    /**
     * Gets a page of a user.
     *
     * @param user The user to get the journal of
     * @param page The page number. Starts from 0
     * @return A list of embeds, containing words
     */
    @Override
    public List<MessageEmbed> getPage(User user, int page, int count) {
        List<MessageEmbed> embeds = new ArrayList<>();
        List<JournalWord> words;
        PrettyTime t = new PrettyTime();

        if (userService.userExists(user.getId())) {
            words = userService.getRecentJournalWords(user.getId(), page, count);
        } else words = Collections.emptyList();

        words.forEach(
                word -> {
                    Optional<CachedWord> cachedWordOptional =
                            wordCacheService.getWordFromCacheOrAPI(word.getWord());

                    if (cachedWordOptional.isPresent()) {
                        CachedWord cachedWord = cachedWordOptional.get();
                        EmbedBuilder embed = new EmbedBuilder();
                        String wordString = word.getWord();
                        Optional<CachedWord.Definition> definitionOptional =
                                cachedWord.getResults().stream()
                                        .filter(d -> d.getIndex() == word.getDefinitionIndex())
                                        .findFirst();

                        if (definitionOptional.isEmpty()) return;

                        CachedWord.Definition definition = definitionOptional.get();

                        String storedTime = t.format(new Date(word.getTimeAdded()));
                        String nextPracticeTime = t.format(new Date(word.getNextPractice()));
                        String lastPracticeTime = t.format(new Date(word.getLastPracticed()));
                        String squashedDefinition = splitString(definition.getDefinition(), 8);

                        embed.setTitle(wordString);
                        embed.setColor(39129);

                        embed.addField("Part of speech", definition.getPartOfSpeech(), false);
                        embed.addField("Definition", squashedDefinition, false);
                        embed.addField("Stored time", storedTime, true);
                        embed.addField("Last practiced", lastPracticeTime, true);
                        embed.addField("Next practice", nextPracticeTime, true);
                        embed.addField("Quality", renderQuality(word.calculateQuality()), true);
                        embed.addField(
                                "Times practiced", String.valueOf(word.getRepetitions()), true);

                        embeds.add(embed.build());
                    }
                });

        return embeds;
    }

    private String renderQuality(int quality) {
        switch (quality) {
            case 1:
                return "🟥";
            case 2:
                return "🟧 🟧";
            case 3:
                return "🟨 🟨 🟨";
            case 4:
                return "🟩 🟩 🟩 🟩 ";
            default:
                return "🚫";
        }
    }

    public static String splitString(final String string, final int chunkSize) {
        final String[] words = string.split("\\s+");
        final int numberOfChunks = (words.length + chunkSize - 1) / chunkSize;
        StringBuilder builder = new StringBuilder();
        List<String> chunks = IntStream.range(0, numberOfChunks)
            .mapToObj(index -> {
                int start = index * chunkSize;
                int end = Math.min((index + 1) * chunkSize, words.length);
                return String.join(" ", Arrays.copyOfRange(words, start, end));
            })
            .collect(Collectors.toList());

        chunks.forEach(chunk -> builder.append(chunk).append("\n"));
        return builder.toString();
    }
}
