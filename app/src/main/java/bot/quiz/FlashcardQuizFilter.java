package bot.quiz;

import bot.SpringContext;
import bot.cmd.JournalCommand;
import bot.entity.User;
import bot.entity.word.CachedWord;
import bot.entity.word.JournalWord;
import bot.service.WordCacheService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
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
        },
        null),
    WORD_QUALITY("word-quality",
        "Word quality", "Select words based on the given quality.",
        Emoji.fromUnicode("✨"),
        (user, metadata) -> {
            int quality = Integer.parseInt(metadata.getAsString());

            return user.getWords().stream()
                .filter(w -> w.getQuality() == quality)
                .collect(Collectors.toList());
        },
        () -> {
            String id = JournalCommand.PREFIX + "word-quality";
            TextInput textInput = TextInput.create("practice-input", "Enter the desired quality", TextInputStyle.SHORT)
                .setPlaceholder("A number from 1 to 4")
                .setRequiredRange(1, 4)
                .setRequired(true)
                .build();

            return Modal.create(id, "Practice: Quality")
                .addComponents(ActionRow.of(textInput))
                .build();
        }),
    PART_OF_SPEECH("part-of-speech",
        "Part of speech", "Practice words based on their type.",
        Emoji.fromUnicode("💬"),
        (user, metadata) -> {
            String partOfSpeech = metadata.getAsString();

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
        },
        () -> {
            String id = JournalCommand.PREFIX + "part-of-speech";
            TextInput textInput = TextInput.create("practice-input", "Enter the desired part of speech", TextInputStyle.SHORT)
                .setPlaceholder("e.g noun, verb, adjective")
                .setRequired(true)
                .build();

            return Modal.create(id, "Practice: Part of speech")
                .addComponents(ActionRow.of(textInput))
                .build();
        }),
    RECENT("recent",
        "Recent", "Practice recently added words.",
        Emoji.fromUnicode("⏳"),
        (user, metadata) -> {
            long now = System.currentTimeMillis();
            int range = Integer.parseInt(metadata.getAsString());
            long rangeMs = (long) range * 1000 * 60 * 60 * 24;

            return user.getWords().stream()
                .filter(w -> now - w.getLastPracticed() < rangeMs)
                .collect(Collectors.toList());
        },
        () -> {
            String id = JournalCommand.PREFIX + "recent";
            TextInput textInput = TextInput.create("practice-input", "Enter time (in days)", TextInputStyle.SHORT)
                .setRequiredRange(1, 30)
                .setRequired(true)
                .build();

            return Modal.create(id, "Practice: Recent")
                .addComponents(ActionRow.of(textInput))
                .build();
        }),
    OLD("old",
        "Old", "Practice words that you added a long time ago.",
        Emoji.fromUnicode("🕰️"),
        (user, metadata) -> {
            long now = System.currentTimeMillis();
            int range = Integer.parseInt(metadata.getAsString());
            long rangeMs = (long) range * 1000 * 60 * 60 * 24;

            return user.getWords().stream()
                .filter(w -> now - w.getLastPracticed() >= rangeMs)
                .collect(Collectors.toList());
        },
        () -> {
            String id = JournalCommand.PREFIX + "old";
            TextInput textInput = TextInput.create("practice-input", "Enter time (in days)", TextInputStyle.SHORT)
                .setRequiredRange(1, 90)
                .setRequired(true)
                .build();

            return Modal.create(id, "Practice: Old")
                .addComponents(ActionRow.of(textInput))
                .build();
        }),
    ALL("all",
        "All words", "Test yourself with your entire journal!",
        Emoji.fromUnicode("📘"),
        (user, metadata) -> user.getWords(),
        null);

    @Getter
    private final String label;

    @Getter
    private final String title;

    @Getter
    private final String description;

    @Getter
    private final Emoji emoji;

    @Getter
    private final Supplier<Modal> metadataModal;

    @Getter
    private final BiFunction<User, ModalMapping, List<JournalWord>> filterProcessor;

    FlashcardQuizFilter(String label,
                        String title,
                        String description,
                        Emoji emoji,
                        BiFunction<User, ModalMapping, List<JournalWord>> filterProcessor,
                        Supplier<Modal> metadataModal) {
        this.label = label;
        this.title = title;
        this.description = description;
        this.emoji = emoji;
        this.filterProcessor = filterProcessor;
        this.metadataModal = metadataModal;
    }

    public static FlashcardQuizFilter getByLabel(String label) {
        return Arrays.stream(values())
            .filter(l -> l.getLabel().equalsIgnoreCase(label))
            .findFirst()
            .orElse(FlashcardQuizFilter.ALL);
    }
}
