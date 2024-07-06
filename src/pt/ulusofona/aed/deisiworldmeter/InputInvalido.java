package pt.ulusofona.aed.deisiworldmeter;

public class InputInvalido {
    String nomeDoFicheiro;
    int linhasCorretas;
    int linhasIncorretas;
    int primeiraLinhaIncorreta;

    public InputInvalido(String nomeFicheiro) {
        this.nomeDoFicheiro = nomeFicheiro;
        this.linhasCorretas = 0;
        this.linhasIncorretas = 0;
        this.primeiraLinhaIncorreta = -1;
    }

    public void contadorLinhasCorretas() {
        linhasCorretas++;
    }

    public void contadorLinhasIncorretas(int linha) {
        if (linhasIncorretas == 0) {
            primeiraLinhaIncorreta = linha;
        }
        linhasIncorretas++;
    }


    @Override
    public String toString() {
        return nomeDoFicheiro + " | " + linhasCorretas + " | " + linhasIncorretas + " | " + (primeiraLinhaIncorreta != -1 ? primeiraLinhaIncorreta : "-1");
    }
}
