import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import com.google.gson.*;

public class UsuariosController implements HttpHandler {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = GsonUtil.GSON;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        String metodo = exchange.getRequestMethod();
        String resposta = "";
        int status = 200;

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("POST".equalsIgnoreCase(metodo)) {
                // Criar novo usuário
                String jsonBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

                String nome  = json.has("nome")  ? json.get("nome").getAsString()  : null;
                String email = json.has("email") ? json.get("email").getAsString() : null;
                String senha = json.has("senha") ? json.get("senha").getAsString() : null;
                String cargo = json.has("cargo") ? json.get("cargo").getAsString() : "Funcionário";
                String turno = json.has("turno") ? json.get("turno").getAsString() : "Manhã";

                if (nome == null || email == null || senha == null ||
                        nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    status = 400;
                    resposta = "Nome, email e senha são obrigatórios";
                } else {
                    Usuario novoUsuario = new Usuario(0, nome, email, cargo, turno, senha);
                    usuarioDAO.criar(novoUsuario);
                    status = 201;
                    resposta = "Colaborador cadastrado com sucesso";
                }

            } else if ("GET".equalsIgnoreCase(metodo)) {
                // Listar todos os usuários (para o dashboard do gerente)
                resposta = gson.toJson(usuarioDAO.listar());

            } else {
                status = 405;
                resposta = "Método não permitido";
            }
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