package bot.view;

import bot.entity.JournalDisplay;
import bot.service.UserService;
import bot.view.paginator.JournalPaginator;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     */
    public JournalDisplay getUserJournalDisplay(User user, int page, int count) {
        List<MessageEmbed> embeds = paginator.getPage(user, page, count);
        JournalDisplay.JournalDisplayBuilder journal = JournalDisplay.builder();
        int wordsCount = userService.getJournalWords(user.getId()).size();
        int maxPages = (int) Math.ceil(wordsCount / (float) count);
        String pageString = (page + 1) + "/" + maxPages;

        if (embeds.isEmpty()) {
            journal.errorMessage("Your journal is empty! Use /define to store words. ⭐️");
            return journal.build();
        }

        // Reverse the current page due to the fact that we want
        // the most recent words to be to the bottom. Yes, that's
        // double reversing, but in this case, it's necessary.
        Collections.reverse(embeds);

        journal.words(embeds);
        journal.message("# " + user.getName() + "'s journal (Page " + pageString + "):");
        journal.page(page);
        journal.maxPages(maxPages);

        return journal.build();
    }
}
