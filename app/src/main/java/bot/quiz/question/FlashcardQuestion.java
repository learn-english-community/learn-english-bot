package bot.quiz.question;

import bot.entity.word.JournalWord;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Getter
public class FlashcardQuestion extends Question<MessageEmbed> {
    private final JournalWord word;

    public FlashcardQuestion(int id, JournalWord word) {
        super(id);
        this.word = word;
    }
}
