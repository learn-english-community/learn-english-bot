package bot.quiz;

import bot.quiz.question.Question;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

@Getter
public abstract class Quiz<T> {

    /** Represents the person who is taking the quiz */
    protected User user;

    protected Map<Integer, Question<T>> questions;

    /** The question that the user is currently in */
    @Setter private int currentQuestionId;

    @Setter private Question<T> currentQuestion;

    public Quiz() {}

    public Quiz(
            User user,
            Map<Integer, Question<T>> questions,
            int currentQuestionId,
            Question<T> currentQuestion) {
        this.user = user;
        this.questions = questions;
        this.currentQuestionId = currentQuestionId;
        this.currentQuestion = currentQuestion;
    }

    public abstract void showQuestion(int number);

    public abstract void showAnswer(int number);

    public abstract void start();
}
