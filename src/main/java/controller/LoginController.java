package controller;

import db.DataBase;
import http.HttpQueryParams;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import session.Session;
import session.SessionManager;

import java.util.UUID;

public class LoginController extends Controller {
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/user/login.html";
    private static final String LOGIN_FAILED_PAGE = "/user/login_failed.html";

    protected void doGet(HttpRequest request, HttpResponse response) {
        String sessionId = request.getSessionId();

        Session session = SessionManager.findSession(sessionId);
        System.out.println(session == null);

        if (session != null) {
            response.redirect(INDEX_PAGE);
            return;
        }
        response.redirect(LOGIN_PAGE);
    }

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
            response.redirect(LOGIN_FAILED_PAGE);
            return;
        }

        String uuid = UUID.randomUUID().toString();
        response.addCookie("JSESSIONID", uuid);
        response.addCookie("logined", "true");
        response.addCookie("Path", "/");

        Session session = new Session(uuid);
        SessionManager.add(session);

        response.redirect(INDEX_PAGE);
    }
}
