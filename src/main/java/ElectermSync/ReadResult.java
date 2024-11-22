package ElectermSync;

public class ReadResult {
    public final String fileData;
    public final int statusCode;

    ReadResult(String fileData, int statusCode) {
        this.fileData = fileData;
        this.statusCode = statusCode;
    }
}
