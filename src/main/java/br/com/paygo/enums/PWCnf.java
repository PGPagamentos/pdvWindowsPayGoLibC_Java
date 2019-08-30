package br.com.paygo.enums;

/**
 * Códigos de Confirmação de Transação
 */
public enum PWCnf {
    AUTO(289),     // A transação foi confirmada pelo Ponto de Captura, sem intervenção do usuário.
    MANU_AUT(12833),   // A transação foi confirmada manualmente na Automação.*/
    PWCNF_REV_MANU_AUT(12849),   // A transação foi desfeita manualmente na Automação.*/
    PWCNF_REV_PRN_AUT(78129),   // A transação foi desfeita pela Automação, devido a uma falha na impressão do comprovante (não fiscal). A priori, não usar. Falhas na impressão não devem gerar desfazimento, deve ser solicitada a reimpressão da transação.*/
    PWCNF_REV_DISP_AUT(143665),  // A transação foi desfeita pela Automação, devido a uma falha no mecanismo de liberação da mercadoria.*/
    PWCNF_REV_COMM_AUT(209201),  // A transação foi desfeita pela Automação, devido a uma falha de comunicação/integração com o ponto de captura (Cliente Muxx).*/
    PWCNF_REV_ABORT(274737),  // A transação não foi finalizada, foi interrompida durante a captura de dados.*/
    PWCNF_REV_OTHER_AUT(471345),  // A transação foi desfeita a pedido da Automação, por um outro motivo não previsto.*/
    PWCNF_REV_PWR_AUT(536881),  // A transação foi desfeita automaticamente pela Automação, devido a uma queda de energia (reinício abrupto do sistema).*/
    PWCNF_REV_FISC_AUT(602417),  // A transação foi desfeita automaticamente pela Automação, devido a uma falha de registro no sistema fiscal (impressora S@T, on-line, etc.).*/
    PWCNF_REV_AUTO_ABORT(262449); // mantido para compatibilidade temporaria

    private final int value;

    PWCnf(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
