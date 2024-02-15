package bot.service;

import bot.entity.word.WiktionaryWord;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class WordService {

    /**
     * Attempts to find a word from the cache.
     *
     * @param word The word to search for
     * @return An instance of the word, null otherwise
     */
    public Optional<WiktionaryWord> findWord(String word) {
        // TODO: Call Consul and find Python microservice
        return Optional.empty();
    }

    /**
     * Performs an API call to the WordsAPI to get a random word and responds with a fancy Discord
     * embed.
     *
     * <p>This method will keep making requests to the API until it gets a word with a definition.
     * It does <b>not</b> have a built-in limit, so this could theoretically spawn rate limit errors
     * if it keeps coming up with words that are lacking definitions.
     *
     * <p>One way to fix this would be to specify this kind of requirement via the API itself,
     * however it is currently impossible to do so, leading us to this solution instead.
     *
     * @return An EmbedBuilder instance of the Discord embed, otherwise returns null if the response
     *     is malformed
     */
    public WiktionaryWord getRandomWordFromAPI() {
        // TODO: Call Consul and find Python microservice
        return null;
    }
}
