package bot.repository;

import bot.App;
import bot.entity.User;
import dev.morphia.DeleteOptions;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.morphia.query.filters.Filters.*;

/**
 * User repository for user entities.
 */
@Repository
public class UserRepository {

    public <S extends User> @NotNull S save(@NotNull S entity) {
        App.getDatastore().save(entity);
        return entity;
    }

    public <S extends User> @NotNull Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    public @NotNull Optional<User> findById(@NotNull String string) {
        return App.getDatastore().find(User.class).filter(
            eq("discordId", string)
        ).stream().findFirst();
    }

    public boolean existsById(@NotNull String string) {
        return findById(string).isPresent();
    }

    public @NotNull Iterable<User> findAll() {
        return App.getDatastore().find(User.class)
            .stream()
            .collect(Collectors.toList());
    }

    public @NotNull Iterable<User> findAllById(Iterable<String> strings) {
        List<User> results = new ArrayList<>();

        strings.iterator().forEachRemaining(item ->
            findById(item).ifPresent(results::add)
        );

        return results;
    }

    public long count() {
        return App.getDatastore().find(User.class).count();
    }

    public void deleteById(@NotNull String string) {
        App.getDatastore().find(User.class).filter(
            eq("discordId", string)
        ).delete();
    }

    public void delete(@NotNull User entity) {
        App.getDatastore().delete(entity);
    }

    public void deleteAll(@NotNull Iterable<? extends User> entities) {
        entities.forEach(this::delete);
    }

    public void deleteAll() {
        App.getDatastore().delete(User.class, new DeleteOptions().multi(true));
    }
}
