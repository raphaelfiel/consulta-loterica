import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;

public class Main {


    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper(); //criado uma vez só, fora do laço

        //Coleta do concurso inicial
        int concurso = 0;
        HttpRequest request_inicio = HttpRequest.newBuilder()
                .uri(URI.create("https://servicebus2.caixa.gov.br/portaldeloterias/api/megasena"))
                .GET()
                .build();

        HttpResponse<String> response_inicio = client.send(request_inicio, HttpResponse.BodyHandlers.ofString());
        System.out.println("Primeiro request: \n" + response_inicio.body());
        JsonNode raiz_inicio = mapper.readTree(response_inicio.body());
        concurso = raiz_inicio.get("numero").asInt();


        //Primeiro e segundo metodo
        ArrayList<Integer> listaDezenas = new ArrayList<Integer>();
        int[] dezenas_ = new int[60];
        for (int i = 0; i < dezenas_.length; i++) {
            dezenas_[i] = i + 1; //Mega-Sena vai de 1 a 60
        }

        //Terceiro metodo
        ArrayList<Integer> ganhadoresQuina = new ArrayList<>();
        ganhadoresQuina.add(Integer.MAX_VALUE); //concurso
        ganhadoresQuina.add(Integer.MAX_VALUE); //quantia de ganhadores

        //Quarto metodo
        ArrayList<Integer> quantiaSemGanhadoresSen = new ArrayList<>();
        quantiaSemGanhadoresSen.add(0);

        //Concurso com maior premio na quina
        ArrayList<Integer> concursoMaiorPremioQuina = new ArrayList<>();
        concursoMaiorPremioQuina.add(0);
        concursoMaiorPremioQuina.add(0);

        for (int i = concurso; i > Integer.MIN_VALUE; i--) {
            String url = String.format("https://servicebus2.caixa.gov.br/portaldeloterias/api/megasena/%s", i);

            //Area da requisição
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //HttpRequest e HttpResponse são imutáveis, é necessário uma nova declaração para cada requisição

            System.out.println(response.body() + "\nCONCURSO: " + i);

            //Controle do JSON
            JsonNode raiz = mapper.readTree(response.body());
            JsonNode dataApuracao = raiz.get("dataApuracao");
            String data = dataApuracao.asText();
            if(!data.endsWith("2026"))
            {
                break;
            }
            //Lista de frequencia (METODO 1 E 2)
            JsonNode dezenas = raiz.get("listaDezenas");
            for (JsonNode dezenaNode : dezenas) //for each -> for(Tipo nomeVariavel : colecao)
            {
                int dezena = dezenaNode.asInt();
                listaDezenas.add(dezena);
            }

            //Concurso com menor numero de ganhadores na quina (METODO 3)
            menosGanhadoresQuina(raiz, i, ganhadoresQuina);
            semGanhadoresSena(raiz, quantiaSemGanhadoresSen);



            Thread.sleep(300); //Aguarda 300ms antes da proxima requisição
        }
        System.out.println("------------------------------------------");
        tresFrequentes(dezenas_, listaDezenas);
        System.out.println("------------------------------------------");
        tresInfrequentes(listaDezenas);

        System.out.println("------------------------------------------");
        System.out.println("Concurso(s) com menos ganhadores na quina:");
        for(int k = 0; k < ganhadoresQuina.size(); k += 2)
        {
            System.out.println("O concurso " + ganhadoresQuina.get(k) + " teve: " + ganhadoresQuina.get(k + 1) + " ganhadores.");
        }

        System.out.println("------------------------------------------");

