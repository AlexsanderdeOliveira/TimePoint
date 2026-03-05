public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String email;
    protected String cargo;
    protected String turno;
    protected String senha;

    public Usuario() {}

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public Usuario(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Usuario(int id, String nome, String email, String cargo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
    }

    public Usuario(int id, String nome, String email, String cargo, String turno) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.turno = turno;
    }

    public Usuario(int id, String nome, String email, String cargo, String turno, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.turno = turno;
        this.senha = senha;
    }

    public abstract void redirecionarParaDashboard();

    // GETTERS E SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}



