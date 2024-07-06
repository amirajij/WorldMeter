package pt.ulusofona.aed.deisiworldmeter;

public class Cidade {
    String alfa2;
    String cidade;
    String regiao;
    double populacao;
    double latitude;
    double longitude;

    // Construtor com argumentos
    public Cidade(String alfa2, String cidade, String regiao, double populacao, double latitude, double longitude) {
        this.alfa2 = alfa2.toUpperCase();
        this.cidade = cidade;
        this.regiao = regiao;
        this.populacao = populacao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {

        int populacaoInt = (int) populacao; //Tirar a vírgula
        // Formatação atualizada para remover a parte decimal da população
        return cidade + " | " + alfa2 + " | " + regiao + " | " + populacaoInt + " | " + "(" + latitude + "," + longitude + ")";
    }
}