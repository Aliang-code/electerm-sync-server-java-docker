package ElectermSync;

public class WriteResult {
    public final String message;
    public final int statusCode;

    WriteResult(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
