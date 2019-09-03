package br.com.paygo.interop;

import br.com.paygo.enums.PWData;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.helper.TextFormatter;
import com.sun.jna.Structure;
@Structure.FieldOrder(value = {"identificador", "tipoDeDado", "prompt", "numOpcoesMenu", "menu", "mascaraDeCaptura",
        "tiposEntradaPermitidos", "tamanhoMinimo", "tamanhoMaximo", "valorMinimo", "valorMaximo", "ocultarDadosDigitados",
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
    public byte tiposEntradaPermitidos;
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

    public PWInfo getIdentificador() throws Exception {
        return PWInfo.valueOf(this.identificador);
    }

    public byte getTipoDeDadoByte() { return this.tipoDeDado; }

    public PWData getTipoDeDado() throws Exception { return PWData.valueOf(this.tipoDeDado); }

    public String getPrompt() { return TextFormatter.formatByteMessage(this.prompt); }

    public int getNumOpcoesMenu() { return this.numOpcoesMenu; }

    public PWMenu getMenu() { return this.menu; }

    public String getMascaraDeCaptura() { return TextFormatter.formatByteMessage(this.mascaraDeCaptura); }

    public byte getTiposEntradaPermitidos() { return this.tiposEntradaPermitidos; }

    public byte getTamanhoMinimo() { return this.tamanhoMinimo; }

    public byte getTamanhoMaximo() { return this.tamanhoMaximo; }

    public int getValorMinimo() { return this.valorMinimo; }

    public int getValorMaximo() { return this.valorMaximo; }

    public byte getOcultarDadosDigitados() { return this.ocultarDadosDigitados; }

    public byte getValidacaoDado() { return this.validacaoDado; }

    public byte getAceitaNulo() { return this.aceitaNulo; }

    public String getValorInicial() { return TextFormatter.formatByteMessage(this.valorInicial); }

    public byte getTeclaDeAtalho() { return this.teclaDeAtalho; }

    public String getMsgValidacao() { return TextFormatter.formatByteMessage(this.msgValidacao); }

    public String getMsgConfirmacao() { return TextFormatter.formatByteMessage(this.msgConfirmacao); }

    public String getMsgDadoMaior() { return TextFormatter.formatByteMessage(this.msgDadoMaior); }

    public String getSzMsgDadoMenor() { return TextFormatter.formatByteMessage(this.msgDadoMenor); }

    public byte getCapturarDataVencimentoCartao() { return this.capturarDataVencimentoCartao; }

    public int getTipoEntradaCartao() { return this.tipoEntradaCartao; }

    public byte getItemInicial() { return this.itemInicial; }

    public byte getNumeroCapturas() { return this.numeroCapturas; }

    public String getMsgPrevia() { return TextFormatter.formatByteMessage(this.msgPrevia); }

    public byte getTipoEntradaCodigoBarras() { return this.tipoEntradaCodigoBarras; }

    public byte getOmitirMsgAlerta() { return this.omitirMsgAlerta; }

    public byte getIniciarPelaEsquerda() { return this.iniciarPelaEsquerda; }

    public byte getNotificarCancelamento() { return this.notificarCancelamento; }

    public byte getAlinharDireita() { return this.alinharDireita; }

    @Override
    public String toString() {
        return "PWGetData{" +
                "identificador=" + identificador +
                ", tipoDeDado=" + tipoDeDado +
                '}';
    }
}
