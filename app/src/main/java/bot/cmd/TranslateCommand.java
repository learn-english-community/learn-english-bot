package bot.cmd;

import bot.App;
import bot.Constants;
import bot.util.Languages;
import com.deepl.api.TextResult;
import java.util.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

/**
 * Represents the "translate" command.
 *
 * <p>It allows users to translate a text into a different language, with the help of the DeepL API.
 */
@Component
public class TranslateCommand extends BotCommand {

    /**
     * Keeps track of the number of command usages from different users.
     *
     * <p>The key represents a String representation of the user's ID, whereas the value represents
     * the amount of uses the user has made in a day. A cron task is scheduled to reset these limits
     * while the bot is running. This is done to prevent people from spamming the command.
     */
    private static final Map<String, Integer> usages = new HashMap<>();

    public TranslateCommand() {
        super("translate", "Translate your text in to a different language!", true);

        // Add target argument along with options
        CommandArgument targetArg =
                new CommandArgument(
                        OptionType.STRING,
                        "target",
                        "The language you wish to translate to",
                        true,
                        true);

        try {
            Languages.languages = App.getTranslator().getTargetLanguages();
            Languages.languages.forEach(language -> targetArg.addOption(language.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        getArguments().put(targetArg.getName(), targetArg);
        getArguments()
                .put(
                        "text",
                        new CommandArgument(
                                OptionType.STRING, "text", "The text to translate", true, false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        final Map<String, String> args = new HashMap<>();
        String userId = event.getUser().getId();
        Integer userUsages = usages.get(userId);

        if (userUsages != null && userUsages >= Constants.MAX_TRANSLATION_USAGES) {
            event.reply(
                            "You have reached the maximum daily amount of translations."
                                    + " Try again in some hours!")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        // If there is an empty argument, let the user know.
        this.getArguments()
                .forEach(
                        (name, option) -> {
                            OptionMapping om = event.getOption(name);

                            if (om == null) {
                                event.reply("Missing arguments. Please try again!")
                                        .setEphemeral(true)
                                        .queue();
                                return;
                            }
                            args.put(option.getName(), om.getAsString());
                        });

        String targetLang = Languages.getCodeFromDisplay(args.get("target"));
        String text = args.get("text");

        // Check if length exceeds maximum translation length.
        if (text.length() > Constants.MAX_TRANSLATION_LENGTH) {
            event.reply(
                            "You can not translate a text that's longer than "
                                    + Constants.MAX_TRANSLATION_LENGTH
                                    + " characters! Try something shorter.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Cancel command if the user attempts to ping one of the roles below.
        if (text.contains("@everyone") || text.contains("@here")) {
            event.reply("Nice try... but we took care of that! :innocent:")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        event.deferReply().queue();

        // Finally, attempt to make the translation.
        try {
            TextResult result = App.translator.translateText(text, null, targetLang);
            Member member = Objects.requireNonNull(event.getMember());
            String displayName =
                    member.getNickname() != null ? member.getNickname() : event.getUser().getName();

            // If this is on a guild
            if (event.getChannelType().equals(ChannelType.TEXT)) {
                Member member = Objects.requireNonNull(event.getMember());
                displayName = member.getNickname() != null
                    ? member.getNickname() : event.getUser().getName();
            }

            usages.put(userId, usages.containsKey(userId) ? usages.get(userId) + 1 : 1);
            event.getHook()
                    .editOriginal(
                            Languages.getEmojiFromCode(targetLang)
                                    + " **"
                                    + displayName
                                    + "**: "
                                    + result.getText())
                    .queue();
        } catch (Exception e) {
            event.getHook().setEphemeral(true);
            event.getHook().editOriginal("I was not able to translate that :frowning2:").queue();
            e.printStackTrace();
        }
    }

    /**
     * @return A map that keeps track of the amount of uses per user.
     */
    public static Map<String, Integer> getUsages() {
        return usages;
    }
}
