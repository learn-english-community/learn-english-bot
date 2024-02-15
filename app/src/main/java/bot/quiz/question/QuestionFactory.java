package bot.quiz.question;

import bot.Constants;
import bot.entity.word.JournalWord;
import bot.entity.word.WiktionaryWord;
import bot.entity.word.WordDefinition;
import bot.service.WordService;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionFactory {

    private final WordService wordsService;

    @Autowired
    public QuestionFactory(WordService wordsService) {
        this.wordsService = wordsService;
    }

    public FlashcardQuestion createFlashcardQuestion(int id, JournalWord journalWord) {
        final FlashcardQuestion question = new FlashcardQuestion(id, journalWord);

        // Construct answer embed
        EmbedBuilder answerEmbed = new EmbedBuilder();
        answerEmbed.setAuthor("Question #" + id);
        answerEmbed.setTitle("📖 The definition of \"" + journalWord + "\" is:");
        answerEmbed.setColor(Constants.EMBED_COLOR);

        WiktionaryWord wiktionaryWord = wordsService.findWord(journalWord.getWord()).orElse(null);

        if (wiktionaryWord == null) {
            answerEmbed.addField("", "Well, this is awkward, but there is no definition!", true);
            question.setAnswer(answerEmbed.build());
            return question;
        }

        int definitionIdx = journalWord.getDefinitionIndex();
        WordDefinition definition = wiktionaryWord.getDefinitions().get(definitionIdx);
        String definitionText = definition.getText().get(journalWord.getTextIndex());

        answerEmbed.addField(definition.getPartOfSpeech(), "> " + definitionText, true);

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
        questionEmbed.setTitle(
                "What is the definition of the word \"" + journalWord + "\" (" + part + ")? 🤔");
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
