public class Funcionario extends Usuario {
    public Funcionario() {
        this.cargo = "Funcionario";
    }

    public Funcionario(int id, String nome, String email) {
        super(id, nome, email, "Funcionario");
    }

    public Funcionario(int id, String nome, String email, String turno) {
        super(id, nome, email, "Funcionario", turno);
    }

    public Funcionario(int id, String nome, String email, String turno, String senha) {
        super(id, nome, email, "Funcionario", turno, senha);
    }

    @Override
    public void redirecionarParaDashboard() {
        System.out.println("Funcionario" + getNome() +"Redirecionado");
    }
}