package bot.quiz;

import bot.service.UserService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class QuizFactory {

    private final UserService userService;

    private final HashMap<User, Quiz<?>> quizMap = new HashMap<>();

    @Autowired
    private QuizFactory(UserService userService) {
        this.userService = userService;
    }

    public FlashcardQuiz getFlashcardQuiz(User user, Map<Integer, Question<MessageEmbed>> questions) {
        return new FlashcardQuiz(user, userService, questions);
    }
}
