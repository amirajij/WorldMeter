package pt.ulusofona.aed.deisiworldmeter;

import java.io.*;
import java.util.*;

public class Main {
    // Armazenamento das linhas
    static ArrayList<Pais> paises = new ArrayList<>();
    static ArrayList<Cidade> cidades = new ArrayList<>();
    static ArrayList<Populacao> populacoes = new ArrayList<>();
    static ArrayList<InputInvalido> inputsInvalidos = new ArrayList<>(3);

    static boolean parseFiles(File pasta) {
        // Data structure reset
        paises = new ArrayList<>();
        cidades = new ArrayList<>();
        populacoes = new ArrayList<>();
        inputsInvalidos = new ArrayList<>();

        // Search for .csv files

        String[] nomesFicheiros = {"paises.csv", "cidades.csv", "populacao.csv"};
        for (String nome : nomesFicheiros) {
            File ficheiro = new File(pasta, nome);
            if (!ficheiro.exists() || ficheiro.isDirectory()) {
                System.out.println("Erro: O ficheiro " + nome + " não existe ou não é um ficheiro válido.");
                return false;
            }
        }
        lerFicheiroPaises(new File(pasta, "paises.csv"));
        lerFicheiroCidades(new File(pasta, "cidades.csv"));
        verificarCorrespondenciaPaisCidade();
        lerFicheiroPopulacao(new File(pasta, "populacao.csv"));
        verificarCorrespondenciaPaisPopulacao();

        return true;
    }

    static boolean lerFicheiroPaises(File ficheiroPaises) {
        Scanner scanner = null;
        InputInvalido inputInvalidoPaises = new InputInvalido(ficheiroPaises.getName());
        boolean primeiraLinha = true; // Ignora a primeira linha (cabeçalho)
        int numeroDaLinha = 0; // Contador para o número da linha

        try {
            scanner = new Scanner(ficheiroPaises);
        } catch (FileNotFoundException e) {
            return false;
        }

        while (scanner.hasNext()) {
            String linha = scanner.nextLine();
            numeroDaLinha++;

            if (primeiraLinha) {
                primeiraLinha = false;
                continue;
            }

            String[] partes = linha.split(",");
            if (partes.length == 4) {
                try {
                    int id = Integer.parseInt(partes[0]);
                    String alfa2 = partes[1];
                    String alfa3 = partes[2];
                    String nome = partes[3];

                    // Verifica se o ID já existe na lista de países (paises repetidos)
                    boolean idJaVisto = false;
                    for (Pais paisExistente : paises) {
                        if (paisExistente.id == id) {
                            idJaVisto = true;
                            break;
                        }
                    }

                    if (!idJaVisto) {
                        if (id > 0 && alfa2.length() == 2 && alfa3.length() == 3 && !nome.isEmpty()) {
                            Pais pais = new Pais(id, alfa2, alfa3, nome);
                            paises.add(pais);
                            inputInvalidoPaises.contadorLinhasCorretas();
                        } else {
                            inputInvalidoPaises.contadorLinhasIncorretas(numeroDaLinha);
                        }
                    } else {
                        inputInvalidoPaises.contadorLinhasIncorretas(numeroDaLinha);
                    }

                } catch (NumberFormatException e) {
                    inputInvalidoPaises.contadorLinhasIncorretas(numeroDaLinha);
                }
            } else {
                inputInvalidoPaises.contadorLinhasIncorretas(numeroDaLinha);
            }
        }
        scanner.close();
        inputsInvalidos.add(inputInvalidoPaises);
        return true;
    }

    static boolean lerFicheiroCidades(File ficheiroCidades) {
        Scanner scanner = null;
        InputInvalido inputInvalidoCidades = new InputInvalido(ficheiroCidades.getName());
        boolean primeiraLinha = true; // Ignora a primeira linha (cabeçalho)
        int numeroDaLinha = 0; // Contador para o número da linha

        try {
            scanner = new Scanner(ficheiroCidades);
        } catch (FileNotFoundException e) {
            return false;
        }

        while (scanner.hasNext()) {
            String linha = scanner.nextLine();
            numeroDaLinha++;

            if (primeiraLinha) {
                primeiraLinha = false;
                continue;
            }

            String[] partes = linha.split(",");
            if (partes.length == 6) {
                try {
                    String alfa2 = partes[0];
                    String nomeCidade = partes[1];
                    String regiao = partes[2];
                    double populacao = Double.parseDouble(partes[3]);
                    double latitude = Double.parseDouble(partes[4]);
                    double longitude = Double.parseDouble(partes[5]);

                    if (alfa2.length() == 2 && !regiao.isEmpty() && populacao > 0) {

                        boolean paisEncontrado = false;
                        for (Pais pais : paises) { // Trocar por HashMap
                            if (pais.alfa2.equalsIgnoreCase(alfa2)) {
                                paisEncontrado = true;
                                break;
                            }
                        }
                        if (paisEncontrado) {
                            Cidade cidadeObj = new Cidade(alfa2, nomeCidade, regiao, populacao, latitude, longitude);
                            cidades.add(cidadeObj);
                            inputInvalidoCidades.contadorLinhasCorretas();

                        } else {
                            inputInvalidoCidades.contadorLinhasIncorretas(numeroDaLinha);
                        }
                    } else {
                        inputInvalidoCidades.contadorLinhasIncorretas(numeroDaLinha);
                    }
                } catch (NumberFormatException e) {
                    inputInvalidoCidades.contadorLinhasIncorretas(numeroDaLinha);
                }
            } else {
                inputInvalidoCidades.contadorLinhasIncorretas(numeroDaLinha);
            }
        }
        scanner.close();
        inputsInvalidos.add(inputInvalidoCidades);
        return true;
    }

