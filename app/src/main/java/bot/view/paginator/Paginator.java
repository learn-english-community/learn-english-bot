package bot.view.paginator;


import net.dv8tion.jda.api.entities.User;

public abstract class Paginator<T> {

    /**
     * @return The list of pages in the paginator
     */
    public abstract T getPage(User user, int idx);

    protected void renderButtons(T t) {

    }
}
