package bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

/**
 * Manages functions that involve the "word of the day" feature.
 */
public class WOTDHandler {

    /**
     * Holds an instance of the WOTDHandler.
     */
    private static WOTDHandler wotd;

    private WOTDHandler() {}

    /**
     * Attempts to announce the word of the day to the given channel.
     * <p>
     * It pings the word of the day role, so use it with caution!
     *
     * @param textChannel The text channel to send the message to
     */
    public void announce(TextChannel textChannel) {
        Role role = Objects.requireNonNull(
            textChannel.getGuild()).getRoleById(App.getenv("ROLE_ID_WOTD")
        );

        if (role == null) {
            App.logger.warn("ROLE_ID_WOTD was not found");
            return;
        }
        EmbedBuilder embed = Dictionary.getDictionary().getRandomWord();
        textChannel.sendMessage(role.getAsMention()).setEmbeds(embed.build()).queue();
    }

    /**
     * @return The only instance of the WOTDHandler
     */
    public static WOTDHandler getWotd() {
        if (wotd == null)
            wotd = new WOTDHandler();

        return wotd;
    }
}
