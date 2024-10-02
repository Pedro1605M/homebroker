public abstract class Modalidade {
    
    private Double valor;

    
    
    public Modalidade(Double valor) {
        this.valor = valor;
    }

    public Double getValor() {
        return valor;
    }



    public void setValor(Double valor) {
        this.valor = valor;
    }

    public abstract void depositar(Double valor);

}
