import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import com.google.gson.*;

public class LoginController implements HttpHandler {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = GsonUtil.GSON;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();
        String resposta = "";
        int status = 200;

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("POST".equalsIgnoreCase(metodo) && caminho.equals("/login")) {
                String jsonBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

                String email = json.has("email") ? json.get("email").getAsString() : null;
                String senha = json.has("senha") ? json.get("senha").getAsString() : null;

                if (email == null || senha == null || email.isEmpty() || senha.isEmpty()) {
                    status = 400;
                    resposta = "Email e senha são obrigatórios";
                } else {
                    Usuario usuario = usuarioDAO.buscarPorEmail(email);

                    if (usuario != null && usuario.getSenha().equals(senha)) {
                        JsonObject usuarioJson = new JsonObject();
                        usuarioJson.addProperty("id", usuario.getId());
                        usuarioJson.addProperty("nome", usuario.getNome());
                        usuarioJson.addProperty("email", usuario.getEmail());
                        usuarioJson.addProperty("cargo", usuario.getCargo());
                        usuarioJson.addProperty("turno", usuario.getTurno());
                        resposta = gson.toJson(usuarioJson);
                    } else {
                        status = 401;
                        resposta = "Email ou senha incorretos";
                    }
                }
            } else {
                status = 404;
                resposta = "Rota não encontrada";
            }
        }  catch (Exception e) {
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
