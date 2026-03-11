import java.sql.*;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class PontoDAO {

    public Ponto buscarPorUsuarioEData(int usuarioId, LocalDate data) {
        String sql = "SELECT * FROM registro WHERE usuarios_id = ? AND data_registro = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setDate(2, java.sql.Date.valueOf(data));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Ponto(
                        rs.getInt("id"),
                        rs.getInt("usuarios_id"),
                        rs.getDate("data_registro").toLocalDate(),
                        rs.getTime("horario_chegada")      != null ? rs.getTime("horario_chegada").toLocalTime()      : null,
                        rs.getTime("horario_saida_almoco") != null ? rs.getTime("horario_saida_almoco").toLocalTime() : null,
                        rs.getTime("horario_volta_almoco") != null ? rs.getTime("horario_volta_almoco").toLocalTime() : null,
                        rs.getTime("horario_saida")        != null ? rs.getTime("horario_saida").toLocalTime()        : null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void criar(Ponto ponto) {
        String sql = "INSERT INTO registro (usuarios_id, data_registro, horario_chegada) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ponto.getUsuarioId());
            stmt.setDate(2, java.sql.Date.valueOf(ponto.getDataRegistro()));
            stmt.setTime(3, java.sql.Time.valueOf(ponto.getHorarioChegada()));
            stmt.executeUpdate();
            System.out.println("Ponto criado com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Ponto ponto) {
        String sql = "UPDATE registro SET horario_saida_almoco = ?, horario_volta_almoco = ?, horario_saida = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, ponto.getHorarioSaidaAlmoco());
            stmt.setObject(2, ponto.getHorarioVoltaAlmoco());
            stmt.setObject(3, ponto.getHorarioSaida());
            stmt.setInt(4, ponto.getId());
            stmt.executeUpdate();
            System.out.println("Ponto atualizado com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarCompleto(Ponto ponto) {
        String sql = "UPDATE registro SET horario_chegada = ?, horario_saida_almoco = ?, horario_volta_almoco = ?, horario_saida = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, ponto.getHorarioChegada());
            stmt.setObject(2, ponto.getHorarioSaidaAlmoco());
            stmt.setObject(3, ponto.getHorarioVoltaAlmoco());
            stmt.setObject(4, ponto.getHorarioSaida());
            stmt.setInt(5, ponto.getId());
            stmt.executeUpdate();
            System.out.println("Ponto atualizado completamente com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Ponto> listar7UltimosDoUsuario(int usuarioId) {
        List<Ponto> pontos = new ArrayList<>();
        String sql = "SELECT * FROM registro WHERE usuarios_id = ? ORDER BY data_registro DESC LIMIT 7";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ponto p = new Ponto(
                        rs.getInt("id"),
                        rs.getInt("usuarios_id"),
                        rs.getDate("data_registro").toLocalDate(),
                        rs.getTime("horario_chegada")      != null ? rs.getTime("horario_chegada").toLocalTime()      : null,
                        rs.getTime("horario_saida_almoco") != null ? rs.getTime("horario_saida_almoco").toLocalTime() : null,
                        rs.getTime("horario_volta_almoco") != null ? rs.getTime("horario_volta_almoco").toLocalTime() : null,
                        rs.getTime("horario_saida")        != null ? rs.getTime("horario_saida").toLocalTime()        : null
                );
                pontos.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pontos;
    }
    public List<Map<String, Object>> listar7UltimosRegistros() {
        List<Map<String, Object>> registros = new ArrayList<>();
        String sql = """
                SELECT r.id,
                       r.usuarios_id        AS usuariosId,
                       u.nome               AS nomeUsuario,
                       r.data_registro      AS dataRegistro,
                       r.horario_chegada    AS horarioChegada,
                       r.horario_saida_almoco  AS horarioSaidaAlmoco,
                       r.horario_volta_almoco  AS horarioVoltaAlmoco,
                       r.horario_saida      AS horarioSaida
                FROM registro r
                JOIN usuarios u ON u.id = r.usuarios_id
                ORDER BY r.data_registro DESC, r.id DESC
                LIMIT 50
                """;

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id",                 rs.getInt("id"));
                row.put("usuariosId",         rs.getInt("usuariosId"));
                row.put("nomeUsuario",        rs.getString("nomeUsuario"));
                row.put("dataRegistro",       String.valueOf(rs.getDate("dataRegistro")));
                row.put("horarioChegada",     rs.getString("horarioChegada"));
                row.put("horarioSaidaAlmoco", rs.getString("horarioSaidaAlmoco"));
                row.put("horarioVoltaAlmoco", rs.getString("horarioVoltaAlmoco"));
                row.put("horarioSaida",       rs.getString("horarioSaida"));
                registros.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registros;
    }


    public void excluirPorUsuario(int usuarioId) {
        String sql = "DELETE FROM registro WHERE usuarios_id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
            System.out.println("Registros de ponto do usuário " + usuarioId + " excluídos.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
























}