public class App{
    public static void main(String[] args) throws Exception{

        SString acao = "MSFT";

        StockApi acao01 = new StockApi();

        acao01.getPrice(acao);
    }
}


