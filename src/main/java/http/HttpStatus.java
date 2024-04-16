package http;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    FOUND(302, "FOUND"),
    NOT_FOUND(404, "NOT_FOUND");


    private final int status_code;
    private final String status;

    HttpStatus(int status_code, String status) {
        this.status_code = status_code;
        this.status = status;
    }

    public int getStatusCode() {
        return status_code;
    }

    public String getStatus() {
        return status;
    }
}
