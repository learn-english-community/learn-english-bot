package bot.cmd;

import bot.Dictionary;
import bot.entity.User;
import bot.entity.word.JournalWord;
import bot.service.UserService;
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

    public DefineCommand() {
        super("define", "Get a word definition!");

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
        EmbedBuilder embedBuilder = Dictionary.getDictionary().getWordDefinition(word);

        if (embedBuilder == null) {
            event.reply("I am not familiar with that word! :pensive:").setEphemeral(true)
                .queue();
            return;
        }

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

            // If the user was not found
            if (!userService.userExists(discordId)) {
                JournalWord journalWord = JournalWord.builder()
                    .word(word)
                    .timeAdded(System.currentTimeMillis())
                    .build();

                User user = User.builder()
                    .discordId(event.getUser().getId())
                    .words(Collections.singletonList(journalWord))
                    .build();

                userService.createUser(user);
            }

            event.reply("I saved the word `" + word + "` to your journal! :blue_book:")
                .setEphemeral(true)
                .queue();
        }
    }
}

