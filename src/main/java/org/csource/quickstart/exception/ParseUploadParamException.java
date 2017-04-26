package org.csource.quickstart.exception;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 
 */
@SuppressWarnings("serial")
public class ParseUploadParamException extends Exception {

    private int code;
    private String message;

    public ParseUploadParamException() {
        super();
    }

    public ParseUploadParamException(int code, String cause) {
        this.code = code;
        this.message = cause;
    }

    public ParseUploadParamException(String message) {
        super(message);
    }

    public ParseUploadParamException(Throwable cause) {
        super(cause);
    }

    public ParseUploadParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}