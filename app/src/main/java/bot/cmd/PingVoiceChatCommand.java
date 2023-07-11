package bot.cmd;

import bot.App;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/** Represents the "pingvc" slash command. */
public class PingVoiceChatCommand extends BotCommand {

    /**
     * Keeps track of the number of command usages from different users.
     *
     * <p>The key represents a String representation of the user's ID, whereas the value represents
     * the amount of uses the user has made in a day. A cron task is scheduled to reset these limits
     * while the bot is running. This is done to prevent people from spamming the command.
     */
    private static final Map<String, Integer> usages = new HashMap<>();

    public PingVoiceChatCommand() {
        super("pingvc", "Let other members know that you want to voice chat!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        if (member == null || guild == null) return;

        String userId = event.getUser().getId();
        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null) return;
        AudioChannelUnion userVcChannel = voiceState.getChannel();

        // If the user is not in any voice channel.
        if (userVcChannel == null) {
            event.reply("You need to be in a voice channel to execute this command! :loud_sound:")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Acquire permission override state and use it to check if the
        // voice channel that the user is in is publicly accessible.
        Optional<PermissionOverride> permOverride =
                userVcChannel.asVoiceChannel().getPermissionOverrides().stream()
                        .filter(po -> po.getRole().equals(guild.getPublicRole()))
                        .findFirst();
        boolean isPublic =
                permOverride.isEmpty()
                        || !permOverride.get().getDenied().contains(Permission.VOICE_CONNECT);

        if (!isPublic) {
            event.reply(
                            "You need to be in a public voice channel to execute this command! :unlock:")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // A handle of the @Voice Chat role.
        Role vcRole =
                Objects.requireNonNull(event.getGuild())
                        .getRoleById(App.getenv("ROLE_ID_VOICE_CHAT"));

        if (vcRole == null) {
            App.logger.warn("ROLE_ID_VOICE_CHAT was not found");
            return;
        }

        event.reply(
                        vcRole.getAsMention()
                                + " Join "
                                + event.getUser().getAsMention()
                                + " for a conversation in "
                                + userVcChannel.getAsMention()
                                + "! :microphone2:")
                .queue();
    }

    /**
     * @return A map that keeps track of the amount of uses per user.
     */
    public static Map<String, Integer> getUsages() {
        return usages;
    }
}
