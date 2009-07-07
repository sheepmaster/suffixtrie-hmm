package de.blacksheepsoftware.gene;

import java.io.IOException;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class FileFormatException extends IOException {

    private static final long serialVersionUID = 1L;

    public FileFormatException() {
        super();
    }

    public FileFormatException(String message) {
        super(message);
    }

}
