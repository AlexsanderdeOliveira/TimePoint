import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import com.google.gson.*;

public class DashboardGerenteController implements HttpHandler {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PontoDAO pontoDAO = new PontoDAO();
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
            if ("GET".equalsIgnoreCase(metodo)) {
                String[] partes = caminho.split("/");
                if (partes.length >= 3) {
                    int gerenteId = Integer.parseInt(partes[2]);
                    Usuario gerente = usuarioDAO.buscarPorId(gerenteId);

                    if (gerente == null) {
                        status = 404;
                        resposta = "Gerente não encontrado";
                    } else if (!gerente.getCargo().equals("Gerente")) {
                        status = 403;
                        resposta = "Apenas gerentes podem acessar";
                    } else {
                        resposta = gson.toJson(pontoDAO.listar7UltimosRegistros());
                    }
                } else {
                    status = 400;
                    resposta = "ID não informado";
                }

            } else if ("POST".equalsIgnoreCase(metodo)) {
                String[] partes = caminho.split("/");
                if (partes.length >= 3) {
                    int gerenteId = Integer.parseInt(partes[2]);
                    Usuario gerente = usuarioDAO.buscarPorId(gerenteId);

                    if (gerente == null) {
                        status = 404;
                        resposta = "Gerente não encontrado";
                    } else if (!gerente.getCargo().equals("Gerente")) {
                        status = 403;
                        resposta = "Apenas gerentes podem registrar ponto";
                    } else {
                        Ponto ponto = pontoDAO.buscarPorUsuarioEData(gerenteId, LocalDate.now());

                        if (ponto == null) {
                            ponto = new Ponto(gerenteId, LocalDate.now(), LocalTime.now());
                            pontoDAO.criar(ponto);
                            resposta = "Chegada registrada em " + LocalTime.now();
                        } else {
                            if (ponto.getHorarioSaidaAlmoco() == null) {
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
                                resposta = "Saída do trabalho registrada em " + LocalTime.now();
                            } else {
                                status = 400;
                                resposta = "Todos os pontos já foram registrados";
                            }
                        }
                    }
                } else {
                    status = 400;
                    resposta = "ID não informado";
                }

            } else if ("PUT".equalsIgnoreCase(metodo)) {
                String[] partes = caminho.split("/");
                if (partes.length >= 3) {
                    int id = Integer.parseInt(partes[2]);

                    String jsonBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

                    if (json.has("nome") || json.has("email") || json.has("cargo") || json.has("turno") || json.has("senha")) {
                        Usuario usuario = usuarioDAO.buscarPorId(id);
                        if (usuario != null) {
                            if (json.has("nome"))  usuario.setNome(json.get("nome").getAsString());
                            if (json.has("email")) usuario.setEmail(json.get("email").getAsString());
                            if (json.has("cargo")) usuario.setCargo(json.get("cargo").getAsString());
                            if (json.has("turno")) usuario.setTurno(json.get("turno").getAsString());
                            if (json.has("senha")) usuario.setSenha(json.get("senha").getAsString());
                            usuarioDAO.atualizar(usuario);
                        }
                    }

                    if (json.has("dataRegistro") || json.has("horarioChegada") || json.has("horarioSaidaAlmoco") || json.has("horarioVoltaAlmoco") || json.has("horarioSaida")) {
                        LocalDate dataRegistro = json.has("dataRegistro")
                                ? LocalDate.parse(json.get("dataRegistro").getAsString())
                                : LocalDate.now();

                        Ponto ponto = pontoDAO.buscarPorUsuarioEData(id, dataRegistro);
                        if (ponto != null) {
                            if (json.has("horarioChegada"))
                                ponto.setHorarioChegada(LocalTime.parse(json.get("horarioChegada").getAsString()));
                            if (json.has("horarioSaidaAlmoco"))
                                ponto.setHorarioSaidaAlmoco(LocalTime.parse(json.get("horarioSaidaAlmoco").getAsString()));
                            if (json.has("horarioVoltaAlmoco"))
                                ponto.setHorarioVoltaAlmoco(LocalTime.parse(json.get("horarioVoltaAlmoco").getAsString()));
                            if (json.has("horarioSaida"))
                                ponto.setHorarioSaida(LocalTime.parse(json.get("horarioSaida").getAsString()));

                            pontoDAO.atualizarCompleto(ponto);
                        }
                    }

                    resposta = "Dados atualizados com sucesso";
                } else {
                    status = 400;
                    resposta = "ID não informado";
                }

            } else if ("DELETE".equalsIgnoreCase(metodo)) {
                String[] partes = caminho.split("/");
                if (partes.length >= 3) {
                    int id = Integer.parseInt(partes[2]);
                    Usuario usuario = usuarioDAO.buscarPorId(id);

                    if (usuario == null) {
                        status = 404;
                        resposta = "Usuário não encontrado";
                    } else {
                        pontoDAO.excluirPorUsuario(id);
                        usuarioDAO.excluir(id);
                        resposta = "Usuário e seus registros deletados com sucesso";
                    }
                } else {
                    status = 400;
                    resposta = "ID não informado";
                }

            } else {
                status = 400;
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