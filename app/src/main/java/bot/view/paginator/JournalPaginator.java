package bot.view.paginator;

import bot.Constants;
import bot.entity.word.CachedWord;
import bot.entity.word.JournalWord;
import bot.service.UserService;
import bot.service.WordCacheService;
import lombok.Builder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
        }
        else
            words = Collections.emptyList();

        words.forEach(word -> {
            Optional<CachedWord> cachedWordOptional = wordCacheService.getWordFromCacheOrAPI(word.getWord());

            if (cachedWordOptional.isPresent()) {
                CachedWord cachedWord = cachedWordOptional.get();
                EmbedBuilder embed = new EmbedBuilder();
                String wordString = word.getWord();
                Optional<CachedWord.Definition> definitionOptional = cachedWord.getResults().stream()
                    .filter(d -> d.getIndex() == word.getDefinitionIndex())
                    .findFirst();

                if (definitionOptional.isEmpty())
                    return;

                CachedWord.Definition definition = definitionOptional.get();

                String storedTime = t.format(new Date(word.getTimeAdded()));
                String nextPracticeTime = t.format(new Date(word.getNextPractice()));

                embed.setTitle(wordString);
                embed.setColor(39129);

                embed.addField("Part of speech", definition.getPartOfSpeech(), false);
                embed.addField("Definition", definition.getDefinition(), false);
                embed.addField("Quality", renderQuality(calculateQuality(word)), false);
                embed.addField("Stored time", storedTime, true);
                embed.addField("Times practiced", String.valueOf(word.getRepetitions()), true);
                embed.addField("Next practice", nextPracticeTime, true);

                embeds.add(embed.build());
            }
        });

        return embeds;
    }

    /**
     * Calculates a user-friendly quality indicator.
     * <p>
     * The idea of this quality indicator is that it gets the last
     * chosen quality that the user picked, and as the next practice
     * timestamp gets closer or surpassed, the quality yields quality
     * values closer to 1. The maximum quality value is 4. The quality
     * is one if it has surpassed it or has touched the timestamp.
     * <p>
     * For instance, if the word has been checked very recently,
     * the quality should be 4, as the time difference between the time
     * practiced and the time moments later would be minimal. But with the
     * passage of time, it should lower down to a quality of 1 as we get closer
     * to the next practice time. It should be exactly 1 if we have surpassed,
     * or we are in the timestamp that indicated that the word should be
     * practiced.
     *
     * @param word The word to test
     * @return The quality of this word
     */
    private int calculateQuality(JournalWord word) {
        long now = System.currentTimeMillis();

        if (now >= word.getNextPractice()) {
            return 1;
        }

        float div = now / (float) word.getNextPractice();
        int n = Constants.MAX_JOURNAL_WORD_QUALITY - (int) Math.floor(div * 3);

        return Math.max(1, n);
    }

    private String renderQuality(int quality) {
        switch (quality) {
            case 1: return "🟥";
            case 2: return "🟧 🟧";
            case 3: return "🟨 🟨 🟨";
            case 4: return "🟩 🟩 🟩 🟩 ";
            default: return "🚫";
        }
    }
}
