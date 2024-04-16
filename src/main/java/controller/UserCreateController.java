package controller;

import db.DataBase;
import http.HttpQueryParams;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class UserCreateController extends Controller {

    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpQueryParams queryParams = request.getQueryParams();
        handleUserCreate(queryParams, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        String body = (String) request.getBody();
        HttpQueryParams queryParams = HttpQueryParams.parseParams(body);

        handleUserCreate(queryParams, response);
    }

    private void handleUserCreate(HttpQueryParams queryParams, HttpResponse response) {
        String userId = queryParams.get("userId");
        String password = queryParams.get("password");
        String name = queryParams.get("name");
        String email = queryParams.get("email");
        User user = new User(userId, password, name, email);

        DataBase.addUser(user);

        response.redirect("/index.html");
    }
}
