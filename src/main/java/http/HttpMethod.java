package http;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String label;

    HttpMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
