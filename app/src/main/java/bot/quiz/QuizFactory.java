package bot.quiz;

import bot.quiz.question.Question;
import bot.quiz.question.QuestionFactory;
import bot.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class QuizFactory {

    private final UserService userService;
    private final QuestionFactory questionFactory;

    private final HashMap<User, Quiz<?>> quizMap = new HashMap<>();

    @Autowired
    private QuizFactory(UserService userService, QuestionFactory questionFactory) {
        this.userService = userService;
        this.questionFactory = questionFactory;
    }

    public FlashcardQuiz getFlashcardQuiz(
            User discordUser,
            PrivateChannel channel,
            FlashcardQuizFilter filter,
            ModalMapping metadata) {
        Map<Integer, Question<MessageEmbed>> questions = new HashMap<>();
        bot.entity.User user = userService.getUser(discordUser.getId());
        AtomicInteger counter = new AtomicInteger(1);

        filter.getFilterProcessor()
                .apply(user, metadata)
                .forEach(
                        word -> {
                            Question<MessageEmbed> question =
                                    questionFactory.createFlashcardQuestion(counter.get(), word);
                            questions.put(counter.getAndIncrement(), question);
                        });

        return new FlashcardQuiz(discordUser, userService, channel, questions);
    }
}
