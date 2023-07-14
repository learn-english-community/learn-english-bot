package bot.service;

import bot.Constants;
import bot.entity.User;
import bot.entity.session.Session;
import bot.entity.word.JournalWord;
import bot.entity.word.Word;
import bot.repository.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** User service component. */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Inserts a new user to the database.
     *
     * @param user The user to insert
     */
    public void createUser(@NonNull User user) {
        userRepository.save(user);
    }

    public User getUser(@NonNull String discordId) {
        return userRepository.findUserByDiscordId(discordId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
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
     * @return A list of all the user's sessions
     */
    public List<Session> getSessions(@NonNull String discordId) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            return user.getSessions();
        }
        return Collections.emptyList();
    }

    /**
     * @param discordId The Discord ID of the user.
     * @return A list of all the user's sessions by index
     */
    public Optional<Session> getSessionByIndex(@NonNull String discordId, int index) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            return user.getSessions().stream().filter(s -> s.getIndex() == index).findFirst();
        }
        return Optional.empty();
    }

    /**
     * @param discordId The Discord ID of the user.
     * @return A list of the user's journal words by type
     */
    public List<Session> getSessionsByType(@NonNull String discordId, Session.Type type) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            return user.getSessions().stream()
                    .filter(s -> s.getType().equals(type))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Saves a session to a user.
     *
     * <p>This also updates the corresponding last activity of the user.
     *
     * @param discordId The Discord ID of the user.
     * @param session The session to save.
     */
    public void saveSession(@NonNull String discordId, Session session) {
        User user = userRepository.findUserByDiscordId(discordId);
        long now = System.currentTimeMillis();

        if (user != null && session != null) {
            if (user.getSessions() == null) user.setSessions(new ArrayList<>());

            if (user.getLastActivity() == null) user.setLastActivity(new HashMap<>());

            user.getSessions().add(session);
            user.getLastActivity().put(session.getType().name(), now);
            userRepository.save(user);
        }
    }

    /**
     * Returns the last activity timestamp by type.
     *
     * @param discordId The Discord ID of the user.
     * @param type The type of the session to look for.
     * @return The timestamp that this activity was last done.
     */
    public long getLastActivityByType(@NonNull String discordId, Session.Type type) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            return user.getLastActivity().get(type.name());
        }
        return 0L;
    }

    /**
     * Gets the day points in a specified day.
     *
     * @param discordId The Discord ID of the user.
     * @param day The day to get
     * @return The amount of points accumulated in the day
     */
    public int getDayPoints(@NonNull String discordId, DayOfWeek day) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            return user.getWeeklyPoints().get(day.getValue() - 1);
        }
        return 0;
    }

    /**
     * Gets the day points in a specified day.
     *
     * @param discordId The Discord ID of the user.
     * @param points The amount of points to add.
     */
    public void addDayPoints(@NonNull String discordId, int points) {
        User user = userRepository.findUserByDiscordId(discordId);
        int today = LocalDate.now().getDayOfWeek().getValue() - 1;

        if (user != null) {
            int initialPoints = user.getWeeklyPoints().get(today);
            user.getWeeklyPoints().set(today, user.getWeeklyPoints().get(today) + points);

            boolean surpassedPts =
                    user.getWeeklyPoints().get(today) >= Constants.MIN_POINTS_FOR_STREAK;
            boolean justAchievedStreak = initialPoints >= Constants.MIN_POINTS_FOR_STREAK;

            if (surpassedPts && !justAchievedStreak) countStreak(user);

            userRepository.save(user);
        }
    }

    /**
     * Counts a user's streak. Handles the maximumStreak as well.
     *
     * @param user An entity instance of the user
     */
    public void countStreak(@NonNull User user) {
        int newStreak = user.getCurrentStreak() + 1;

        user.setCurrentStreak(newStreak);
        user.setMaximumStreak(Math.max(newStreak, user.getMaximumStreak()));
        userRepository.save(user);
    }

    /**
     * Counts a user's streak. Handles the maximumStreak as well.
     *
     * @param discordId The Discord ID of the user.
     */
    public void countStreak(@NonNull String discordId) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) countStreak(user);
    }

    /**
     * Resets a user's streak.
     *
     * @param discordId the Discord ID of the user.
     */
    public void resetStreak(@NonNull String discordId) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            resetStreak(user);
        }
    }

    /**
     * Resets a user's streak.
     *
     * @param user The entity instance of the user.
     */
    public void resetStreak(@NonNull User user) {
        user.setCurrentStreak(0);
        userRepository.save(user);
    }

    /**
     * Gets the most recent journal words of a user.
     *
     * @param discordId The Discord ID of a user
     * @param page The page to find.
     * @param count The amount of items to include in the result
     * @return The list of journal words
     */
    public List<JournalWord> getRecentJournalWords(@NonNull String discordId, int page, int count) {
        User user = userRepository.findUserByDiscordId(discordId);

        if (user != null) {
            int start = page * count;

            List<JournalWord> words =
                    user.getWords().stream()
                            .sorted(Comparator.comparingLong(JournalWord::getTimeAdded).reversed())
                            .collect(Collectors.toList());

            int totalWords = words.size();
            int totalPages = (int) Math.ceil((double) totalWords / count);

            if (page > totalPages) {
                return Collections.emptyList(); // Return an empty list if the page is out of range
            }

            int end = Math.min(start + count, totalWords);
            if (start > words.size())
                return words.stream().limit(count).collect(Collectors.toList());

            return words.subList(start, end);
        }

        return Collections.emptyList();
    }

    /**
     * @param discordId The Discord ID of the user.
     * @param word The word to get from the list of the words
     * @return The word as a JournalWord
     */
    public JournalWord getJournalWord(@NonNull String discordId, String word, int index) {
        List<JournalWord> words = getJournalWords(discordId);

        for (JournalWord journalWord : words) {
            if (journalWord.getWord().equalsIgnoreCase(word)
                    && journalWord.getDefinitionIndex() == index) return journalWord;
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
    public boolean hasJournalWord(@NonNull String discordId, String word, int definitionIndex) {
        List<JournalWord> words = getJournalWords(discordId);

        return words.stream()
                .filter(w -> w.getDefinitionIndex() == definitionIndex)
                .map(Word::getWord)
                .collect(Collectors.toList())
                .contains(word);
    }

    /**
     * Updates a journal word's data using the SuperMemo algorithm.
     *
     * @param discordId The user whose word needs updating
     * @param wordString The word to update
     * @param index The word index to help with indexing
     * @param quality The quality inputted by the user
     */
    public void updateWordSuperMemo(String discordId, String wordString, int index, int quality) {
        User user = userRepository.findUserByDiscordId(discordId);
        Optional<JournalWord> optionalWord =
                user.getWords().stream()
                        .filter(w -> getWordWithIndexFilter(w, index, wordString))
                        .findFirst();

        if (quality < 0 || quality > 5) return;
        if (wordString == null || optionalWord.isEmpty()) return;

        JournalWord word = optionalWord.get();
        int repetitions = word.getRepetitions();
        float easiness = word.getEasiness();
        int interval = word.getInterval();

        easiness = calculateEasiness(easiness, quality);

        if (quality < 3) {
            repetitions = 0;
        } else {
            repetitions += 1;
        }

        if (repetitions <= 1) {
            interval = 1;
        } else if (repetitions == 2) {
            interval = 6;
        } else {
            interval = Math.round(interval * easiness);
        }

        int millisecondsInDay = 60 * 60 * 24 * 1000;
        long now = System.currentTimeMillis();
        long nextPracticeDate = now + (long) millisecondsInDay * interval;

        word.setNextPractice(nextPracticeDate);
        word.setLastPracticed(now);
        word.setRepetitions(word.getRepetitions() + 1);

        userRepository.save(user);
    }

    private float calculateEasiness(float easiness, int quality) {
        return (float)
                Math.max(1.3, easiness + 0.1 - (5.0 - quality) * (0.08 + (5.0 - quality) * 0.02));
    }

    private boolean getWordWithIndexFilter(JournalWord word, int index, String wordString) {
        return word.getDefinitionIndex() == index && word.getWord().equalsIgnoreCase(wordString);
    }
}
