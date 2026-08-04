public class Sessao {
    private static Integer userId;
    private static Integer accountId;
    private static String nome;
    private static String email;
    private static double saldo;

    public static void iniciarSessao(int uId, int aId, String name, String mail, double balance) {
        userId = uId;
        accountId = aId;
        nome = name;
        email = mail;
        saldo = balance;
    }

    public static boolean isLogado() {
        return userId != null && accountId != null;
    }

    public static void encerrarSessao() {
        userId = null;
        accountId = null;
        nome = null;
        email = null;
        saldo = 0.0;
    }

    public static Integer getUserId() {
        return userId;
    }

    public static Integer getAccountId() {
        return accountId;
    }

    public static String getNome() {
        return nome;
    }

    public static String getEmail() {
        return email;
    }

    public static double getSaldo() {
        return saldo;
    }

    public static void setSaldo(double novoSaldo) {
        saldo = novoSaldo;
    }

    public static void setAccountId(int novoAccountId) {
        accountId = novoAccountId;
    }
}
