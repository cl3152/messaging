package com.len.messaging.exception;


import java.io.Serial;
/** Im Original eine Runtimeexception
 * */
public class AussteuernException extends Exception {

    public AussteuernException(String message) {
        super(message);
    }

    public AussteuernException(String message, Throwable cause) {
        super(message, cause);
    }
}
