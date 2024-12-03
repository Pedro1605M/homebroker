import java.util.ArrayList;

public class Conta{
    
    private Pessoa conta;
    private Double saldo;
    private  ArrayList<String> carteiraAcoes = new ArrayList();
    
    public ArrayList<String> getCarteiraAcoes() {
        return carteiraAcoes;
    }

    public void setCarteiraAcoes(ArrayList<String> carteiraAcoes) {
        this.carteiraAcoes = carteiraAcoes;
    }

    private Integer numeroDaConta;

    public Pessoa getConta() {
        return conta;
    }

    public void setConta(Pessoa conta) {
        this.conta = conta;
    }

    public Conta() {
        this.saldo = 0.0; // Inicializa o saldo com 0.0
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

    public Integer getNumeroDaConta() {
        return numeroDaConta;
    }

    public void setNumeroDaConta(Integer numeroDaConta) {
        this.numeroDaConta = numeroDaConta;
    }


    
}
