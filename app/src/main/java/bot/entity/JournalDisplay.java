package bot.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class JournalDisplay {
    private static final String PREFIX = "journal-";
    private List<MessageEmbed> words;
    private String message;
    private String errorMessage;
    private final int maxPages;

    @Setter
    private int page;

    /**
     * Returns a list of action buttons that act as
     * handlers for the paginator feature.
     *
     * @return The list of action buttons
     */
    public List<Button> getActionButtons() {
        int maxPagesLimit = maxPages - 1;
        Button firstBtn = Button.secondary(PREFIX + "first:0", Emoji.fromUnicode("⏮"));
        Button lastBtn = Button.secondary(PREFIX + "last:" + maxPagesLimit, Emoji.fromUnicode("⏭"));

        String nextBtnId = PREFIX + "next:" + (page + 1);
        Button nextBtn = Button.secondary(nextBtnId, Emoji.fromUnicode("⏩"));

        String prevBtnId = PREFIX + "previous:" + (page - 1);
        Button prevBtn = Button.secondary(prevBtnId, Emoji.fromUnicode("⏪"));

        if (page == 0) {
            firstBtn = firstBtn.asDisabled();
            prevBtn = prevBtn.asDisabled();
        }

        if (page == maxPagesLimit) {
            lastBtn = lastBtn.asDisabled();
            nextBtn = nextBtn.asDisabled();
        }

        return new ArrayList<>(List.of(firstBtn, prevBtn, nextBtn, lastBtn));
    }
}
