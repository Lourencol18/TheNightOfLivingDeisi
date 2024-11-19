package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class InvalidFileException extends Throwable {
    private final int lineWithError;

    // Construtor existente
    public InvalidFileException(String message, int lineWithError) {
        super(message);
        this.lineWithError = lineWithError;
    }

    // Novo construtor para compatibilidade
    public InvalidFileException(String message) {
        super(message);
        this.lineWithError = -1; // Use -1 para indicar que a linha não foi especificada
    }

    public int getLineWithError() {
        return lineWithError;
    }
}
