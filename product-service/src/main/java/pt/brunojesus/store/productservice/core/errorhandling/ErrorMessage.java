package pt.brunojesus.store.productservice.core.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private final boolean error = true;
    private final Date timestamp;
    private final String message;

    public ErrorMessage(String message) {
        this.timestamp = new Date();
        this.message = message;
    }
}
