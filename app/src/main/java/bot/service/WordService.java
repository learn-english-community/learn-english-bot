package bot.service;

import bot.consul.ConsulDiscoveryClientWrapper;
import bot.entity.word.WiktionaryWord;
import java.net.URI;
import java.util.Optional;
import kong.unirest.Unirest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class WordService {

    private static final String SERVICE_NAME_WORD_WIKTIONARY = "word-wiktionary";
    private static final String DEFINE_ENDPOINT = "define";
    private final ConsulDiscoveryClientWrapper discoveryClient;

    @Autowired
    public WordService(ConsulDiscoveryClientWrapper discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * Attempts to find a word from the cache.
     *
     * @param word The word to search for
     * @return An instance of the word, null otherwise
     */
    public Optional<WiktionaryWord> findWord(String word) {
        // TODO: Call Consul and find Python microservice
        URI serviceUri = discoveryClient.getService(SERVICE_NAME_WORD_WIKTIONARY).orElse(null);

        if (serviceUri == null) {
            final var errorMsg =
                    String.format(
                            "Tried to find a word from %s, got a null serviceUri.",
                            SERVICE_NAME_WORD_WIKTIONARY);

            log.error(errorMsg);
            return Optional.empty();
        }

        log.warn("Attempting to contact: " + serviceUri.getHost() + ":" + serviceUri.getPort());

        final String url = getUriFromEndpoint(serviceUri, DEFINE_ENDPOINT);
        Unirest.get(url).queryString("word", word).thenConsume(response -> {
            var body = response.getContentAsString("UTF-8");

        });

        return Optional.empty();
    }

    /**
     * Takes in a {@link URI} and an endpoint string, and constructs a URL
     * used to make the request.
     */
    private static String getUriFromEndpoint(final URI uri, String endpoint) {
        return "http://%s:%d/%s".formatted(uri.getHost(), uri.getPort(), endpoint);
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
