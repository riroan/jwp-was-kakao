package controller;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserListController extends Controller {
    protected void doGet(HttpRequest request, HttpResponse response) {
        System.out.println(request.isLogin());
        if (request.isLogin()) {
            handle(response);
            return;
        }

        response.redirect("/user/login.html");
    }

    private void handle(HttpResponse response) {
        try {
            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix("/templates");
            loader.setSuffix(".html");
            Handlebars handlebars = new Handlebars(loader);
            Template template = handlebars.compile("/user/list");
            Map<String, Object> users = new HashMap<>();
            users.put("users", DataBase.findAll());
            String profilePage = template.apply(users);
            response.setBody(profilePage.getBytes());
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}
