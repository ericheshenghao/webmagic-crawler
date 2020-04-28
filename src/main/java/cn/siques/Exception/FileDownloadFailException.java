package cn.siques.Exception;

public class FileDownloadFailException extends RuntimeException {
    public FileDownloadFailException() {
        super();
    }

    public FileDownloadFailException(String message) {
        super(message);
    }
}
