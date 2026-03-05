public class Gerente extends Usuario {
    public Gerente() {
        this.cargo = "Funcionario";
    }

    public Gerente(int id, String nome, String email ){
        super(id, nome, email, "Gerente");
    }

    public Gerente(int id, String nome, String email, String turno){
        super(id, nome, email, "Gerente", turno);
    }

    public Gerente(int id, String nome, String email, String turno, String senha){
        super(id, nome, email, "Gerente", turno, senha);
    }

    @Override
    public void redirecionarParaDashboard(){
        System.out.println("Funcionário" + getNome() + "Redirecionado" );
    }
}