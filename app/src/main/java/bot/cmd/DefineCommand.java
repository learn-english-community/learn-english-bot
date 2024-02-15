package bot.cmd;

import bot.entity.User;
import bot.entity.word.JournalWord;
import bot.entity.word.WiktionaryWord;
import bot.service.UserService;
import bot.service.WordService;
import bot.view.WordView;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Represents the "define" command. */
@Log4j2
@Component
public class DefineCommand extends BotCommand {

    private static final Pattern pattern = Pattern.compile("^(.*?)\\s–");

    @Autowired private UserService userService;

    @Autowired private WordService wordService;

    public DefineCommand() {
        super("define", "Get a word definition!", true);

        getArguments()
                .put(
                        "word",
                        new CommandArgument(
                                OptionType.STRING,
                                "word",
                                "The word you want to get the definition of!",
                                true,
                                false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String wordStr = event.getOption("word").getAsString();
        WiktionaryWord word = wordService.findWord(wordStr).orElse(null);
        WordView wordView = new WordView();
        ChannelType channelType = event.getChannel().getType();

        if (word == null || wordStr.isEmpty()) {
            event.reply("I am not familiar with that word! :pensive:").setEphemeral(true).queue();
            return;
        }

        boolean ephemeral = !channelType.isGuild();
        EmbedBuilder embedBuilder = wordView.getDefinitionEmbed(word);
        event.replyEmbeds(embedBuilder.build())
                .addActionRow(Button.secondary("save", "Save").withEmoji(Emoji.fromUnicode("💾")))
                .setEphemeral(ephemeral)
                .queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("save")) {
            Optional<MessageEmbed> embedOptional =
                    event.getMessage().getEmbeds().stream().findFirst();
            if (embedOptional.isEmpty()) return;
            MessageEmbed embed = embedOptional.get();

            String description = embed.getDescription();
            Matcher matcher = pattern.matcher(description);
            String word = matcher.find() ? matcher.group(1).trim() : "";
            StringSelectMenu.Builder stringSelectMenu =
                    StringSelectMenu.create("choose-definition:" + word);
            AtomicInteger counter = new AtomicInteger(1);

            embed.getFields()
                    .forEach(
                            field -> {
                                String fieldName = field.getName();
                                String fieldValue = field.getValue();

                                if (fieldName != null && fieldValue != null) {
                                    String content =
                                            fieldValue
                                                    .split("\\r?\\n")[0]
                                                    .substring(2)
                                                    .replace("*", "");

                                    String value = StringUtils.abbreviate(content, 100);
                                    int emojiUnicode = 0x1f1e6 + fieldName.charAt(0) - ((int) 'a');

                                    stringSelectMenu.addOption(
                                            "#" + counter.get(),
                                            String.valueOf(counter.get() - 1),
                                            value,
                                            Emoji.fromUnicode(
                                                    "U+"
                                                            + Integer.toHexString(emojiUnicode)
                                                                    .toUpperCase()));

                                    counter.incrementAndGet();
                                }
                            });

            event.reply("Pick the definition you want to save for the word `" + word + "`:")
                    .setEphemeral(true)
                    .addActionRow(stringSelectMenu.build())
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().startsWith("choose-definition")) {
            String word = event.getComponentId().split(":")[1];
            String selected = event.getValues().get(0); // the values the user selected
            int selectedNo = Integer.parseInt(selected);
            String discordId = event.getUser().getId();
            String numberDisplay = "**#" + (selectedNo + 1) + "**";
            User user = userService.getUser(discordId);
            WiktionaryWord wiktionaryWord = wordService.findWord(word).orElse(null);

            if (wiktionaryWord == null) {
                log.error("Got a null Wiktionary response after picking definitionIndex");
                event.reply("Something went wrong. Please try again.").setEphemeral(true).queue();
                return;
            }

            JournalWord journalWord =
                    JournalWord.builder()
                            .word(word)
                            .timeAdded(System.currentTimeMillis())
                            .repetitions(0)
                            .interval(1)
                            .easiness(2.5f)
                            .definitionIndex(selectedNo)
                            .savedDefinition(wiktionaryWord.getDefinitions().get(selectedNo))
                            .quality(-1) // -1 since this word has never been graded before
                            .build();

            if (userService.hasJournalWord(discordId, word, journalWord.getDefinitionIndex())) {
                event.reply(
                                "The word `"
                                        + word
                                        + "` is already in your journal with"
                                        + " the "
                                        + numberDisplay
                                        + " definition! :star:")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            userService.addWord(discordId, journalWord);
            userService.updateWordSuperMemo(
                    discordId, journalWord.getWord(), journalWord.getDefinitionIndex(), 0);

            event.reply(
                            "I saved the word `"
                                    + word
                                    + "` to your journal with"
                                    + " the "
                                    + numberDisplay
                                    + " definition! :blue_book:")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
