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
                        rs.getTime("horario_chegada") != null ? rs.getTime("horario_chegada").toLocalTime() : null,
                        rs.getTime("horario_saida_almoco") != null ? rs.getTime("horario_saida_almoco").toLocalTime() : null,
                        rs.getTime("horario_volta_almoco") != null ? rs.getTime("horario_volta_almoco").toLocalTime() : null,
                        rs.getTime("horario_saida") != null ? rs.getTime("horario_saida").toLocalTime() : null
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
        String sql = "UPDATE registro SET horario_saida_almoco = ?, " +
                "horario_volta_almoco = ?, horario_saida = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTime(1, ponto.getHorarioSaidaAlmoco() != null ? java.sql.Time.valueOf(ponto.getHorarioSaidaAlmoco()) : null);
            stmt.setTime(2, ponto.getHorarioVoltaAlmoco() != null ? java.sql.Time.valueOf(ponto.getHorarioVoltaAlmoco()) : null);
            stmt.setTime(3, ponto.getHorarioSaida() != null ? java.sql.Time.valueOf(ponto.getHorarioSaida()) : null);
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

            stmt.setTime(1, ponto.getHorarioChegada() != null ? java.sql.Time.valueOf(ponto.getHorarioChegada()) : null);
            stmt.setTime(2, ponto.getHorarioSaidaAlmoco() != null ? java.sql.Time.valueOf(ponto.getHorarioSaidaAlmoco()) : null);
            stmt.setTime(3, ponto.getHorarioVoltaAlmoco() != null ? java.sql.Time.valueOf(ponto.getHorarioVoltaAlmoco()) : null);
            stmt.setTime(4, ponto.getHorarioSaida() != null ? java.sql.Time.valueOf(ponto.getHorarioSaida()) : null);
            stmt.setInt(5, ponto.getId());

            stmt.executeUpdate();
            System.out.println("Ponto atualizado completamente com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}