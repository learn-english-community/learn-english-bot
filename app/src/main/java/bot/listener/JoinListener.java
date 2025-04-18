package bot.listener;

import bot.Constants;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.Level;

/**
 * @author Eaky0 A listener called whenever a user joins the guild. Mainly used for assigning
 *     initial roles such as 'Member'.
 */
@Log4j2
public class JoinListener extends ListenerAdapter {
    /**
     * @param event The event that takes place when a User/Member joins the guild.
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            User user = event.getUser();
            Role role = event.getGuild().getRoleById(Constants.MEMBER_ROLE_ID);
            if (role != null) {
                // Assign the member role to the new user.
                event.getGuild().addRoleToMember(user, role).queue();

                String logNote = "Successfully assigned the member role to " + user.getName() + "!";
                log.log(Level.INFO, logNote);

            } else {
                throw new NullPointerException();
            }

        } catch (NullPointerException e) {
            log.error("Role doesn't exist!");
        }
    }
}