        System.out.println("Concursos sem ganhadores na sena:");
            System.out.println(quantiaSemGanhadoresSen.get(0) + " concursos ficaram sem ganhadores na sena.");
        System.out.println("------------------------------------------");
        System.out.println("Concurso com maior premio na quina:");
            System.out.println("O concurso " + concursoMaiorPremioQuina.get(0) + " teve o premio na quina de: " + concursoMaiorPremioQuina.get(1) + "R$");

    }

    //Metodo 1
    public static void tresFrequentes(int[] dezenas_, ArrayList<Integer> listaDezenas) {
        int[] quantiaAparicoes = new int[60];

        for (int dezena : listaDezenas) {
            quantiaAparicoes[dezena - 1]++;
        }
        //Utilizando a posição como o numero, valor para quantia de repetições

        int[] podio = {0, 0, 0};
        int[] mis_rak_max_csp = {0, 0, 0};

        for (int i = 0; i < quantiaAparicoes.length; i++) {
            if (quantiaAparicoes[i] > podio[0]) {
                podio[2] = podio[1];
                mis_rak_max_csp[2] = mis_rak_max_csp[1];
                podio[1] = podio[0];
                mis_rak_max_csp[1] = mis_rak_max_csp[0];
                podio[0] = quantiaAparicoes[i];
                mis_rak_max_csp[0] = i + 1;
            } else if (quantiaAparicoes[i] > podio[1]) {
                podio[2] = podio[1];
                mis_rak_max_csp[2] = mis_rak_max_csp[1];
                podio[1] = quantiaAparicoes[i];
                mis_rak_max_csp[1] = i + 1;
            } else if (quantiaAparicoes[i] > podio[2]) {
                podio[2] = quantiaAparicoes[i];
                mis_rak_max_csp[2] = i + 1;
            }
        }

        System.out.println("Numeros mais repetidos:");
        for (int i = 0; i < podio.length; i++) {
            System.out.println((i + 1) + "º " + mis_rak_max_csp[i] + " repetiu " + podio[i] + " vezes");
        }
    }

    //Metodo 2
    public static void tresInfrequentes(ArrayList<Integer> listaDezenas) {
        int[] quantiaAparicoes = new int[60];

        for (int dezena : listaDezenas) {
            quantiaAparicoes[dezena - 1]++;
        }

        int[] podio = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        int[] mis_rak_min_csp = {0, 0, 0};

        for (int i = 0; i < quantiaAparicoes.length; i++) {
            if (quantiaAparicoes[i] < podio[0]) {
                podio[2] = podio[1];
                mis_rak_min_csp[2] = mis_rak_min_csp[1];
                podio[1] = podio[0];
                mis_rak_min_csp[1] = mis_rak_min_csp[0];
                podio[0] = quantiaAparicoes[i];
                mis_rak_min_csp[0] = i + 1;
            } else if (quantiaAparicoes[i] < podio[1]) {
                podio[2] = podio[1];
                mis_rak_min_csp[2] = mis_rak_min_csp[1];
                podio[1] = quantiaAparicoes[i];
                mis_rak_min_csp[1] = i + 1;
            } else if (quantiaAparicoes[i] < podio[2]) {
                podio[2] = quantiaAparicoes[i];
                mis_rak_min_csp[2] = i + 1;
            }
        }

        System.out.println("Numeros menos repetidos:");
        for (int i = 0; i < podio.length; i++) {
            System.out.println((i + 1) + "º " + mis_rak_min_csp[i] + " repetiu " + podio[i] + " vezes");
        }
    }

    //Metodo 3
    public static void menosGanhadoresQuina(JsonNode raiz, int i, ArrayList<Integer> ganhadoresQuina) {
        //Concurso com menor numero de ganhadores na quina (METODO 3)
        JsonNode listaQuina = raiz.at("/listaRateioPremio/1/numeroDeGanhadores");
        int quantiaGanhadores = listaQuina.asInt();

        if (quantiaGanhadores < ganhadoresQuina.get(1)) {
            ganhadoresQuina.clear();
            ganhadoresQuina.add(i); //Numero do concurso
            ganhadoresQuina.add(quantiaGanhadores);//Quantia de ganhadores
        } else if (quantiaGanhadores == ganhadoresQuina.get(1)) {
            ganhadoresQuina.add(i);
            ganhadoresQuina.add(quantiaGanhadores);
        }
    }

    //Metodo 4
    public static void semGanhadoresSena(JsonNode raiz, ArrayList<Integer> quantiaSemGanhadores)
    {
        int ganhadoresSena = raiz.at("/listaRateioPremio/0/numeroDeGanhadores").asInt();
        if(ganhadoresSena == 0);
        {
            quantiaSemGanhadores.set(0, quantiaSemGanhadores.get(0) + 1);
        }
    }
}