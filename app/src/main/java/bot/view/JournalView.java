package bot.view;

import bot.entity.JournalDisplay;
import bot.view.paginator.JournalPaginator;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class JournalView {

    private final JournalPaginator paginator;

    @Autowired
    public JournalView(JournalPaginator paginator) {
        this.paginator = paginator;
    }

    public JournalDisplay getUserJournalDisplay(User user, int page) {
        List<MessageEmbed> embeds = paginator.getPage(user, page);
        JournalDisplay.JournalDisplayBuilder journal = JournalDisplay.builder();

        if (embeds.isEmpty()) {
            journal.errorMessage("Your journal is empty! Use /define to store words. ⭐️");
            return journal.build();
        }

        journal.words(embeds);
        journal.message("**" + user.getName() + "'s Journal**:");

        return journal.build();
    }
}
