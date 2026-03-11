import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import com.google.gson.*;

public class DashboardGerenteController implements HttpHandler {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PontoDAO pontoDAO = new PontoDAO();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();
        String resposta =  "";
        int status = 200;

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            // GET /dashboard-gerente/3 - ver dashboard do gerente com os ultimos 7 funcioanarios
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
            } // POST /dashboard-gerente/3 - Gerente registra seu proprio ponto
            else if ("POST".equalsIgnoreCase(metodo)){
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
                            // se for primeiro clique, não vai ter registro, logo vai criar um
                            ponto = new Ponto(gerenteId, LocalDate.now(), LocalTime.now());
                            pontoDAO.criar(ponto);
                            resposta = "Chegada registrada em " + LocalTime.now();
                        } else {
                            // 2º, 3º e 4º cliques -  atualizar ponto
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
            } // PUT /dashboard-gerente/1 - Editar usuário





            else if ("PUT".equalsIgnoreCase(metodo)) {
                String[] partes = caminho.split("/");
                if (partes.length >= 3) {
                    int id = Integer.parseInt(partes[2]);

                    String jsonBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

                    if (json.has("nome") || json.has("email") || json.has("cargo") || json.has("turno") || json.has("senha")) {
                        Usuario usuario = usuarioDAO.buscarPorId(id);
                        if (usuario != null) {
                            if (json.has("nome")) usuario.setNome(json.get("nome").getAsString());
                            if (json.has("email")) usuario.setEmail(json.get("email").getAsString());
                            if (json.has("cargo")) usuario.setCargo(json.get("cargo").getAsString());
                            if (json.has("turno")) usuario.setTurno(json.get("turno").getAsString());
                            if (json.has("senha")) usuario.setSenha(json.get("senha").getAsString());
                            usuarioDAO.atualizar(usuario);
                        }
                    }


                } else {
                    status = 400;
                    resposta = "ID não informado";
                }



            } // DELETE /dashboard-gerente/1 - Deletar usuário
            else if ("DELETE".equalsIgnoreCase(metodo)) {
                String[] partes = caminho.split("/");
                if (partes.length >= 3) {
                    int id = Integer.parseInt(partes[2]);
                    Usuario usuario = usuarioDAO.buscarPorId(id);

                    if (usuario == null) {
                        status = 404;
                        resposta = "Usuário não encontrado";
                    } else {
                        usuarioDAO.excluir(id);
                        resposta = "Usuário deletado com sucesso";
                    }
                } else {
                    status = 400;
                    resposta = "ID não informado";
                }
            } // NENHUMA ACIMA
            else {
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
