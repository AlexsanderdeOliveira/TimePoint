import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LoginController implements HttpHandler {
    private final UsuarioDAO dao = new UsuarioDAO();

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
            // se for POST em /login, fazer o login
            if("POST".equalsIgnoreCase(metodo) && caminho.equals("/login")) {
                fazerLogin(exchange);
            } else {
                // se não for POST em login, retorna o erro 404
                enviarResposta(exchange, 404, "Rota não encontrada");
            }
        }  catch (Exception e) {
            // se der erro retorna 500
            enviarResposta(exchange, 500, "Erro interno: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fazerLogin(HttpExchange exchange) throws IOException {
        // parte 1, ler o corpo da requisição
        InputStream inputStream = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String corpo = reader.readLine();

        //parte 2, extrair email e senha do corpo da requisição
        Map<String, String> dados = extrairDados(corpo);
        String email = dados.get("email");
        String senha = dados.get("senha");

        // parte 3, validar se email e senha foram enviados
        if (email == null || senha == null || email.isEmpty() || senha.isEmpty()) {
            enviarResposta(exchange, 400, "Email e senha são obrigatórios");
            return;
        }

        // parte 4, buscar usuario no banco de dados
        Usuario usuario = dao.buscarPorEmail(email);

        // parte 5, validar se usuario existe e se sua senha está correta
        if (usuario != null && usuario.getSenha().equals(senha)) {
            // Login ok -- retorna os dados do usuario
            String resposta = "Login realizado com sucesso. Bem-vindo, " + usuario.getNome() + "! com cargo: " + usuario.getCargo();
            enviarResposta(exchange, 200, resposta);
        } else {
            // Login falhou
            enviarResposta(exchange, 401, "Email ou senha incorretos");
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
