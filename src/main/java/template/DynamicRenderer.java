package template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;

import java.io.IOException;
import java.util.Map;

public class DynamicRenderer {
    public static final TemplateLoader loader;
    public static Template template;

    static {
        loader = new ClassPathTemplateLoader();
    }
    private static final String PATH = "/user/list";
    private final String prefix;
    private final String suffix;

    public DynamicRenderer(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void init() {
        try {
            loader.setPrefix(prefix);
            loader.setSuffix(suffix);
            Handlebars handlebars = new Handlebars(loader);
            template = handlebars.compile(PATH);
        } catch (IOException e) {
            // do something
        }
    }

    public String apply(Map<String, Object> users) {
        try {
            users.put("users", DataBase.findAll());
            return template.apply(users);
        } catch (IOException e) {
            // do something
        }
        return "";
    }
}
