package stubidp.stubidp.views;

import io.dropwizard.views.View;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class ErrorPageView extends View {

    public ErrorPageView() {
        super("errorPage.ftl", StandardCharsets.UTF_8);
    }

    public ErrorPageView(String templateName, Charset charset) {
        super(templateName, charset);
    }

    public String getReaction() {
        final List<String> reactions = List.of("😧","😮","😢","😭","👎","😶","🙃");
        final int reaction = new Random().nextInt(reactions.size());
        return reactions.get(reaction);
    }
}
