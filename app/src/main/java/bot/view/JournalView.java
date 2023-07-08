package bot.view;

import bot.entity.JournalDisplay;
import bot.service.UserService;
import bot.view.paginator.JournalPaginator;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

// TODO (@Christolis): This is disgusting code
@Getter
@Component
public class JournalView {

    private final JournalPaginator paginator;
    private final UserService userService;

    @Autowired
    public JournalView(JournalPaginator paginator, UserService userService) {
        this.paginator = paginator;
        this.userService = userService;
    }

    /**
     * Gets a handle of the user's journal display.
     *
     * @param user The Disocrd user to get the journal display from
     * @param page The page to render
     * @param count The amount of words to include in the journal display
     * @return An instance of the journal display
     * T
     */
    public JournalDisplay getUserJournalDisplay(User user, int page, int count) {
        List<MessageEmbed> embeds = paginator.getPage(user, page, count);
        JournalDisplay.JournalDisplayBuilder journal = JournalDisplay.builder();
        int wordsCount = userService.getJournalWords(user.getId()).size();

        if (embeds.isEmpty()) {
            journal.errorMessage("Your journal is empty! Use /define to store words. ⭐️");
            return journal.build();
        }

        journal.words(embeds);
        journal.message("# " + user.getName() + "'s journal (Page " + (page + 1) + "):");
        journal.page(page);
        journal.maxPages((int) Math.ceil(wordsCount / (float) count));

        return journal.build();
    }
}
