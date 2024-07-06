package pt.ulusofona.aed.deisiworldmeter;

public class Pais {
    int id;
    String alfa2;
    String alfa3;
    String nome;
    int contagemId; // Variável para armazenar a contagem de IDs

    public Pais(int id, String alfa2, String alfa3, String nome) {
        this.id = id;
        this.alfa2 = alfa2.toUpperCase();
        this.alfa3 = alfa3.toUpperCase();
        this.nome = nome;
        this.contagemId = 0; // Inicializa a contagem como 0
    }

    // Método para incrementar a contagem de IDs
    public void incrementarContagemId() {
        this.contagemId++;
    }

    @Override
    public String toString() {
        // Verifica se o ID é maior ou igual a 700 e ajusta o formato de saída
        if (id >= 700) {
            return nome + " | " + id + " | " + alfa2 + " | " + alfa3 + " | " + contagemId;
        } else {
            return nome + " | " + id + " | " + alfa2 + " | " + alfa3;
        }
    }
}


