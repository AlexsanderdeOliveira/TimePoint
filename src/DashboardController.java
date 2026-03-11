import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import com.google.gson.*;

public class DashboardController implements HttpHandler {
    private final PontoDAO pontoDAO = new PontoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = GsonUtil.GSON;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        String metodo  = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();
        String resposta = "";
        int status = 200;

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            // Extrai o ID do path: /dashboard/{id}
            String[] partes = caminho.split("/");
            if (partes.length < 3) {
                status = 400;
                resposta = "ID do usuário não informado";
            } else {
                int funcionarioId = Integer.parseInt(partes[2]);
                Usuario funcionario = usuarioDAO.buscarPorId(funcionarioId);

                if (funcionario == null) {
                    status = 404;
                    resposta = "Usuário não encontrado";

                } else if ("GET".equalsIgnoreCase(metodo)) {
                    resposta = gson.toJson(pontoDAO.listar7UltimosDoUsuario(funcionarioId));

                } else if ("POST".equalsIgnoreCase(metodo)) {

                    Ponto ponto = pontoDAO.buscarPorUsuarioEData(funcionarioId, LocalDate.now());

                    if (ponto == null) {
                        ponto = new Ponto(funcionarioId, LocalDate.now(), LocalTime.now());
                        pontoDAO.criar(ponto);
                        resposta = "Chegada registrada em " + LocalTime.now();
                    } else if (ponto.getHorarioSaidaAlmoco() == null) {
                        ponto.setHorarioSaidaAlmoco(LocalTime.now());
                        pontoDAO.atualizar(ponto);
                        resposta = "Saída para almoço registrada em " + LocalTime.now();
                    } else if (ponto.getHorarioVoltaAlmoco() == null) {
                        ponto.setHorarioVoltaAlmoco(LocalTime.now());
                        pontoDAO.atualizar(ponto);
                        resposta = "Volta do almoço registrada em " + LocalTime.now();
                    } else if (ponto.getHorarioSaida() == null) {
                        ponto.setHorarioSaida(LocalTime.now());
                        pontoDAO.atualizar(ponto);
                        resposta = "Saída registrada em " + LocalTime.now();
                    } else {
                        status = 400;
                        resposta = "Todos os pontos do dia já foram registrados";
                    }
                } else {
                    status = 405;
                    resposta = "Método não permitido";
                }
            }
        } catch (NumberFormatException e) {
            status = 400;
            resposta = "ID inválido";
        } catch (Exception e) {
            status = 500;
            resposta = "Erro: " + e.getMessage();
            e.printStackTrace();
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, resposta.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(resposta.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}