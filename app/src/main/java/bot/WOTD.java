package bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public class WOTD {
    private static WOTD wotd;

    private WOTD() {}

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

    public static WOTD getWotd() {
        if (wotd == null)
            wotd = new WOTD();

        return wotd;
    }
}
