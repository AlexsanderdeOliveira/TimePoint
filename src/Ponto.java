import java.time.LocalDate;
import java.time.LocalTime;

public class Ponto {
    private int id;
    private int usuarioId;
    private LocalDate dataRegistro;
    private LocalTime horarioChegada;
    private LocalTime horarioSaidaAlmoco;
    private LocalTime horarioVoltaAlmoco;
    private LocalTime horarioSaida;

    public Ponto() {}

    //Registrar Chegada
    public Ponto(int usuarioId, LocalDate dataRegistro, LocalTime horarioChegada) {
        this.usuarioId = usuarioId;
        this.dataRegistro = dataRegistro;
        this.horarioChegada = horarioChegada;
    }

    // Construtor Geral
    public Ponto(int id, int usuarioId, LocalDate dataRegistro, LocalTime horarioChegada, LocalTime horarioSaidaAlmoco, LocalTime horarioVoltaAlmoco, LocalTime horarioSaida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.dataRegistro = dataRegistro;
        this.horarioChegada = horarioChegada;
        this.horarioSaidaAlmoco = horarioSaidaAlmoco;
        this.horarioVoltaAlmoco = horarioVoltaAlmoco;
        this.horarioSaida = horarioSaida;
    }

    // GETTERS E SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }

    public LocalTime getHorarioChegada() { return horarioChegada; }
    public void setHorarioChegada(LocalTime horarioChegada) { this.horarioChegada = horarioChegada; }

    public LocalTime getHorarioSaidaAlmoco() { return horarioSaidaAlmoco; }
    public void setHorarioSaidaAlmoco(LocalTime horarioSaidaAlmoco) { this.horarioSaidaAlmoco = horarioSaidaAlmoco; }

    public LocalTime getHorarioVoltaAlmoco() { return horarioVoltaAlmoco; }
    public void setHorarioVoltaAlmoco(LocalTime horarioVoltaAlmoco) { this.horarioVoltaAlmoco = horarioVoltaAlmoco; }

    public LocalTime getHorarioSaida() { return horarioSaida; }
    public void setHorarioSaida(LocalTime horarioSaida) { this.horarioSaida = horarioSaida; }

}
