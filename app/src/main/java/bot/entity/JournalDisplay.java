package bot.entity;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

@Getter
@Builder
public class JournalDisplay {
    private List<MessageEmbed> words;
    private String message;
    private String errorMessage;
}
