import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Servidor {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/login", new UsuarioController());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor rodando em http://localhist:8080/usuarios");
    }
}

