package br.com.rrossi.proxy.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 11/04/2021.
 */
@Data
@JsonPropertyOrder({"message", "error", "status", "cause"})
public class ProxyExceptionModel {
    String message;
    String error;
    Integer status;
    String[] cause;
}
