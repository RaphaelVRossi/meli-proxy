package br.com.rrossi.proxy.exception;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */
public class QuotaException extends RuntimeException {
    public QuotaException() {
        super();
    }

    public QuotaException(String message) {
        super(message);
    }
}
