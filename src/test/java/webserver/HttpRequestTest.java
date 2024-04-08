package webserver;

import db.DataBase;
import model.User;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import utils.FileIoUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class HttpRequestTest {

    private static Thread server;

    @BeforeAll
    static void runServer() {
        server = new Thread(() -> {
            try {
                WebApplicationServer.main(new String[]{});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        server.start();
    }

    @AfterAll
    static void stopServer() throws InterruptedException {
        server.interrupt();
        server.join();
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        DataBase.clear();
        Thread.sleep(5000);
    }

    @Test
    void request_resttemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("templates의 `/index.html` 파일 응답")
    @Test
    void request_indexhtml() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/index.html", String.class);

        String indexFile = new String(FileIoUtils.loadFileFromClasspath("templates/index.html"));
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isEqualTo(indexFile)
        );
    }

    @DisplayName("static의 `css` 파일 응답")
    @Test
    void request_css() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/./css/style.css", String.class);

        String cssFile = new String(FileIoUtils.loadFileFromClasspath("static/css/styles.css"));
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isEqualTo(cssFile)
        );
    }

    @DisplayName("Query String 파싱하여 유저 객체 생성 및 Database에 저장")
    @Test
    void request_queryString() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/user/create?userId=cu&password=password&name=%EC%9D%B4%EB%8F%99%EA%B7%9C&email=brainbackdoor%40gmail.com", String.class);

        User user = new User("cu", "password", "이동규", "brainbackdoor@gmail.com");
        User cu = DataBase.findUserById("cu");

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(cu).isEqualTo(user)
        );
    }

    @DisplayName("POST form 요청 바디 파싱하여 유저 객체 생성 및 Database에 저장")
    @Test
    void request_post() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("userId=cu&password=password&name=%EC%9D%B4%EB%8F%99%EA%B7%9C&email=brainbackdoor%40gmail.com", headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/user/create", HttpMethod.POST, request, String.class);

        User user = new User("cu", "password", "이동규", "brainbackdoor@gmail.com");
        User cu = DataBase.findUserById("cu");

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(cu).isEqualTo(user)
        );
    }

    @DisplayName("회원가입 완료 후 `index.html`로 Redirect")
    @Test
    void request_redirect() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("userId=cu&password=password&name=%EC%9D%B4%EB%8F%99%EA%B7%9C&email=brainbackdoor%40gmail.com", headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/user/create", HttpMethod.POST, request, String.class);

        User user = new User("cu", "password", "이동규", "brainbackdoor@gmail.com");
        User cu = DataBase.findUserById("cu");

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(cu).isEqualTo(user)
        );
    }
}
