package bot.quiz;

import bot.Constants;
import bot.entity.word.CachedWord;
import bot.service.WordCacheService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class QuestionFactory {

    private final WordCacheService wordsService;

    @Autowired
    public QuestionFactory(WordCacheService wordsService) {
        this.wordsService = wordsService;
    }

    public Question<MessageEmbed> createFlashcardQuestion(int id,
                                                          String word) {
        final Question<MessageEmbed> question = new Question<>(id);

        // Construct question embed
        EmbedBuilder questionEmbed = new EmbedBuilder();
        questionEmbed.setAuthor("Question #" + id);
        questionEmbed.setTitle("What is the definition of the word \"" + word + "\"? 🤔");
        questionEmbed.addField("", "Try to guess the meaning of the" +
            " word and after you think you got it, click on the" +
            " **Reveal Answer** below.", false);
        questionEmbed.setColor(Constants.EMBED_COLOR);
        question.setQuestion(questionEmbed.build());

        // Construct answer embed
        EmbedBuilder answerEmbed = new EmbedBuilder();
        answerEmbed.setAuthor("Question #" + id);
        answerEmbed.setTitle("📖 The definition of \"" + word + "\" is:");

        CachedWord cachedWord = wordsService.findWord(word);
        Optional<CachedWord.Definition> definitionOptional = wordsService.getDefinitionByIndex(word, id);

        if (definitionOptional.isEmpty()) {
            answerEmbed.addField("", "Well, this is awkward, but there is no definition!", true);
            question.setAnswer(answerEmbed.build());
            return question;
        }

        CachedWord.Definition definition = definitionOptional.get();
        answerEmbed.addField(definition.getPartOfSpeech(),
            "> " + definition.getDefinition(), true);

        answerEmbed.addField("", "On a scale of 0️⃣ to 5️⃣, click on the buttons below to" +
            " indicate how well you guessed the definition of the word.\n\n" +
            "- 0️⃣: *Your mind completely went blank over the definition.*\n" +
            "- 5️⃣: *You remembered the word's definition perfectly!*", false);
        question.setAnswer(answerEmbed.build());
        return question;
    }
}
