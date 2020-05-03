package tm.fantom.simpledrawer.net;

public class HttpException extends Exception {

    public final int responseCode;

    public final String errorMessage;

    public HttpException(int responseCode, String errorMessage) {
        super(responseCode + " " + errorMessage);
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
    }
}
