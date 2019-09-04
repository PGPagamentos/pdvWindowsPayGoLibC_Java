package br.com.paygo.interop;

import br.com.paygo.enums.PWData;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWValidDataEntry;
import br.com.paygo.exception.InvalidDataType;
import br.com.paygo.exception.InvalidInfoType;
import br.com.paygo.helper.TextFormatter;
import com.sun.jna.Structure;
@Structure.FieldOrder(value = {"identificador", "tipoDeDado", "prompt", "numOpcoesMenu", "menu", "mascaraDeCaptura",
        "tipoEntradaPermitido", "tamanhoMinimo", "tamanhoMaximo", "valorMinimo", "valorMaximo", "ocultarDadosDigitados",
        "validacaoDado", "aceitaNulo", "valorInicial", "teclaDeAtalho", "msgValidacao", "msgConfirmacao", "msgDadoMaior",
        "msgDadoMenor", "capturarDataVencimentoCartao", "tipoEntradaCartao", "itemInicial", "numeroCapturas", "msgPrevia",
        "tipoEntradaCodigoBarras", "omitirMsgAlerta", "iniciarPelaEsquerda", "notificarCancelamento", "alinharDireita"})
public class PWGetData extends Structure {

    public short identificador;
    public byte tipoDeDado;
    public byte[] prompt = new byte[84];
    public byte numOpcoesMenu;
    public PWMenu menu;
    public byte[] mascaraDeCaptura = new byte[41];
    public byte tipoEntradaPermitido;
    public byte tamanhoMinimo;
    public byte tamanhoMaximo;
    public int valorMinimo;
    public int valorMaximo;
    public byte ocultarDadosDigitados;
    public byte validacaoDado;
    public byte aceitaNulo;
    public byte[] valorInicial = new byte[41];
    public byte teclaDeAtalho;
    public byte[] msgValidacao = new byte[84];
    public byte[] msgConfirmacao = new byte[84];
    public byte[] msgDadoMaior = new byte[84];
    public byte[] msgDadoMenor = new byte[84];
    public byte capturarDataVencimentoCartao;
    public int tipoEntradaCartao;
    public byte itemInicial;
    public byte numeroCapturas;
    public byte[] msgPrevia = new byte[84];
    public byte tipoEntradaCodigoBarras;
    public byte omitirMsgAlerta;
    public byte iniciarPelaEsquerda;
    public byte notificarCancelamento;
    public byte alinharDireita;

    PWInfo getIdentificador() throws InvalidInfoType {
        return PWInfo.valueOf(this.identificador);
    }

    byte getTipoDeDadoByte() { return this.tipoDeDado; }

    PWData getTipoDeDado() throws InvalidDataType { return PWData.valueOf(this.tipoDeDado); }

    String getPrompt() { return TextFormatter.formatByteMessage(this.prompt); }

    int getNumOpcoesMenu() { return this.numOpcoesMenu; }

    PWMenu getMenu() { return this.menu; }

    String getMascaraDeCaptura() { return TextFormatter.formatByteMessage(this.mascaraDeCaptura); }

    PWValidDataEntry getTipoEntradaPermitido() { return PWValidDataEntry.valueOf(this.tipoEntradaPermitido); }

    byte getTamanhoMinimo() { return this.tamanhoMinimo; }

    byte getTamanhoMaximo() { return this.tamanhoMaximo; }

    int getValorMinimo() { return this.valorMinimo; }

    int getValorMaximo() { return this.valorMaximo; }

    byte getOcultarDadosDigitados() { return this.ocultarDadosDigitados; }

    byte getValidacaoDado() { return this.validacaoDado; }

    byte getAceitaNulo() { return this.aceitaNulo; }

    String getValorInicial() { return TextFormatter.formatByteMessage(this.valorInicial); }

    byte getTeclaDeAtalho() { return this.teclaDeAtalho; }

    String getMsgValidacao() { return TextFormatter.formatByteMessage(this.msgValidacao); }

    String getMsgConfirmacao() { return TextFormatter.formatByteMessage(this.msgConfirmacao); }

    String getMsgDadoMaior() { return TextFormatter.formatByteMessage(this.msgDadoMaior); }

    String getSzMsgDadoMenor() { return TextFormatter.formatByteMessage(this.msgDadoMenor); }

    byte getCapturarDataVencimentoCartao() { return this.capturarDataVencimentoCartao; }

    public int getTipoEntradaCartao() { return this.tipoEntradaCartao; }

    byte getItemInicial() { return this.itemInicial; }

    byte getNumeroCapturas() { return this.numeroCapturas; }

    String getMsgPrevia() { return TextFormatter.formatByteMessage(this.msgPrevia); }

    byte getTipoEntradaCodigoBarras() { return this.tipoEntradaCodigoBarras; }

    byte getOmitirMsgAlerta() { return this.omitirMsgAlerta; }

    byte getIniciarPelaEsquerda() { return this.iniciarPelaEsquerda; }

    byte getNotificarCancelamento() { return this.notificarCancelamento; }

    byte getAlinharDireita() { return this.alinharDireita; }

    @Override
    public String toString() {
        return "PWGetData{" +
                "identificador=" + identificador +
                ", tipoDeDado=" + tipoDeDado +
                '}';
    }
}
