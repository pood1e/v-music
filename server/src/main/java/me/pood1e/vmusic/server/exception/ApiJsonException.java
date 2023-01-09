package me.pood1e.vmusic.server.exception;

/**
 * @author pood1e
 */
public class ApiJsonException extends RuntimeException {
    public ApiJsonException(String message) {
        super(message);
    }
}
