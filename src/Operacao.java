public enum Operacao {
    BUY,    // Operação de compra
    SELL;   // Operação de venda

    // Você pode adicionar métodos extras se precisar, por exemplo, para retornar uma descrição da operação
    public String getDescription() {
        switch (this) {
            case BUY:
                return "Compra de ações";
            case SELL:
                return "Venda de ações";
            default:
                return "Operação desconhecida";
        }
    }
}
