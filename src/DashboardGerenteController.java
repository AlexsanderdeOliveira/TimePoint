import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DashboardGerenteController implements HttpHandler {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PontoDAO pontoDAO = new PontoDAO();

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
            // GET /dashboard-gerente?gerenteId=1 - ver todos os usuarios
            if ("GET".equalsIgnoreCase(metodo) && caminho.equals("/dashboard-gerente")) {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("listar")) {
                    listarTodosUsuarios(exchange);
                } else {
                    buscarDashboardGerente(exchange);
                }

                // PUT /dashboard-gerente - Atualizar usuario
            } else if ("PUT".equalsIgnoreCase(metodo) && caminho.equals("/dashboard-gerente")) {
                atualizarUsuario(exchange);

                // DELETE /dashboard-gerente?usuarioId=1 Deletar usuario
            } else if ("DELETE".equalsIgnoreCase(metodo) && caminho.equals("/dashboard-gerente")) {
                deletarUsuario(exchange);

            } else {
                enviarResposta(exchange, 404, "Rota não encontrada");
            }
        } catch (Exception e) {
            enviarResposta(exchange, 500, "Erro interno: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void buscarDashboardGerente(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();

            if (query == null || query.isEmpty()) {
                enviarResposta(exchange, 400, "GerenteId é obrigatório");
                return;
            }

            Map<String, String> parametros = extrairDados(query);
            String gerenteIdStr = parametros.get("gerenteId");

            if (gerenteIdStr == null || gerenteIdStr.isEmpty()) {
                enviarResposta(exchange, 400, "gerenteId é obrigatório");
                return;
            }

            int gerenteId = Integer.parseInt(gerenteIdStr);
            Usuario gerente = usuarioDAO.buscarPorId(gerenteId);

            if (gerente == null) {
                enviarResposta(exchange, 404, "Gerente não encontrado");
                return;
            }

            if (!gerente.g)


        }
    }



        // FEITO FEITO FEITO FEITO
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


  // final do codigo
}
