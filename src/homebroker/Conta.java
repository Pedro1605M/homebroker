import java.util.ArrayList;

public class Conta{
    
    private Pessoa conta;
    private Double saldo;
    private Integer numeroDaConta;
    private Modalidade deposito;
    ArrayList<Acoes> acoes = new ArrayList();
    
    public Conta(Pessoa conta, Double saldo, Integer numeroDaConta, Modalidade deposito, ArrayList<Acoes> acoes) {
        this.conta = conta;
        this.saldo = saldo;
        this.numeroDaConta = numeroDaConta;
        this.deposito = deposito;
        this.acoes = acoes;
    }

    public Pessoa getConta() {
        return conta;
    }

    public void setConta(Pessoa conta) {
        this.conta = conta;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Integer getNumeroDaConta() {
        return numeroDaConta;
    }

    public void setNumeroDaConta(Integer numeroDaConta) {
        this.numeroDaConta = numeroDaConta;
    }

    public Modalidade getDeposito() {
        return deposito;
    }

    public void setDeposito(Modalidade deposito) {
        this.deposito = deposito;
    }

    public ArrayList<Acoes> getAcoes() {
        return acoes;
    }

    public void setAcoes(ArrayList<Acoes> acoes) {
        this.acoes = acoes;
    }
    
    public void sacar(Double valor){

    }

    
}
