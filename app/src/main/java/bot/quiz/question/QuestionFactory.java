package bot.quiz.question;

import bot.Constants;
import bot.entity.word.CachedWord;
import bot.entity.word.JournalWord;
import bot.service.WordCacheService;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionFactory {

    private final WordCacheService wordsService;

    @Autowired
    public QuestionFactory(WordCacheService wordsService) {
        this.wordsService = wordsService;
    }

    public FlashcardQuestion createFlashcardQuestion(int id, JournalWord word) {
        final FlashcardQuestion question = new FlashcardQuestion(id, word);

        // Construct answer embed
        EmbedBuilder answerEmbed = new EmbedBuilder();
        answerEmbed.setAuthor("Question #" + id);
        answerEmbed.setTitle("📖 The definition of \"" + word + "\" is:");

        Optional<CachedWord.Definition> definitionOptional =
            wordsService.getDefinitionByIndex(word.getWord(), word.getDefinitionIndex());

        if (definitionOptional.isEmpty()) {
            answerEmbed.addField("", "Well, this is awkward, but there is no definition!", true);
            question.setAnswer(answerEmbed.build());
            return question;
        }

        CachedWord.Definition definition = definitionOptional.get();
        answerEmbed.addField(definition.getPartOfSpeech(), "> " + definition.getDefinition(), true);

        answerEmbed.addField(
            "",
            "On a scale of 1️⃣ to 5️⃣, click on the buttons below to"
                + " indicate how well you guessed the definition of the word.\n\n"
                + "- 1️⃣: *Your mind completely went blank over the definition.*\n"
                + "- 5️⃣: *You remembered the word's definition perfectly!*",
            false);

        // Construct question embed
        EmbedBuilder questionEmbed = new EmbedBuilder();
        String part = definition.getPartOfSpeech();
        questionEmbed.setAuthor("Question #" + id);
        questionEmbed.setTitle("What is the definition of the word \"" + word + "\" (" + part + ")? 🤔");
        questionEmbed.addField(
                "",
                "Try to guess the meaning of the"
                        + " word and after you think you got it, click on the"
                        + " **Reveal Answer** below.",
                false);
        questionEmbed.setColor(Constants.EMBED_COLOR);
        question.setQuestion(questionEmbed.build());

        question.setAnswer(answerEmbed.build());
        return question;
    }
}
