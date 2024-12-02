package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class InvalidFileException extends Exception {
    private int lineWithError; // Armazena a linha onde o erro ocorreu

    public InvalidFileException(String message, int lineWithError) {
        super(message); // Mensagem de erro
        this.lineWithError = lineWithError;
    }

    public int getLineWithError() {
        return this.lineWithError; // Retorna a linha com erro
    }
}

