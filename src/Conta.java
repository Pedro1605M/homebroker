import java.util.ArrayList;

public class Conta {

private static int proximoId = 1; // Variável estática para rastrear o próximo ID

private Pessoa conta;
private Double saldo;
private ArrayList carteiraAcoes = new ArrayList<>();
private Integer id;

public Integer getId() {
    return id;
}



public void setId(Integer id) {
    this.id = id;
}



public Conta() {
this.id = proximoId++; 
this.saldo = 0.0; // Inicializa o saldo com 0.0
}



public ArrayList getCarteiraAcoes() {
return carteiraAcoes;
}

public void setCarteiraAcoes(ArrayList carteiraAcoes) {
this.carteiraAcoes = carteiraAcoes;
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

public void adicionarSaldo(Double valor) {
if (valor != null && valor > 0) {
this.saldo += valor;
}
}



public void subtrairSaldo(double valor) {
if (saldo >= valor) {
saldo -= valor;
} else {
// Se o saldo for insuficiente, lida com o erro de forma apropriada
System.out.println("Saldo insuficiente para realizar a operação.");
}
}
}