import java.util.ArrayList;

public class Conta{
    
    private Pessoa conta;
    private Double saldo;
    private Integer numeroDaConta;
    
    public Conta(Pessoa conta, Double saldo, Integer numeroDaConta) {
        this.conta = conta;
        this.saldo = saldo;
        this.numeroDaConta = numeroDaConta;
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
    
    public void sacar(Double valor){
        
    }

    
}
