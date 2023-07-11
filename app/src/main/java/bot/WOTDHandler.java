package bot;

import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/** Manages functions that involve the "word of the day" feature. */
public class WOTDHandler {

    /** Holds an instance of the WOTDHandler. */
    private static WOTDHandler wotd;

    private WOTDHandler() {}

    /**
     * Primarily used by the cron scheduler, and it acts as the executing method once triggered.
     *
     * <p>Upon execution, it gets a random word from the dictionary using the right API, and it
     * announces it to the WOTD channel.
     *
     * @param guild A reference of the guild to perform this to
     */
    public void executeCron(Guild guild) {
        TextChannel chatChannel = guild.getTextChannelById(App.getenv("CHANNEL_ID_WOTD"));

        if (chatChannel != null) announce(chatChannel);
    }

    /**
     * Attempts to announce the word of the day to the given channel.
     *
     * <p>It pings the word of the day role, so use it with caution!
     *
     * @param textChannel The text channel to send the message to
     */
    public void announce(TextChannel textChannel) {
        Role role =
                Objects.requireNonNull(textChannel.getGuild())
                        .getRoleById(App.getenv("ROLE_ID_WOTD"));

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
        if (wotd == null) wotd = new WOTDHandler();

        return wotd;
    }
}
