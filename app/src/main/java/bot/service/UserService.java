package bot.service;

import bot.entity.User;
import bot.entity.word.JournalWord;
import bot.entity.word.Word;
import bot.repository.UserRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User service component.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Inserts a new user to the database.
     * @param user The user to insert
     */
    public void createUser(@NonNull User user) {
        userRepository.save(user);
    }

    /**
     * @param discordId The Discord ID of the user.
     * @return True if the user exists, false if not
     */
    public boolean userExists(@NonNull String discordId) {
        return userRepository.findUserByDiscordId(discordId) != null;
    }

    /**
     * @param discordId The Discord ID of the user.
     * @return A list of the user's journal words
     */
    public List<JournalWord> getJournalWords(@NonNull String discordId) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            return user.getWords();
        }
        return Collections.emptyList();
    }

    /**
     * @param discordId The Discord ID of the user.
     * @param word The word to get from the list of the words
     * @return The word as a JournalWord
     */
    public JournalWord getJournalWord(@NonNull String discordId, String word) {
        List<JournalWord> words = getJournalWords(discordId);

        for (JournalWord journalWord : words) {
            if (journalWord.getWord().equalsIgnoreCase(word))
                return journalWord;
        }
        return null;
    }

    /**
     * @param discordId The Discord ID of the user.
     * @param word The journal word to add
     */
    public void addWord(@NonNull String discordId, @NonNull JournalWord word) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            user.getWords().add(word);
            userRepository.save(user);
        }
    }

    /**
     * @param discordId The Discord ID of the user.
     * @param word The word as a string
     * @return True if the journal word exists, false if otherwise
     */
    public boolean hasJournalWord(@NonNull String discordId, String word) {
        List<JournalWord> words = getJournalWords(discordId);

        return words.stream()
            .map(Word::getWord)
            .collect(Collectors.toList())
            .contains(word);
    }
}
