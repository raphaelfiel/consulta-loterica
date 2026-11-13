import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;

public class Main{

    public static void main(String[] args) throws Exception
    {
        int[] dezenas_ = new int[60];
        for(int i = 0; i < 60; i++)
        {
            dezenas_[i] = i + 1;
        }

        ArrayList<Integer> listaDezenas = new ArrayList<Integer>();

        HttpClient client = HttpClient.newHttpClient();


        int concurso = 2955; //Primeiro concurso do ano de 2026, foi de 2955 Até 3084;

        //Requisições para API
        for(int i = concurso; i <= 2956; i++)
        {
            String url = String.format("https://servicebus2.caixa.gov.br/portaldeloterias/api/megasena/%s", i);

            //Area da requisição
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //HttpRequest e HttpResponse são imutaveis, é necessario uma nova declaração para cada requisição

            System.out.println(response.body() + "\nCONCURSO: " + i);
            concurso = i;

            //Controle do JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dados = mapper.readTree(response.body());

            JsonNode dezenas = dados.get("listaDezenas");
            for(JsonNode dezenaNode: dezenas) //for each -> for(Tipo nomeVariavel : colecao)
            {
                int dezena = dezenaNode.asInt();
                listaDezenas.add(dezena);
            }
            Thread.sleep(500); //Aguarda 0,5s antes da proxima requisição
        }

        tresFrequentes(dezenas_, listaDezenas);
        Thread.sleep(2000);
        tresInfrequentes(dezenas_, listaDezenas);
    }

     public static void tresFrequentes(int[] dezenas_, ArrayList<Integer> listaDezenas)
        {
            int[] quantiaAparicoes = new int[60];

            for(int i = 0; i < listaDezenas.size(); i++)
            {
                for(int j = 0; j < dezenas_.length; j++)
                {
                    if(dezenas_[j] == listaDezenas.get(i))
                    {
                        quantiaAparicoes[j]++; //aumenta em um o numero já existente no array
                    }
                }
            }
            //Utilizando a posição como o numero, valor para quantia de repetições

            for(int i = 0; i < quantiaAparicoes.length; i++)
            {
                System.out.println("Numero: " + (i + 1) + " repetiu " + quantiaAparicoes[i] + " vez(es)");
            }

            int[] podio = {0, 0, 0};
            int[] mis_rak_max_csp = {0, 0, 0};

            for(int i = 0; i < quantiaAparicoes.length; i++)
            {
                if(quantiaAparicoes[i] > podio[0])
                {
                    mis_rak_max_csp[0] = i + 1;
                    podio[0] = quantiaAparicoes[i];
                }else if (quantiaAparicoes[i] > podio[1] && quantiaAparicoes[i] < podio[0])
                {
                    mis_rak_max_csp[1] = i + 1;
                    podio[1] = quantiaAparicoes[i];
                }else if(quantiaAparicoes[i] > podio[2] && quantiaAparicoes[i] < podio[1])
                {
                    mis_rak_max_csp[2] = i + 1;
                    podio[2] = quantiaAparicoes[i];
                }

            }

            for(int i = 0; i < podio.length; i++)
            {
                System.out.println((i+1) + "º " + mis_rak_max_csp[i] + " repetiu " + podio[i] + " vezes");
            }

        }

        public static void tresInfrequentes(int[] dezenas_, ArrayList<Integer> listaDezenas)
        {
            int[] quantiaAparicoes = new int[60];

            for(int i = 0; i < listaDezenas.size(); i++)
            {
                for(int j = 0; j < dezenas_.length; j++)
                {
                    if(dezenas_[j] == listaDezenas.get(i))
                    {
                        quantiaAparicoes[j]++; //aumenta em um o numero já existente no array
                    }
                }
            }
            //Utilizando a posição como o numero, valor para quantia de repetições

            for(int i = 0; i < quantiaAparicoes.length; i++)
            {
                System.out.println("Numero: " + (i + 1) + " repetiu " + quantiaAparicoes[i] + " vez(es)");
            }

            int[] podio = {60, 60, 60};
            int[] mis_rak_min_csp = {60, 60, 60};

            System.out.println(podio[0] + " " + podio[1] + " " + podio[2]);
            for(int i = 0; i < quantiaAparicoes.length; i++)
            {
                if(quantiaAparicoes[i] == 0)
                {
                    continue;
                }

                if(quantiaAparicoes[i] < podio[0])
                {
                    podio[2] = podio[1];
                    mis_rak_min_csp[2] = mis_rak_min_csp[1];
                    podio[1] = podio[0];
                    mis_rak_min_csp[1] = mis_rak_min_csp[0];
                    podio[0] = quantiaAparicoes[i];
                    mis_rak_min_csp[0] = i + 1;
                }
                else if(quantiaAparicoes[i] < podio[1])
                {
                    podio[2] = podio[1];
                    mis_rak_min_csp[2] = mis_rak_min_csp[1];
                    podio[1] = quantiaAparicoes[i];
                    mis_rak_min_csp[1] = i + 1;
                }
                else if(quantiaAparicoes[i] < podio[2])
                {
                    podio[2] = quantiaAparicoes[i];
                    mis_rak_min_csp[2] = i + 1;
                }
            }

            for(int i = 0; i < podio.length; i++)
            {
                System.out.println((i+1) + "º " + mis_rak_min_csp[i] + " repetiu " + podio[i] + " vezes");
            }

        }


    }