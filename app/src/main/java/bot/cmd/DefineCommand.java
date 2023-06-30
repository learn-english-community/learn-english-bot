package bot.cmd;

import bot.App;
import bot.Dictionary;
import bot.entity.User;
import bot.entity.word.Word;
import bot.service.UserService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collections;
import java.util.Optional;

/**
 * Represents the "define" command.
 */
@Log4j2
public class DefineCommand extends BotCommand {

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
            UserService userService = new UserService();
            String discordId = event.getUser().getId();

            // If the user was not found
            if (!userService.userExists(discordId)) {
                User user = User.builder()
                    .discordId(event.getUser().getId())
                    .build();

                userService.createUser(user);
                log.debug("I created a user");
            }
            else log.debug("I did not create a user");
        }
    }
}

