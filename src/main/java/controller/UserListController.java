package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import template.DynamicRenderer;

import java.util.HashMap;
import java.util.Map;

public class UserListController extends Controller {
    private static final DynamicRenderer renderer;

    static {
        renderer = new DynamicRenderer("/templates", ".html");
        renderer.init();
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.isLogin()) {
            handle(response);
            return;
        }

        response.redirect("/user/login.html");
    }

    private void handle(HttpResponse response) {
        Map<String, Object> users = new HashMap<>();
        users.put("users", DataBase.findAll());
        String profilePage = renderer.apply(users);
        response.setBody(profilePage.getBytes());
    }
}
