public class Conta {

    private Double saldo;
    private Integer id;

    public Conta() {
        this.saldo = 0.0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public void adicionarSaldo(Double valor) {
        if (valor != null && valor > 0) {
            this.saldo += valor;
        }
    }

    public void subtrairSaldo(double valor) {
        if (saldo >= valor) {
            saldo -= valor;
        } else {
            System.out.println("Saldo insuficiente para realizar a operação.");
        }
    }
}