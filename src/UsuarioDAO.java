import java.sql.*;
import java.util.*;

public class UsuarioDAO {

    public void criar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, email, cargo, turno, senha) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql))  {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getCargo());
            stmt.setString(4, usuario.getTurno());
            stmt.setString(5, usuario.getSenha());
            stmt.executeUpdate();
            System.out.println("Usuário cadastrado com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
