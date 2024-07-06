package pt.ulusofona.aed.deisiworldmeter;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestMain {
    @Test
    public void testToStringPaisIdMenorQue700() {
        Pais pais = new Pais(500, "pt", "prt", "Portugal");
        String resultado = pais.toString();
        String resultadoEsperado = "Portugal | 500 | PT | PRT";

        assertEquals(resultadoEsperado, resultado,
                "Teste string país com ID menor que 700 INCORRETO");
    }

    @Test
    public void testToStringPaisIdMaiorQue700() {
        Pais pais = new Pais(701, "WK", "WKA", "Wakanda");
        // Simula a contagem de IDs
        pais.incrementarContagemId();
        pais.incrementarContagemId();

        String resultado = pais.toString();
        String resultadoEsperado = ("Wakanda | 701 | WK | WKA | 2");

        assertEquals(resultadoEsperado, resultado,
                "Teste string país com ID maior que 700 INCORRETO");

    }

    @Test
    public void testToStringCidade() {
        Cidade cidade = new Cidade("PT", "Lisboa", "Lisboa", 505526, 38.7167, -9.1333);
        String resultado = cidade.toString();
        String resultadoEsperado = ("Lisboa | PT | Lisboa | 505526 | (38.7167,-9.1333)");

        assertEquals(resultadoEsperado, resultado,
                "Teste string Cidade INCORRETO");

    }

    @Test
    public void testParseFilesEGetObjects() {
        File pastaTeste = new File("test-files");
        boolean resultadoParse = Main.parseFiles(pastaTeste);

        // Verifica se o parseFiles() foi bem-sucedido
        assertTrue(resultadoParse);

        // Verifica se existem pelo menos 2 países
        ArrayList<Object> paises = Main.getObjects(TipoEntidade.PAIS);
        assertEquals(true, paises.size() >= 2);

        // Verifica se existem pelo menos 2 cidades para cada país
        ArrayList<Object> cidades = Main.getObjects(TipoEntidade.CIDADE);
        for (Object objeto : paises) {
            Pais pais = (Pais) objeto;
            int contadorCidades = 0;
            for (Object cidadeObjeto : cidades) {
                Cidade cidade = (Cidade) cidadeObjeto;
                if (cidade.alfa2.equals(pais.alfa2)) {
                    contadorCidades++;
                }
            }
            assertTrue(contadorCidades >= 2, "Menos de 2 cidades para o país: " + pais.nome);
        }
    }

    @Test
    public void testParseFilesComErrorsEGetObjectsInputInvalido() {

        File pastaTesteErro = new File("test-files");
        boolean resultadoParse = Main.parseFiles(pastaTesteErro);
        assertTrue(resultadoParse);

        ArrayList<Object> inputInvalido = Main.getObjects(TipoEntidade.INPUT_INVALIDO);

        assertFalse(inputInvalido.isEmpty(), "A lista de objetos INPUT_INVALIDO está vazia");
    }

    @Test
    public void testGetObjects() {

        //Limpar as variáveis
        Main.paises.clear();
        Main.cidades.clear();
        Main.inputsInvalidos.clear();

        Main.paises.add(new Pais(1, "PT", "PRT", "Portugal"));
        Main.paises.add(new Pais(2, "ES", "ESP", "Espanha"));
        Main.cidades.add(new Cidade("PT", "Lisboa", "Lisboa", 1000000, 38.736946, -9.142685));
        Main.cidades.add(new Cidade("PT", "Porto", "Porto", 600000, 41.14961, -8.61099));
        Main.populacoes.add(new Populacao(1, 2020, 5000000, 5200000, 124.52));
        Main.populacoes.add(new Populacao(2, 2020, 46000000, 48000000, 94.28));
        Main.inputsInvalidos.add(new InputInvalido("Invalido1"));
        Main.inputsInvalidos.add(new InputInvalido("Invalido2"));

        //Teste o retorno para PAIS
        ArrayList<Object> paisesResult = Main.getObjects(TipoEntidade.PAIS);
        assertEquals(2, paisesResult.size(), "Número incorreto de elementos no array de países");

        //Teste o retorno para CIDADE
        ArrayList<Object> cidadesResult = Main.getObjects(TipoEntidade.CIDADE);
        assertEquals(2, cidadesResult.size(), "Número incorreto de elementos no array de cidades");

        //Teste o retorno para INPUT_INVALIDO
        ArrayList<Object> inputInvalidosResult = Main.getObjects(TipoEntidade.INPUT_INVALIDO);
        assertEquals(2, inputInvalidosResult.size(), "Número incorreto de elementos no array de entradas inválidas");
    }

    @Test
    public void testCountCities() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("COUNT_CITIES 100000");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "3"
        }, resultParts);

        result = Main.execute("COUNT_CITIES 500000");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "1"
        }, resultParts);

    }

    @Test
    public void testGetCitiesByCountry() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_BY_COUNTRY 2 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "almada",
                "lamego"
        }, resultParts);

        result = Main.execute("GET_CITIES_BY_COUNTRY 3 Angola");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "luanda",
                "cidade1"
        }, resultParts);
    }

    @Test
    public void testSumPopulations() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("SUM_POPULATIONS Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "8799093"
        }, resultParts);

        result = Main.execute("SUM_POPULATIONS Portugal,Júpiter");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Pais invalido: Júpiter"
        }, resultParts);
    }

    @Test
    public void testGetHistory() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_HISTORY 2020 2021 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "2020:4137k:4482k",
                "2021:4157k:4508k"
        }, resultParts);

        result = Main.execute("GET_HISTORY 2020 2024 Cabo Verde");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "2020:289k:292k",
                "2021:292k:295k",
                "2022:294k:298k",
                "2023:297k:301k",
                "2024:300k:303k"

        }, resultParts);
    }

    @Test
    public void testGetMissingHistory() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_MISSING_HISTORY 2020 2021");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "ao:Angola"
        }, resultParts);

        result = Main.execute("GET_MISSING_HISTORY 1951 1952");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "cv:Cabo Verde",
                "ao:Angola",
                "pt:Portugal"
        }, resultParts);
    }

    @Test
    public void testGetMostPopulous() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_MOST_POPULOUS 1");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Angola:luanda:2776125"
        }, resultParts);

        result = Main.execute("GET_MOST_POPULOUS 3");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Angola:luanda:2776125",
                "Cabo Verde:praia:131717",
                "Portugal:queluz:103398"
        }, resultParts);
    }

    @Test
    public void testGetTopCitiesByCountry() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_TOP_CITIES_BY_COUNTRY 1 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "queluz:103K",
        }, resultParts);

        result = Main.execute("GET_TOP_CITIES_BY_COUNTRY -1 Cabo Verde");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "praia:131K",
                "mindelo:71K",
                "santa nar1a:19K"
        }, resultParts);
    }

    @Test
    public void testGetDuplicateCities() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_DUPLICATE_CITIES 1000");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "cidade1 (Portugal,01)",
                "cidade2 (Cabo Verde,01)"
        }, resultParts);

        result = Main.execute("GET_DUPLICATE_CITIES 100000");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Sem resultados"
        }, resultParts);
    }

    @Test
    public void testGetCountriesGenderGap() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_COUNTRIES_GENDER_GAP 0");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Cabo Verde:0.57",
                "Portugal:4.21"
        }, resultParts);

        result = Main.execute("GET_COUNTRIES_GENDER_GAP 1");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Portugal:4.21"
        }, resultParts);
    }

    @Test
    public void testGetTopPopulationIncrease() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_TOP_POPULATION_INCREASE 2021 2022");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Cabo Verde:2021-2022:0.88%",
                "Portugal:2021-2022:0.48%"
        }, resultParts);

        result = Main.execute("GET_TOP_POPULATION_INCREASE 2020 2022");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Cabo Verde:2020-2022:1.77%",
                "Portugal:2020-2022:1.01%",
                "Cabo Verde:2020-2021:0.90%",
                "Cabo Verde:2021-2022:0.88%",
                "Portugal:2020-2021:0.53%"
        }, resultParts);
    }

    @Test
    public void testGetDuplicateCitiesDifferentCountries() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES 1000");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "cidade1: Angola,Portugal",
                "cidade2: Cabo Verde,Portugal"
        }, resultParts);

        result = Main.execute("GET_TOP_POPULATION_INCREASE 2020 2022");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Cabo Verde:2020-2022:1.77%",
                "Portugal:2020-2022:1.01%",
                "Cabo Verde:2020-2021:0.90%",
                "Cabo Verde:2021-2022:0.88%",
                "Portugal:2020-2021:0.53%"
        }, resultParts);
    }

    @Test
    public void testGetCitiesAtDistance1() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_AT_DISTANCE 1 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "cidade1->cidade2"
        }, resultParts);

        result = Main.execute("GET_CITIES_AT_DISTANCE 10 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "almada->carnaxide",
                "caparica->carcavelos",
                "carcavelos->queluz"
        }, resultParts);
    }

    @Test
    public void testGetCitiesAtDistance2() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_AT_DISTANCE2 2984 Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "carcavelos->praia"
        }, resultParts);

    }

    @Test
    public void testGetCitiesWithinRadius() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("GET_CITIES_WHITIN_RADIUS 300 lamego");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "almada",
                "caparica",
                "carcavelos",
                "carnaxide",
                "queluz"
        }, resultParts);

    }

    @Test
    public void testInsertCity() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("INSERT_CITY 1 sabonada 1 123");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Pais invalido"
        }, resultParts);

        result = Main.execute("INSERT_CITY pt lisboa 04 2");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Inserido com sucesso"
        }, resultParts);
    }

    @Test
    public void testRemoveCountry() {
        assertTrue(Main.parseFiles(new File("test-files")));

        Result result = Main.execute("REMOVE_COUNTRY Sabao");
        assertNotNull(result);
        assertTrue(result.success);
        String[] resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Pais invalido"
        }, resultParts);

        result = Main.execute("REMOVE_COUNTRY Portugal");
        assertNotNull(result);
        assertTrue(result.success);
        resultParts = result.result.split("\n");
        assertArrayEquals(new String[]{
                "Removido com sucesso"
        }, resultParts);
    }


}
