package bot.cmd;

import bot.App;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PingVoiceChatCommand extends BotCommand {

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

        if (userVcChannel == null) {
            event.reply("You need to be in a voice channel to execute this command! :loud_sound:")
                .setEphemeral(true)
                .queue();
            return;
        }

        Optional<PermissionOverride> permOverride = userVcChannel.asVoiceChannel().getPermissionOverrides().stream()
            .filter(po -> po.getRole().equals(guild.getPublicRole())).findFirst();
        boolean isPublic = permOverride.isEmpty() || !permOverride.get().getDenied().contains(Permission.VOICE_CONNECT);

        if (!isPublic) {
            event.reply("You need to be in a public voice channel to execute this command! :unlock:")
                .setEphemeral(true)
                .queue();
            return;
        }

        Role vcRole = Objects.requireNonNull(
            event.getGuild()).getRoleById(App.getenv("ROLE_ID_VOICE_CHAT")
        );

        if (vcRole == null) {
            App.logger.warn("ROLE_ID_VOICE_CHAT was not found");
            return;
        }

        event.reply(vcRole.getAsMention() + " Join "
            + event.getUser().getAsMention() + " for a conversation in "
            + userVcChannel.getAsMention() + "! :microphone2:")
            .queue();
    }

    public static Map<String, Integer> getUsages() {
        return usages;
    }
}
