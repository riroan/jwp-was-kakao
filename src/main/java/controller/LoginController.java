package controller;

import db.DataBase;
import http.HttpQueryParams;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.util.UUID;

public class LoginController extends Controller {

    protected void doPost(HttpRequest request, HttpResponse response) {
        String body = (String) request.getBody();
        HttpQueryParams queryParams = HttpQueryParams.parseParams(body);
        handleLogin(queryParams, response);

    }

    private void handleLogin(HttpQueryParams queryParams, HttpResponse response) {
        String userId = queryParams.get("userId");
        String password = queryParams.get("password");

        User existUser = DataBase.findUserById(userId);

        if (existUser == null || !existUser.match(userId, password)) {
            response.redirect("/user/login_failed.html");
            return;
        }

        UUID uuid = UUID.randomUUID();
        response.addCookie("JSESSIONID", uuid.toString());
        response.addCookie("logined", "true");
        response.addCookie("Path", "/");

        response.redirect("/index.html");
    }
}