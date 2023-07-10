package bot.quiz;

import bot.SpringContext;
import bot.entity.User;
import bot.entity.word.CachedWord;
import bot.entity.word.JournalWord;
import bot.service.WordCacheService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum FlashcardQuizFilter {

    NEEDS_PRACTICE("needs-practice",
        "Spaced repetition words", "Practice with words that your spaced repetition profile thinks you should use.",
        Emoji.fromUnicode("💪"),
        (user, metadata) -> {
        long now = System.currentTimeMillis();

        return user.getWords().stream()
            .filter(w -> w.getNextPractice() > now)
            .collect(Collectors.toList());
    }),
    WORD_QUALITY("word-quality",
        "Word quality", "Select words based on the given quality.",
        Emoji.fromUnicode("✨"),
        (user, metadata) -> {
        if (!(metadata instanceof Integer)) return Collections.emptyList();
        int quality = (int) metadata;

        return user.getWords().stream()
            .filter(w -> w.getQuality() == quality)
            .collect(Collectors.toList());
    }),
    PART_OF_SPEECH("part-of-speech",
        "Part of speech", "Practice words based on their type.",
        Emoji.fromUnicode("💬"),
        (user, metadata) -> {
        if (!(metadata instanceof String)) return Collections.emptyList();
        String partOfSpeech = (String) metadata;

        // @Christolis: I guess there's no other way to acquire this bean.
        WordCacheService wordCacheService = SpringContext.getBean(WordCacheService.class);

        return user.getWords().stream()
            .filter(w -> {
                Optional<CachedWord> cachedWord = wordCacheService.getWordFromCacheOrAPI(w.getWord());

                return cachedWord.map(word -> word
                    .getResults()
                    .get(w.getDefinitionIndex())
                    .getPartOfSpeech()
                    .equalsIgnoreCase(partOfSpeech))
                    .orElse(false);
            })
            .collect(Collectors.toList());
    }),
    RECENT("recent",
        "Recent", "Practice recently added words.",
        Emoji.fromUnicode("⏳"),
        (user, metadata) -> {
        if (!(metadata instanceof Long)) return Collections.emptyList();
        long now = System.currentTimeMillis();
        Long rangeMs = (Long) metadata;

        return user.getWords().stream()
            .filter(w -> now - w.getLastPracticed() < rangeMs)
            .collect(Collectors.toList());
    }),
    OLD("old",
        "Old", "Practice words that you added a long time ago.",
        Emoji.fromUnicode("🕰️"),
        (user, metadata) -> {
        if (!(metadata instanceof Long)) return Collections.emptyList();
        long now = System.currentTimeMillis();
        Long rangeMs = (Long) metadata;

        return user.getWords().stream()
            .filter(w -> now - w.getLastPracticed() >= rangeMs)
            .collect(Collectors.toList());
    }),
    ALL("all",
        "All words", "Test yourself with your entire journal!",
        Emoji.fromUnicode("📘"),
        (user, metadata) -> user.getWords());

    @Getter
    private final String label;

    @Getter
    private final String title;

    @Getter
    private final String description;

    @Getter
    private final Emoji emoji;

    @Getter
    private final BiFunction<User, Object, List<JournalWord>> filterProcessor;

    FlashcardQuizFilter(String label,
                        String title,
                        String description,
                        Emoji emoji,
                        BiFunction<User, Object, List<JournalWord>> filterProcessor) {
        this.label = label;
        this.title = title;
        this.description = description;
        this.emoji = emoji;
        this.filterProcessor = filterProcessor;
    }
}
