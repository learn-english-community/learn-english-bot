package bot.service;

import bot.App;
import bot.Constants;
import bot.entity.word.CachedWord;
import bot.repository.WordCacheRepository;
import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Log4j2
public class WordCacheService {

    private static final long OUTDATED_THRESHOLD_MS = 1000 * 60 * 60 * 24 * 14; // 2 weeks
    private final WordCacheRepository wordCacheRepository;

    @Autowired
    public WordCacheService(WordCacheRepository wordCacheRepository) {
        this.wordCacheRepository = wordCacheRepository;
    }

    /**
     * Attempts to find a word from the cache.
     *
     * @param word The word to search for
     * @return An instance of the word, null otherwise
     */
    public CachedWord findWord(String word) {
        return wordCacheRepository.getCachedWordByWord(word);
    }

    public CachedWord getWordFromCacheOrAPI(String word) {
        CachedWord cachedWord = findWord(word);

        if (cachedWord != null && isWordOutdated(word)) {
            wordCacheRepository.deleteCachedWordByWord(word);
            cachedWord = null;
        }

        if (cachedWord == null) {
            long now = System.currentTimeMillis();
            AtomicInteger index = new AtomicInteger(0);
            cachedWord = getWordFromAPI(word);

            List<CachedWord.Definition> results = cachedWord.getResults();
            cachedWord.setLastUpdate(now);
            cachedWord.setSuccess(true);

            if (results != null) {
                cachedWord.getResults().forEach(result -> {
                    result.setIndex(index.getAndIncrement());
                });
            }
            wordCacheRepository.save(cachedWord);
        }

        return cachedWord;
    }

    /**
     * Checks if a word exists in the cache.
     *
     * @param word The word to search for
     * @return True if the word was found, false if not.
     */
    public boolean existsWord(String word) {
        return wordCacheRepository.getCachedWordByWord(word) != null;
    }

    /**
     * Checks if a certain word in the cache is outdated.
     * <p>
     * It does so by checking if the word in the cache has been there
     * for more than a certain time interval, defined by the private
     * constant OUTDATED_THRESHOLD_MS.
     *
     * @param word The word to search for
     * @return True if the word is outdated, false if not.
     */
    public boolean isWordOutdated(String word) {
        CachedWord wordInstance = findWord(word);

        if (wordInstance == null)
            return true;

        final long now = System.currentTimeMillis();
        return now - wordInstance.getLastUpdate() > OUTDATED_THRESHOLD_MS;
    }

    /**
     * Performs an API call to the WordsAPI and responds with
     * the contents of the word.
     *
     * @param word The word to find
     * @return The CachedWord
     */
    public CachedWord getWordFromAPI(String word) {
        HttpResponse<String> response = Unirest.get(Constants.WORDS_API_URL + word)
            .header("X-RapidAPI-Key", App.getenv("KEY_RAPID_API"))
            .asString();

        return processHTTPResponse(response);
    }

    /**
     * Performs an API call to the WordsAPI to get a random word
     * and responds with a fancy Discord embed.
     * <p>
     * This method will keep making requests to the API until
     * it gets a word with a definition. It does <b>not</b> have
     * a built-in limit, so this could theoretically spawn rate
     * limit errors if it keeps coming up with words that are
     * lacking definitions.
     * <p>
     * One way to fix this would be to specify this kind of
     * requirement via the API itself, however it is currently
     * impossible to do so, leading us to this solution instead.
     *
     * @return An EmbedBuilder instance of the Discord embed,
     *  otherwise returns null if the response is malformed
     */
    public CachedWord getRandomWordFromAPI() {
        CachedWord wordsObject;
        do {
            HttpResponse<String> response = Unirest.get(Constants.WORDS_API_URL)
                .header("X-RapidAPI-Key", App.getenv("KEY_RAPID_API"))
                .queryString("random", true)
                .asString();
            wordsObject = processHTTPResponse(response);

            if (wordsObject == null)
                return null;
        } while (wordsObject.getResults() == null || wordsObject.getResults().isEmpty());

        return wordsObject;
    }
    /**
     * Performs some checks on the WordsAPI response to make sure that
     * it can be converted into a fancy Discord embed.
     *
     * @param response The WordsAPI response
     * @return The WordsAPI response as an object, null if the response
     *  is invalid
     */
    private CachedWord processHTTPResponse(HttpResponse<String> response) {
        String body = response.getBody();
        JSONObject jsonObject = new JSONObject(body);
        CachedWord responseGson = new Gson().fromJson(body, CachedWord.class);

        log.debug("Called WordsAPI for word " + responseGson.getWord());
        if (jsonObject.has("success") && !responseGson.isSuccess())
            return null;

        return responseGson;
    }
}
