import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Servidor {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/login",              new LoginController());
        server.createContext("/usuarios",           new UsuariosController()); // ✅ nova rota
        server.createContext("/dashboard",          new DashboardController());
        server.createContext("/dashboard-gerente",  new DashboardGerenteController());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor rodando em http://localhost:8080/login");
    }
}