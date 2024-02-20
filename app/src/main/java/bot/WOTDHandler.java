package bot;

import bot.service.WordService;
import bot.view.WordView;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class WOTDHandler {

    /** Holds an instance of the WOTDHandler. */
    private static WOTDHandler wotd;

    private final WordService wordService;

    @Autowired
    private WOTDHandler(WordService wordService) {
        this.wordService = wordService;
    }

    /**
     * Primarily used by the cron scheduler, and it acts as the executing method once triggered.
     *
     * <p>Upon execution, it gets a random word from the dictionary using the right API, and it
     * announces it to the WOTD channel.
     *
     * @param guild A reference of the guild to perform this to
     */
    public void executeCron(Guild guild) {
        TextChannel chatChannel = guild.getTextChannelById(App.getEnv("CHANNEL_ID_WOTD"));

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
                        .getRoleById(App.getEnv("ROLE_ID_WOTD"));

        if (role == null) {
            log.warn("ROLE_ID_WOTD was not found");
            return;
        }
        WordView wordView = new WordView();
        EmbedBuilder embed = wordView.getDefinitionEmbed(wordService.getRandomWordFromAPI());
        textChannel.sendMessage(role.getAsMention()).setEmbeds(embed.build()).queue();
    }
}
