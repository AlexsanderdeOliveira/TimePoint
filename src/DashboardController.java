import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class DashboardController implements HttpHandler {
    private final PontoDAO pontoDAO = new PontoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(metodo) && caminho.equals("/dashboard")) {
                buscarDashboard(exchange);
            } else if ("POST".equalsIgnoreCase(metodo) && caminho.equals("/dashboard")) {
                registrarPonto(exchange);
            } else {
                enviarResposta(exchange, 404, "Rota não encontrada");
            }
        } catch (Exception e){
            enviarResposta(exchange, 500, "Erro interno: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void buscarDashboard(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();

            if (query == null || query.isEmpty()) {
                enviarResposta(exchange, 400, "UsuarioId é Obrigatório");
                return;
            }

            Map<String, String> parametros = extrairDados(query);
            String usuarioIdStr = parametros.get("usuarioId");

            if (usuarioIdStr == null || usuarioIdStr.isEmpty()) {
                enviarResposta(exchange, 400, "UsuarioId é Obrigatório");
                return;
            }

            int usuarioId = Integer.parseInt(usuarioIdStr);
            Usuario usuario = usuarioDAO.buscarPorId(usuarioId);

            if (usuario == null) {
                enviarResposta(exchange, 404, "Usuário não encontrado");
                return;
            }

            List<Ponto> ultimosPontos = pontoDAO.listar7UltimosDoUsuario(usuarioId);

            StringBuilder resposta = new StringBuilder();
            resposta.append("Bem-vindo, ").append(usuario.getNome()).append("!\n");
            resposta.append("Cargo: ").append(usuario.getCargo()).append("\n");
            resposta.append("\n=== Últimos Registros ===\n");

            if (ultimosPontos.isEmpty()) {
                resposta.append("Nenhum registro encontrado\n");
            } else {
                for (Ponto ponto : ultimosPontos) {
                    resposta.append("Data: ").append(ponto.getDataRegistro()).append(" | ");
                    resposta.append("Chegada: ").append(ponto.getHorarioChegada() != null ? ponto.getHorarioChegada() : "---").append(" | ");
                    resposta.append("Saída Almoço: ").append(ponto.getHorarioSaidaAlmoco() != null ? ponto.getHorarioSaidaAlmoco() : "---").append(" | ");
                    resposta.append("Volta Almoço: ").append(ponto.getHorarioVoltaAlmoco() != null ? ponto.getHorarioVoltaAlmoco() : "---").append(" | ");
                    resposta.append("Saída: ").append(ponto.getHorarioSaida() != null ? ponto.getHorarioSaida() : "---").append("\n");
                }
            }

            enviarResposta(exchange, 200, resposta.toString());

        } catch (NumberFormatException e) {
            enviarResposta(exchange, 400, "usuarioId deve ser um número");
        } catch (Exception e) {
            enviarResposta(exchange, 500, "Erro ao buscar dashboard: " + e.getMessage());
        }
    }

    private void registrarPonto(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String corpo = reader.readLine();

            Map<String, String> dados = extrairDados(corpo);
            String usuarioIdStr = dados.get("usuarioId");

            if (usuarioIdStr == null || usuarioIdStr.isEmpty()) {
                enviarResposta(exchange, 400, "usuarioId é Obrigatório");
                return;
            }

            int usuarioId = Integer.parseInt(usuarioIdStr);
            Ponto ponto = pontoDAO.buscarPorUsuarioEData(usuarioId, LocalDate.now());

            if (ponto == null) {
                criarPonto(usuarioId, exchange);
            } else {
                atualizarPonto(ponto, exchange);
            }

        } catch (NumberFormatException e) {
            enviarResposta(exchange, 400, "usuarioId deve ser um número");
        } catch (Exception e) {
            enviarResposta(exchange, 500, "Erro ao registrar ponto: " + e.getMessage());
        }
    }

    private void criarPonto(int usuarioId, HttpExchange exchange) throws IOException {
        try {
            Ponto novoPonto = new Ponto(usuarioId, LocalDate.now(), LocalTime.now());
            pontoDAO.criar(novoPonto);
            enviarResposta(exchange, 200, "Chegada registrada com sucesso em " + LocalTime.now());
        } catch (Exception e) {
            enviarResposta(exchange, 500, "Erro ao registrar chegada: " + e.getMessage());
        }
    }

    private void atualizarPonto(Ponto ponto, HttpExchange exchange) throws IOException {
        try {
                // Saida para o Almoço
            if (ponto.getHorarioSaidaAlmoco() == null) {
                ponto.setHorarioSaidaAlmoco(LocalTime.now());
                pontoDAO.atualizar(ponto);
                enviarResposta(exchange, 200, "Saida para almoço registrada em " + LocalTime.now());

                // Volta do almoço
            } else if (ponto.getHorarioVoltaAlmoco() == null) {
                ponto.setHorarioVoltaAlmoco(LocalTime.now());
                pontoDAO.atualizar(ponto);
                enviarResposta(exchange, 200, "Volta do almoço registrada em " + LocalTime.now());

                // Saida do trabalho
            } else if (ponto.getHorarioSaida() == null) {
                ponto.setHorarioSaida(LocalTime.now());
                pontoDAO.atualizar(ponto);
                enviarResposta(exchange, 200, "Saída do trabalho registrada em " + LocalTime.now());

                // Todos os pontos registrados
            } else {
                enviarResposta(exchange, 400, "Todos os pontos do dia já foram registrados");
            }

        } catch (Exception e) {
            enviarResposta(exchange, 500, "Erro ao atualizar ponto: " + e.getMessage());
        }
    }

    private Map<String, String> extrairDados(String corpo) {
        Map<String, String> dados = new HashMap<>();

        if (corpo == null || corpo.isEmpty()) {
            return dados;
        }

        // Dividir por "&" e separar o email e a senha
        String[] partes =  corpo.split("&");

        for (String parte : partes) {
            // Divide cada parte por "=" para separar chave e valor
            String[] chaveValor = parte.split("=");
            if(chaveValor.length == 2) {
                dados.put(chaveValor[0], chaveValor[1]);
            }
        }

        return dados;
    }

    private void enviarResposta(HttpExchange exchange, int status, String mensagem) throws IOException {
        exchange.getResponseHeaders().set("Content-type", "text/plain; charset=utf-8");
        byte[] response = mensagem.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
