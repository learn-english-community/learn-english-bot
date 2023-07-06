package bot.cmd;

import bot.entity.User;
import bot.entity.word.CachedWord;
import bot.entity.word.JournalWord;
import bot.service.UserService;
import bot.service.WordCacheService;
import bot.view.WordCacheView;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the "define" command.
 */
@Log4j2
@Component
public class DefineCommand extends BotCommand {

    private static final Pattern pattern = Pattern.compile("^(.*?)\\s–");

    @Autowired
    private UserService userService;

    @Autowired
    private WordCacheService wordCacheService;

    public DefineCommand() {
        super("define", "Get a word definition!", true);

        getArguments().put("word", new CommandArgument(
            OptionType.STRING,
            "word",
            "The word you want to get the definition of!",
            true, false
        ));
    }
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String word = event.getOption("word").getAsString();
        WordCacheView wordCacheView = new WordCacheView();
        Optional<CachedWord> cachedWord = wordCacheService.getWordFromCacheOrAPI(word);

        if (cachedWord.isEmpty()) {
            event.reply("I am not familiar with that word! :pensive:").setEphemeral(true)
                .queue();
            return;
        }

        EmbedBuilder embedBuilder = wordCacheView.getDefinitionEmbed(cachedWord.get());
        event.replyEmbeds(embedBuilder.build())
            .addActionRow(
                Button.secondary("save", "Save").withEmoji(Emoji.fromUnicode("💾")))
            .queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("save")) {
            String discordId = event.getUser().getId();
            Optional<MessageEmbed> embedOptional = event.getMessage().getEmbeds()
                .stream()
                .findFirst();
            if (embedOptional.isEmpty()) return;

            String description = embedOptional.get().getDescription();
            Matcher matcher = pattern.matcher(description);
            String word = matcher.find() ? matcher.group(1).trim() : "";

            JournalWord journalWord = JournalWord.builder()
                .word(word)
                .timeAdded(System.currentTimeMillis())
                .repetitions(0)
                .interval(1)
                .easiness(2.5f)
                .quality(-1) // -1 since this word has never been graded before
                .build();

            // If the user was not found
            if (!userService.userExists(discordId)) {
                User user = User.builder()
                    .discordId(event.getUser().getId())
                    .words(Collections.singletonList(journalWord))
                    .build();

                userService.createUser(user);

                event.reply("I saved the word `" + word + "` to your journal! :blue_book:")
                    .setEphemeral(true)
                    .queue();
            }

            if (userService.hasJournalWord(discordId, word)) {
                event.reply("The word `" + word + "` is already in your journal! :star:")
                    .setEphemeral(true)
                    .queue();
                return;
            }

            userService.addWord(discordId, journalWord);

            event.reply("I saved the word `" + word + "` to your journal! :blue_book:")
                .setEphemeral(true)
                .queue();
        }
    }
}

/* From https://stackoverflow.com/questions/49047159/spaced-repetition-algorithm-from-supermemo-sm-2/49047160#49047160
private void calculateSuperMemo2Algorithm(FlashCard card, int quality) {

    if (quality < 0 || quality > 5) {
        // throw error here or ensure elsewhere that quality is always within 0-5
    }

    // retrieve the stored values (default values if new cards)
    int repetitions = card.getRepetitions();
    float easiness = card.getEasinessFactor();
    int interval = card.getInterval();

    // easiness factor
    easiness = (float) Math.max(1.3, easiness + 0.1 - (5.0 - quality) * (0.08 + (5.0 - quality) * 0.02));

    // repetitions
    if (quality < 3) {
        repetitions = 0;
    } else {
        repetitions += 1;
    }

    // interval
    if (repetitions <= 1) {
        interval = 1;
    } else if (repetitions == 2) {
        interval = 6;
    } else {
        interval = Math.round(interval * easiness);
    }

    // next practice
    int millisecondsInDay = 60 * 60 * 24 * 1000;
    long now = System.currentTimeMillis();
    long nextPracticeDate = now + millisecondsInDay*interval;

    // Store the nextPracticeDate in the database
    // ...
}*/
