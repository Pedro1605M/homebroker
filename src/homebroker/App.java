public class App{
    public static void main(String[] args) throws Exception{

        String acao = "AAPL";

        StockApi acao01 = new StockApi();

        acao01.getPrice(acao);
    }
}