    static boolean lerFicheiroPopulacao(File ficheiroPopulacao) {
        Scanner scanner = null;
        InputInvalido inputInvalidoPopulacao = new InputInvalido(ficheiroPopulacao.getName());
        boolean primeiraLinha = true; // Ignora a primeira linha (cabeçalho)
        int numeroDaLinha = 0; // Contador para o número da linha

        try {
            scanner = new Scanner(ficheiroPopulacao);
            while (scanner.hasNext()) {
                String linha = scanner.nextLine();
                numeroDaLinha++;

                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] partes = linha.split(",");
                if (partes.length == 5) {
                    try {
                        int id = Integer.parseInt(partes[0]);
                        int ano = Integer.parseInt(partes[1]);
                        int populacaoMasculina = Integer.parseInt(partes[2]);
                        int populacaoFeminina = Integer.parseInt(partes[3]);
                        double densidade = Double.parseDouble(partes[4]);

                        if (ano >= 1950 && ano <= 2100 && id > 0 && populacaoMasculina > 0 && populacaoFeminina > 0
                                && densidade > 0) {
                            boolean paisEncontrado = false;
                            for (Pais pais : paises) {
                                if (pais.id == id) {
                                    paisEncontrado = true;
                                    break;
                                }
                            }

                            if (paisEncontrado) {
                                Populacao populacao = new Populacao(id, ano, populacaoMasculina, populacaoFeminina,
                                        densidade);
                                populacoes.add(populacao);
                                inputInvalidoPopulacao.contadorLinhasCorretas();
                                for (Pais pais : paises) {
                                    if (pais.id == id && id >= 700) {
                                        pais.incrementarContagemId();
                                    }
                                }
                            } else {
                                inputInvalidoPopulacao.contadorLinhasIncorretas(numeroDaLinha);
                            }
                        } else {
                            inputInvalidoPopulacao.contadorLinhasIncorretas(numeroDaLinha);
                        }
                    } catch (NumberFormatException e) {
                        inputInvalidoPopulacao.contadorLinhasIncorretas(numeroDaLinha);
                    }
                } else {
                    inputInvalidoPopulacao.contadorLinhasIncorretas(numeroDaLinha);
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        inputsInvalidos.add(inputInvalidoPopulacao);
        return true;
    }

    static void verificarCorrespondenciaPaisCidade() { // Verificar paises com cidades
        ArrayList<Pais> paisesFormatados = new ArrayList<>();
        HashSet<String> paisesComCidades = new HashSet<>();
        InputInvalido primeiroInputInvalido = inputsInvalidos.get(0);

        int linhasIncorretas = 0;
        int primeiraLinha = 1;
        boolean primeiraLinhaErrada = true;

        for (Cidade cidade : cidades) {
            paisesComCidades.add(cidade.alfa2);
        }

        for (Pais pais : paises) {
            if (primeiraLinhaErrada) {
                primeiraLinha++;
            }
            if (paisesComCidades.contains(pais.alfa2)) {
                paisesFormatados.add(pais);

            } else {
                linhasIncorretas++;
                primeiraLinhaErrada = false;
            }
        }

        paises = paisesFormatados;

        primeiroInputInvalido.linhasCorretas -= linhasIncorretas;
        primeiroInputInvalido.linhasIncorretas += linhasIncorretas;
        primeiroInputInvalido.primeiraLinhaIncorreta = primeiraLinha;
    }

    static void verificarCorrespondenciaPaisPopulacao() { // Remover populacoes sem paises
        ArrayList<Populacao> populacaoFormatado = new ArrayList<>();
        HashSet<Integer> paisesComPopulacao = new HashSet<>();
        InputInvalido terceiroInput = inputsInvalidos.get(2);

        int numeroDaLinha = 2;
        int linhasIncorretas = 0;


        for (Pais pais : paises) {
            paisesComPopulacao.add(pais.id);
        }

        for (Populacao populacao : populacoes) {
            if (paisesComPopulacao.contains(populacao.id)) {
                populacaoFormatado.add(populacao);
            } else {
                linhasIncorretas++;
                terceiroInput.contadorLinhasIncorretas(numeroDaLinha);
                terceiroInput.linhasCorretas--;

            }
            numeroDaLinha++;
        }
        populacoes = populacaoFormatado;

    }

    static ArrayList<Object> getObjects(TipoEntidade tipo) {
        // Formata as listas
        ArrayList<Object> resultado = new ArrayList<>();

        switch (tipo) {
            case PAIS:
                for (Pais pais : paises) {
                    resultado.add(pais);
                }
                break;
            case CIDADE:
                for (Cidade cidade : cidades) {
                    resultado.add(cidade);
                }
                break;
            case INPUT_INVALIDO:
                for (InputInvalido input : inputsInvalidos) {
                    resultado.add(input);
                }
                break;
        }
        return resultado;
    }

    public static String comandoHelp() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("-------------------------\n");
        resultado.append("Commands available:\n");
        resultado.append("COUNT_CITIES <min_population>\n");
        resultado.append("GET_CITIES_BY_COUNTRY <num-results> <country-name>\n");
        resultado.append("SUM_POPULATIONS <countries-list>\n");
        resultado.append("GET_HISTORY <year-start> <year-end> <country-name>\n");
        resultado.append("GET_MISSING_HISTORY <year-start> <year-end>\n");
        resultado.append("GET_MOST_POPULOUS <num-results>\n");
        resultado.append("GET_TOP_CITIES_BY_COUNTRY <num-results> <country-name>\n");
        resultado.append("GET_DUPLICATE_CITIES <min_population>\n");
        resultado.append("GET_COUNTRIES_GENDER_GAP <min-gender-gap>\n");
        resultado.append("GET_TOP_POPULATION_INCREASE <year-start> <year-end>\n");
        resultado.append("GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES <min-population>\n");
        resultado.append("GET_CITIES_AT_DISTANCE <distance> <country-name>\n");
        resultado.append("GET_CITIES_AT_DISTANCE2 <distance> <country-name>\n");
        resultado.append("GET_CITIES_WHITIN_RADIUS <radius> <central-city>\n");
        resultado.append("INSERT_CITY <alfa2> <city-name> <region> <population>\n");
        resultado.append("REMOVE_COUNTRY <country-name>\n");
        resultado.append("HELP\n");
        resultado.append("QUIT\n");
        resultado.append("-------------------------\n");
        return resultado.toString();
    }

    public static Result comandoCountCities(int minPopulation) { // Conta as cidades de um país atraves do minimo de populacao
        int count = 0;
        for (Cidade cidades : cidades) {
            if (cidades.populacao >= minPopulation) {
                count++;
            }
        }
        return new Result(true, null, "" + count);
    }

    public static Result comandoGetCitiesByCountry(int numResults, String countryName) {
        List<String> listaCidades = new ArrayList<>();
        String identificador = "";

        boolean paisEncontrado = false; // Encontra o id do país
        for (Pais pais : paises) {
            if (pais.nome.equals(countryName)) {
                identificador = pais.alfa2;
                paisEncontrado = true;
                break;
            }
        }

        if (!paisEncontrado) { // Caso o país não seja encontrado
            return new Result(true, null, "Pais invalido: " + countryName);
        }

        for (Cidade cidade : cidades) { // Adiciona a lista as cidades com esse id
            if (cidade.alfa2.equalsIgnoreCase(identificador)) {
                listaCidades.add(cidade.cidade);
                if (listaCidades.size() == numResults) {
                    break;
                }
            }
        }

        if (listaCidades.isEmpty()) {
            return new Result(true, null, "Zero cidades encontradas");
        }

        //listaCidades.sort(Comparator.comparing(cidade -> -po)); // serve para odernar a lista decrescente
        // Mostra as cidades
        StringBuilder resultadoMensagem = new StringBuilder();
        for (String cidade : listaCidades) {
            resultadoMensagem.append(cidade).append("\n");
        }
        return new Result(true, null, resultadoMensagem.toString());
    }

    public static Result comandoSumPopulations(String[] countriesList) { // Recebe lista de países
        int totalPopulacao = 0;

        Set<String> nomesPaises = new HashSet<>(Arrays.asList(countriesList)); // Contém a lista de países
        Set<Integer> identificadores = new HashSet<>();

        // Verifica se todos os países da lista têm um identificador
        boolean todosPaisesValidos = true;
        List<String> paisesInvalidos = new ArrayList<>(); // Lista para armazenar os nomes de países inválidos
        for (String nomePais : nomesPaises) { // Verifica se os paises inseridos existem
            boolean paisValido = false;
            for (Pais pais : paises) {
                if (pais.nome.equals(nomePais)) {
                    identificadores.add(pais.id);
                    paisValido = true;
                    break;
                }
            }
            if (!paisValido) {
                todosPaisesValidos = false;
                paisesInvalidos.add(nomePais); // Adiciona o nome do país inválido à lista
            }
        }

        if (!todosPaisesValidos) {
            String mensagem = "Pais invalido: " + String.join(",", paisesInvalidos);
            // Concatenar a lsita dos países inválidos
            return new Result(true, null, mensagem);
        }


        // Somar as populações dos países do ano 2024
        for (Populacao populacao : populacoes) {
            if (identificadores.contains(populacao.id) && populacao.ano == 2024) {
                totalPopulacao += populacao.populacaoMasculina + populacao.populacaoFeminina;
            }
        }

        String resultMessage = String.valueOf(totalPopulacao);
        return new Result(true, null, resultMessage);
    }

    public static String formatarPopulacaoPequeno(int populacao) {
        if (populacao >= 1000) {
            return (populacao / 1000) + "k";
        } else {
            return String.valueOf(populacao);
        }
    }

    public static Result comandoGetHistory(int yearStart, int yearEnd, String country) { // Mostra a populacao total dentro de um intervalo
        StringBuilder resultadoMensagem = new StringBuilder();
        int identificador = 0;

        if (yearStart > yearEnd) {
            return new Result(true, null, "intervalo invalido");

        }
        for (Pais pais : paises) {
            if (pais.nome.equals(country)) {
                identificador = pais.id;
                break;
            }
        }

        // Armazena os anos existentes dentro do intervalo
        Set<Integer> anosExistentes = new HashSet<>();

        // Itera sobre as populações para encontrar os anos existentes
        for (Populacao populacao : populacoes) {
            if (populacao.id == identificador && populacao.ano >= yearStart && populacao.ano <= yearEnd) {
                anosExistentes.add(populacao.ano);
            }
        }

        // Itera sobre os anos existentes para construir a mensagem de resultado
        for (int ano = yearStart; ano <= yearEnd; ano++) {

            if (anosExistentes.contains(ano)) {
                int populacaoMasculina = 0;
                int populacaoFeminina = 0;


                for (Populacao populacao : populacoes) {
                    if (populacao.ano == ano && populacao.id == identificador) {
                        populacaoMasculina += populacao.populacaoMasculina;
                        populacaoFeminina += populacao.populacaoFeminina;
                    }
                }

                String masculinoFormatado = formatarPopulacaoPequeno(populacaoMasculina);
                String femininoFormatado = formatarPopulacaoPequeno(populacaoFeminina);

                resultadoMensagem.append(ano).append(":").append(masculinoFormatado).append(":").
                        append(femininoFormatado).append("\n");
            }
        }

        return new Result(true, null, resultadoMensagem.toString());
    }

    public static Result comandoGetMissingHistory(int inicio, int yearEnd) { // Mostra os paises quem tem anos em falta nesse intervalo
        Set<String> paisesAusentes = new HashSet<>(); // Nomes dos países sem população
        Map<Integer, Set<Integer>> dadosPorPais = new HashMap<>(); // Armazena dos dados da população por país (ID, ANOS)

        // Preencher o HashMap, cria uma nova entrada com um conjunto de anos vazios
        for (Populacao populacao : populacoes) {
            if (!dadosPorPais.containsKey(populacao.id)) {
                dadosPorPais.put(populacao.id, new HashSet<>());
            }
            dadosPorPais.get(populacao.id).add(populacao.ano); //obtem os anos e adiciona-os
        }

        // Verificar se algum país está ausente em algum ano no intervalo dado
        for (Pais pais : paises) {
            Set<Integer> anosDisponiveis = dadosPorPais.getOrDefault(pais.id, new HashSet<>()); // Obtem um conjunto de anos disponiveis
            for (int ano = inicio; ano <= yearEnd; ano++) {
                if (!anosDisponiveis.contains(ano)) { // Verifica se contem os anos nesse intervalo
                    paisesAusentes.add(pais.alfa2.toLowerCase() + ":" + pais.nome); // se nao contem adiciona ao conjunto
                    break;
                }
            }
        }

        if (paisesAusentes.isEmpty()) { // se tiver todos os anos
            return new Result(true, null, "Sem resultados");
        }

        StringBuilder resultadoMensagem = new StringBuilder();
        for (String paisAusente : paisesAusentes) {
            resultadoMensagem.append(paisAusente).append("\n");
        }

        return new Result(true, null, resultadoMensagem.toString());
    }

    public static Result comandoGetMostPopulous(int numResults) { //Cidade mais populosas

        Map<String, Cidade> cidadesMaisPopulosasPorPais = new HashMap<>(); // chave é o alfa2, valor é cidade

        //Se não existir, ou se a população da cidade atual (cidade.populacao) for maior do que a cidade já armazenada
        // no mapa para aquele país, substitui a entrada no mapa com a cidade atual.
        for (Cidade cidade : cidades) {
            if (!cidadesMaisPopulosasPorPais.containsKey(cidade.alfa2) ||
                    cidade.populacao > cidadesMaisPopulosasPorPais.get(cidade.alfa2).populacao) {
                cidadesMaisPopulosasPorPais.put(cidade.alfa2, cidade);
            }
        }

        // Todas as cidades armazendas
        List<Cidade> cidadesOrdenadasPorPopulacao = new ArrayList<>(cidadesMaisPopulosasPorPais.values());
        cidadesOrdenadasPorPopulacao.sort(Comparator.comparing(cidade -> -cidade.populacao)); // serve para odernar a lista decrescente

        int numResultadosReais = Math.min(numResults, cidadesOrdenadasPorPopulacao.size()); // calcula o numero de resultados a retornar

        StringBuilder resultadoMensagem = new StringBuilder();
        for (int i = 0; i < numResultadosReais; i++) {
            Cidade cidade = cidadesOrdenadasPorPopulacao.get(i);
            String nomePais = "";
            for (Pais pais : paises) {
                if (pais.alfa2.equals(cidade.alfa2)) {
                    nomePais = pais.nome;
                    break;
                }
            }

            resultadoMensagem.append(nomePais).append(":").append(cidade.cidade).append(":").append((int) cidade.populacao).append("\n");
        }
        return new Result(true, null, resultadoMensagem.toString());
    }

    public static String formatarPopulacaoGrande(int populacao) {
        if (populacao >= 1000) {
            return (populacao / 1000) + "K";
        } else {
            return String.valueOf(populacao);
        }
    }

    public static Result comandoGetTopCitiesByCountry(int numResults, String countryName) { // cidades mais populosas de um pais
        ArrayList<Cidade> cidadesDoPais = new ArrayList<>();
        for (Pais pais : paises) {
            if (pais.nome.equals(countryName)) {
                for (Cidade cidade : cidades) {
                    if (cidade.alfa2.equals(pais.alfa2) && cidade.populacao >= 10000) {
                        cidadesDoPais.add(cidade);
                    }
                }
                break;
            }
        }

        // Ordena as cidades por população, depois pelo nome da cidade
        cidadesDoPais.sort((cidade1, cidade2) -> {
            int comparar = Integer.compare(((int) cidade2.populacao / 1000), ((int) cidade1.populacao / 1000)); // compara por valor
            if (comparar != 0) {
                return comparar;
            } else { // Se forem iguais os valores
                // Ordena pelo nome da cidade em ordem alfabética
                return cidade1.cidade.compareTo(cidade2.cidade);
            }
        });


        // se for -1 mostra tudo
        if (numResults != -1) {
            int numResultadosReais = Math.min(numResults, cidadesDoPais.size());
            cidadesDoPais = new ArrayList<>(cidadesDoPais.subList(0, numResultadosReais));
        }

        StringBuilder resultadoMensagem = new StringBuilder();
        for (Cidade cidade : cidadesDoPais) {
            String populacaoFormatada = formatarPopulacaoGrande((int) cidade.populacao);
            resultadoMensagem.append(cidade.cidade).append(":").append(populacaoFormatada).append("\n");
        }

        return new Result(true, null, resultadoMensagem.toString());
    }

    public static Result comandoInsertCity(String alfa2, String cityName, String region, int population) {
        alfa2 = alfa2.toLowerCase();
        double populationDouble = population;
        boolean found = false;
        for (Pais pais : paises) {
            if (pais.alfa2.toLowerCase().equals(alfa2)) {
                found = true;
                break;
            }
        }

        if (!found) {
            return new Result(true, null, "Pais invalido");
        }

        Cidade cidade = new Cidade(alfa2, cityName, region, populationDouble, 0.0, 0.0);

        // Adiciona a cidade ao array global de cidades
        cidades.add(cidades.size(), cidade);

        return new Result(true, null, "Inserido com sucesso");
    }

    public static Result comandoRemoveCountry(String countryName) {
        boolean found = false;
        String alfa2PaisRemovido = null;

        // Remove o país da lista global
        for (int i = 0; i < paises.size(); i++) {
            if (paises.get(i).nome.equalsIgnoreCase(countryName)) {
                alfa2PaisRemovido = paises.get(i).alfa2;
                paises.remove(i);
                found = true;
                break;
            }
        }

        if (!found) {
            return new Result(true, null, "Pais invalido");
        }

        // Remove todas as cidades associadas ao país removido
        Iterator<Cidade> iterator = cidades.iterator();
        while (iterator.hasNext()) {
            Cidade cidade = iterator.next();
            if (cidade.alfa2.equalsIgnoreCase(alfa2PaisRemovido)) {
                iterator.remove();
            }
        }

        return new Result(true, null, "Removido com sucesso");
    }

    public static Result comandoGetDuplicateCities(int minPopulation) {
        Map<String, List<Cidade>> cidadesPorNome = new HashMap<>();

        // Agrupar cidades pelo nome
        for (Cidade cidade : cidades) {
            if (cidade.populacao >= minPopulation) {
                String chave = cidade.cidade.toLowerCase(); // Nome da cidade como chave
                cidadesPorNome.computeIfAbsent(chave, k -> new ArrayList<>()).add(cidade); // Verifica se a chave já existe no HashMap
            }
        }

        // Filtrar apenas as cidades duplicadas não incluir a cidade original
        List<String> cidadesDuplicadas = new ArrayList<>();
        for (List<Cidade> listaCidades : cidadesPorNome.values()) {
            if (listaCidades.size() > 1) { // Se houver mais de uma cidade com o mesmo nome
                boolean primeiraCidade = true;
                for (Cidade cidade : listaCidades) {
                    if (!primeiraCidade) {
                        String nomePais = "";
                        for (Pais pais : paises) {
                            if (pais.alfa2.equals(cidade.alfa2)) {
                                nomePais = pais.nome;
                                break;
                            }
                        }
                        cidadesDuplicadas.add(cidade.cidade + " (" + nomePais + "," + cidade.regiao + ")");
                    }
                    primeiraCidade = false;
                }
            }
        }

        if (cidadesDuplicadas.isEmpty()) {
            return new Result(true, null, "Sem resultados");
        }

        StringBuilder resultadoMensagem = new StringBuilder();
        for (String cidadeDuplicada : cidadesDuplicadas) {
            resultadoMensagem.append(cidadeDuplicada).append("\n");
        }

        return new Result(true, null, resultadoMensagem.toString());
    }

    public static Result comandoGetCountriesGenderGap(int minGenderGap) {
        Map<Integer, Populacao> populacoes2024PorPais = new HashMap<>();

        for (Populacao populacao : populacoes) {
            if (populacao.ano == 2024) {
                populacoes2024PorPais.put(populacao.id, populacao);
            }
        }

        Map<String, Double> genderGapPorPais = new HashMap<>();

        // Calcular para cada país
        for (Pais pais : paises) {
            Populacao populacao = populacoes2024PorPais.get(pais.id);
            if (populacao != null) {
                double populacaoMasculina = populacao.populacaoMasculina;
                double populacaoFeminina = populacao.populacaoFeminina;

                // Calcular o gender gap usando a fórmula
                double genderGap = (Math.abs(populacaoMasculina - populacaoFeminina) / (populacaoMasculina
                        + populacaoFeminina)) * 100;

                // Adicionar o gender gap ao mapa se for maior ou igual ao mínimo especificado
                if (genderGap >= minGenderGap) {
                    genderGapPorPais.put(pais.nome, genderGap);
                }
            }
        }

        if (genderGapPorPais.isEmpty()) {
            return new Result(true, null, "Sem resultados");
        }

        StringBuilder resultadoMensagem = new StringBuilder();
        for (Map.Entry<String, Double> entry : genderGapPorPais.entrySet()) {
            resultadoMensagem.append(entry.getKey()).append(":").append(String.format("%.2f", entry.getValue())).
                    append("\n");
        }

        return new Result(true, null, resultadoMensagem.toString());
    }

    public static Result comandoGetTopPopulationIncrease(int anoInicio, int anoFim) {
        Map<String, Double> aumentosPopulacionais = new TreeMap<>();

        for (Pais pais : paises) {
            int paisId = pais.id;

            // Armazena a população de cada ano
            Map<Integer, Integer> populacaoPorAno = new HashMap<>();

            // População para o intervalo de anos
            for (Populacao dadosPopulacao : populacoes) {
                if (dadosPopulacao.id == paisId && dadosPopulacao.ano >= anoInicio && dadosPopulacao.ano <= anoFim) {
                    int populacaoTotal = dadosPopulacao.populacaoMasculina + dadosPopulacao.populacaoFeminina;
                    populacaoPorAno.put(dadosPopulacao.ano, populacaoTotal);
                }
            }

            // Calcula o aumento de população para cada par de anos dentro do intervalo
            List<Integer> anos = new ArrayList<>(populacaoPorAno.keySet());
            for (int i = 0; i < anos.size(); i++) {
                int anoInicial = anos.get(i);
                int populacaoInicial = populacaoPorAno.get(anoInicial);

                for (int j = i + 1; j < anos.size(); j++) {
                    int anoFinal = anos.get(j);
                    int populacaoFinal = populacaoPorAno.get(anoFinal);

                    // Verifica se houve aumento na população
                    if (populacaoFinal > populacaoInicial) {
                        double aumentoPopulacional = ((double) (populacaoFinal - populacaoInicial) / populacaoFinal) * 100;
                        String chave = pais.nome + ":" + anoInicial + "-" + anoFinal;
                        aumentosPopulacionais.put(chave, aumentoPopulacional);
                    }
                }
            }
        }

        // Coloca em ordem decrescente
        List<Map.Entry<String, Double>> aumentosOrdenados = new ArrayList<>(aumentosPopulacionais.entrySet());
        aumentosOrdenados.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        StringBuilder resultado = new StringBuilder();
        int contador = 0;
        for (Map.Entry<String, Double> entrada : aumentosOrdenados) {
            if (contador >= 5) {
                break;
            }
            // Formata o valor para duas casas decimais
            String aumentoFormatado = String.format("%.2f", entrada.getValue());
            resultado.append(entrada.getKey()).append(":").append(aumentoFormatado).append("%\n");
            contador++;
        }

        return new Result(true, null, resultado.toString());
    }

    public static Result comandoGetDuplicateCitiesDifferentCountries(int populacaoMinima) {
        Map<String, List<Cidade>> cidadesAgrupadasPorNome = new HashMap<>();

        // Agrupa as cidades pelo nome
        for (Cidade cidade : cidades) {
            if (cidade.populacao >= populacaoMinima) {
                String nomeCidade = cidade.cidade.toLowerCase();
                cidadesAgrupadasPorNome.computeIfAbsent(nomeCidade, k -> new ArrayList<>()).add(cidade); // Verifica se a chave já existe no HAshMap
            }
        }

        // Coloca apenas as cidades duplicadas e inclui as originais
        Map<String, Set<String>> cidadesDuplicadasPorNome = new TreeMap<>();
        for (List<Cidade> cidadesComMesmoNome : cidadesAgrupadasPorNome.values()) {
            if (cidadesComMesmoNome.size() > 1) { // Se houver mais de uma cidade com o mesmo nome
                Set<String> paisesComCidadeDuplicada = new TreeSet<>(); // Armazena nomes dos países de forma ordenada
                for (Cidade cidade : cidadesComMesmoNome) {
                    String nomePais = getNomePaisByAlfa2(cidade.alfa2); // Procura o nome do país pelo alfa2
                    paisesComCidadeDuplicada.add(nomePais);
                }
                if (paisesComCidadeDuplicada.size() > 1) { // Verifica se há mais de um país
                    cidadesDuplicadasPorNome.put(cidadesComMesmoNome.get(0).cidade, paisesComCidadeDuplicada);
                }
            }
        }

        StringBuilder resultadoMensagem = new StringBuilder();
        for (Map.Entry<String, Set<String>> entrada : cidadesDuplicadasPorNome.entrySet()) {
            resultadoMensagem.append(entrada.getKey()).append(": ");
            Set<String> paises = entrada.getValue();
            resultadoMensagem.append(String.join(",", paises)).append("\n");
        }

        return new Result(true, null, resultadoMensagem.toString());
    }

    public static String getNomePaisByAlfa2(String alfa2) {
        for (Pais pais : paises) {
            if (pais.alfa2.equals(alfa2)) {
                return pais.nome;
            }
        }
        return null;
    }

    public static Result comandoGetCitiesAtDistance1(int distance, String countryName) {
        StringBuilder resultadoMensagem = new StringBuilder();
        String countryAlpha2 = "";

        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(countryName)) {
                countryAlpha2 = pais.alfa2;
                break;
            }
        }

        List<Cidade> countryCities = new ArrayList<>();
        for (Cidade cidade : cidades) {
            if (cidade.alfa2.equalsIgnoreCase(countryAlpha2)) {
                countryCities.add(cidade);
            }
        }

        for (int i = 0; i < countryCities.size() - 1; i++) {
            for (int j = i + 1; j < countryCities.size(); j++) {
                Cidade cidade1 = countryCities.get(i);
                Cidade cidade2 = countryCities.get(j);
                double dist = haversine(cidade1.latitude, cidade1.longitude, cidade2.latitude, cidade2.longitude);
                if (Math.abs(dist - distance) <= 1) {
                    String cidadeMenor, cidadeMaior;
                    if (cidade1.cidade.compareToIgnoreCase(cidade2.cidade) < 0) {
                        cidadeMenor = cidade1.cidade;
                        cidadeMaior = cidade2.cidade;
                    } else {
                        cidadeMenor = cidade2.cidade;
                        cidadeMaior = cidade1.cidade;
                    }
                    resultadoMensagem.append(cidadeMenor).append("->").append(cidadeMaior).append("\n");
                }
            }
        }

        return new Result(true, null, resultadoMensagem.toString());
    }

    public static Result comandoGetCitiesAtDistance2(int distance, String countryName) {
        StringBuilder resultadoMensagem = new StringBuilder();
        String countryAlpha2 = "";

        // Encontrar o código alfa2 do país
        for (Pais pais : paises) {
            if (pais.nome.equalsIgnoreCase(countryName)) {
                countryAlpha2 = pais.alfa2;
                break;
            }
        }

        // Criar listas separadas para cidades dentro e fora do país especificado
        List<Cidade> countryCities = new ArrayList<>();
        List<Cidade> otherCities = new ArrayList<>();
        for (Cidade cidade : cidades) {
            if (cidade.alfa2.equalsIgnoreCase(countryAlpha2)) {
                countryCities.add(cidade);
            } else {
                otherCities.add(cidade);
            }
        }

        // Lista para armazenar pares de cidades
        List<String> paresDeCidades = new ArrayList<>();

        // Comparar cada cidade do país com todas as outras cidades fora do país
        for (Cidade cidade1 : countryCities) { // Acaba por multiplicar
            for (Cidade cidade2 : otherCities) {
                double dist = haversine(cidade1.latitude, cidade1.longitude, cidade2.latitude, cidade2.longitude);
                if (Math.abs(dist - distance) <= 1) {
                    // Ordenar as cidades pelo nome antes de adicionar à lista de pares
                    String cidadeMenor, cidadeMaior;
                    if (cidade1.cidade.compareTo(cidade2.cidade) < 0) {
                        cidadeMenor = cidade1.cidade;
                        cidadeMaior = cidade2.cidade; //
                    } else {
                        cidadeMenor = cidade2.cidade;
                        cidadeMaior = cidade1.cidade;
                    }
                    paresDeCidades.add(cidadeMenor + "->" + cidadeMaior);
                }
            }
        }

        // Ordenar a lista de pares de cidades
        Collections.sort(paresDeCidades);

        // Construir a string final
        for (String par : paresDeCidades) {
            resultadoMensagem.append(par).append("\n");
        }

        return new Result(true, null, resultadoMensagem.toString());
    }

    // Fórmula de Haversine
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Raio da terra
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distância em quilômetros
    }

    // Comando Próprio - Componente da Creatividade
    public static Result comandoGetCitiesWithinRadius(double radius, String centralCityName) {
        StringBuilder ciadesNumRaio = new StringBuilder();
        Cidade cidadeCentral = null;

        // Encontra a cidade central pelo nome
        for (Cidade cidade : cidades) {
            if (cidade.cidade.equalsIgnoreCase(centralCityName)) {
                cidadeCentral = cidade;
                break;
            }
        }
        // Verifica se a cidade central foi encontrada
        if (cidadeCentral == null) {
            return new Result(true, null, "Cidade não encontrada");
        }
        // Lista para armazenar as cidades dentro do raio
        List<Cidade> cidadesPerto = new ArrayList<>();

        // Calcula a distância entre a cidade central e todas as outras cidades
        for (Cidade cidade : cidades) {
            if (!cidade.cidade.equalsIgnoreCase(centralCityName)) {
                double dist = haversine(cidadeCentral.latitude, cidadeCentral.longitude, cidade.latitude, cidade.longitude);
                if (dist <= radius) {
                    cidadesPerto.add(cidade);
                }
            }
        }
        // Ordena as cidades pelo nome
        cidadesPerto.sort(Comparator.comparing(cidade -> cidade.cidade));

        // Adicionar as cidades ao resultado
        for (Cidade cidade : cidadesPerto) {
            ciadesNumRaio.append(cidade.cidade).append("\n");
        }

        return new Result(true, null, ciadesNumRaio.toString());
    }

    public static Result execute(String command) {
        String[] parts = command.split(" ");
        switch (parts[0]) {

            case "HELP": // Mostra os comandos
                return new Result(true, null, comandoHelp());

            case "COUNT_CITIES": // Quantas cidades têm >= a um certo número de habitantes
                return comandoCountCities(Integer.parseInt(parts[1])); // min-population

            case "GET_CITIES_BY_COUNTRY": // Primeiras cidades de acordo com a ordem que aparece no ficheiro
                String country = "";
                for (int palavras = 2; palavras < parts.length; palavras++) {
                    country += parts[palavras];
                    if (palavras < parts.length - 1) {
                        country += " ";
                    }
                }
                return comandoGetCitiesByCountry(Integer.parseInt(parts[1]), country); // num-results, countryName

            case "SUM_POPULATIONS": // Calcula população total do conjunto de países
                String listaPaises = command.substring(command.indexOf("SUM_POPULATIONS") +
                        "SUM_POPULATIONS".length()).trim();
                String[] paises = listaPaises.split(",");
                return comandoSumPopulations(paises);  // country-list

            case "GET_HISTORY": // Mostra a PM e PF de um país num certo intervalo
                String country1 = "";
                for (int palavras = 3; palavras < parts.length; palavras++) {
                    country1 += parts[palavras];
                    if (palavras < parts.length - 1) {
                        country1 += " ";
                    }
                }
                return comandoGetHistory(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), country1); // year-start, year-end, country

            case "GET_MISSING_HISTORY": // Mostra os países em que faltam anos dentro do intervalo indicado.
                return comandoGetMissingHistory(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])); // year-start, year-end

            case "GET_MOST_POPULOUS": // Mostra por ordem decrescente as maiores cidades a nível mundial
                return comandoGetMostPopulous(Integer.parseInt(parts[1])); // num-results

            case "GET_TOP_CITIES_BY_COUNTRY": // Mostra as cidades mais populosas por país
                String country2 = "";
                for (int palavras = 2; palavras < parts.length; palavras++) {
                    country2 += parts[palavras];
                    if (palavras < parts.length - 1) {
                        country2 += " ";
                    }
                }
                return comandoGetTopCitiesByCountry(Integer.parseInt(parts[1]), country2); // num-results, country

            case "GET_DUPLICATE_CITIES": // Mostra as cidades duplicadas não mostrando o original
                return comandoGetDuplicateCities(Integer.parseInt(parts[1])); // min-population

            case "GET_COUNTRIES_GENDER_GAP": // Mostra a descrepância entre homens e mulheres
                return comandoGetCountriesGenderGap(Integer.parseInt(parts[1])); // min-gender-Gap

            case "GET_TOP_POPULATION_INCREASE": // Mostra o aumento da população dentro de um intervalo de anos
                return comandoGetTopPopulationIncrease(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])); // year-start, year-end

            case "GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES": // Mostra cidades duplicadas de outros países
                return comandoGetDuplicateCitiesDifferentCountries(Integer.parseInt(parts[1]));

            case "GET_CITIES_AT_DISTANCE": // Mostra cidades que têm uma certa distância em um país
                String country3 = "";
                for (int palavras = 2; palavras < parts.length; palavras++) {
                    country3 += parts[palavras];
                    if (palavras < parts.length - 1) {
                        country3 += " ";
                    }
                }
                return comandoGetCitiesAtDistance1(Integer.parseInt(parts[1]), country3); // distance, country

            case "GET_CITIES_AT_DISTANCE2":
                String country4 = ""; // Mostra cidades que têm uma certa distância em um país e outra noutro
                for (int palavras = 2; palavras < parts.length; palavras++) {
                    country4 += parts[palavras];
                    if (palavras < parts.length - 1) {
                        country4 += " ";
                    }
                }
                return comandoGetCitiesAtDistance2(Integer.parseInt(parts[1]), country4);

            case "GET_CITIES_WHITIN_RADIUS":
                String cidadeCentral = ""; // Diz as cidades dentro de um certo raio doutra
                for (int palavras = 2; palavras < parts.length; palavras++) {
                    cidadeCentral += parts[palavras];
                    if (palavras < parts.length - 1) {
                        cidadeCentral += " ";
                    }
                }
                return comandoGetCitiesWithinRadius(Double.parseDouble(parts[1]), cidadeCentral);

            case "INSERT_CITY": // Insere uma nova cidade no ArrayList
                return comandoInsertCity(parts[1].toUpperCase(), parts[2], parts[3], Integer.parseInt(parts[4]));

            case "REMOVE_COUNTRY": // Remove uma cidade do ArrayList pais e atualiza o das cidades
                String countryRemove = "";
                for (int palavras = 1; palavras < parts.length; palavras++) {
                    countryRemove += parts[palavras];
                    if (palavras < parts.length - 1) {
                        countryRemove += " ";
                    }
                }
                return comandoRemoveCountry(countryRemove); // Nome do pais

            default:
                return new Result(false, "Comando invalido", null);
        }
    }

    public static void main(String[] args) {

        paises = new ArrayList<>();
        cidades = new ArrayList<>();
        populacoes = new ArrayList<>();
        inputsInvalidos = new ArrayList<>();

        System.out.println("Welcome to DEISI World Meter");

        long start = System.currentTimeMillis();
        File pasta = new File("test-files");
        boolean parseOk = parseFiles(pasta);
        if (!parseOk) {
            System.out.println("Error loading files");
            return;
        }
        long end = System.currentTimeMillis();

        System.out.println("Loaded files in " + (end - start) + " ms\n");


        ArrayList<Object> resultados;

        // Teste para INPUT_INVALIDO
        System.out.println("Resultados para INPUT_INVALIDO:");
        resultados = getObjects(TipoEntidade.INPUT_INVALIDO);
        for (Object obj : resultados) {
            System.out.println(obj);
        }

        Result result = execute("HELP");
        System.out.println(result.result);

        Scanner in = new Scanner(System.in);

        String line;

        do {
            System.out.print("> ");
            line = in.nextLine(); // Read input at the start of the loop

            if (line != null && !line.equals("QUIT")) {
                start = System.currentTimeMillis();
                result = execute(line);
                end = System.currentTimeMillis();

                if (!result.success) {
                    System.out.println("Error " + result.error);
                } else {
                    System.out.println(result.result);
                    System.out.println("(took " + (end - start) + " ms)");
                }
            }
        } while (line != null && !line.equals("QUIT"));


       /* ArrayList<Object> resultados;

        // Teste para INPUT_INVALIDO
        System.out.println("Resultados para INPUT_INVALIDO:");
        resultados = getObjects(TipoEntidade.INPUT_INVALIDO);
        for (Object obj : resultados) {
            System.out.println(obj);
        }

        // Teste para PAIS
        System.out.println("\nResultados para PAIS:");
        resultados = getObjects(TipoEntidade.PAIS);
        for (Object obj : resultados) {
            System.out.println(obj);
        }

        // Teste para CIDADE
        System.out.println("\nResultados para CIDADE:");
        resultados = getObjects(TipoEntidade.CIDADE);
        for (Object obj : resultados) {
            System.out.println(obj);
        }*/
    }

}

/** AUTHOR : https://github.com/amirajij */