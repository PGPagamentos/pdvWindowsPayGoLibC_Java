package br.com.paygo.model;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class PWGetData extends Structure {

    public short identificador;
    public byte  tipoDeDado;
    public byte[] prompt = new byte[84];
    public byte  numOpcoesMenu;
    public PWMenu menu;
    public byte[] mascaraDeCaptura = new byte[41];
    public byte  tiposEntradaPermitidos;
    public byte  tamanhoMinimo;
    public byte  tamanhoMaximo;
    public int valorMinimo;
    public int valorMaximo;
    public byte  ocultarDadosDigitados;
    public byte  validacaoDado;
    public byte  aceitaNulo;
    public byte[] valorInicial = new byte[41];
    public byte  teclaDeAtalho;
    public byte[] msgValidacao = new byte[84];
    public byte[] msgConfirmacao = new byte[84];
    public byte[] msgDadoMaior = new byte[84];
    public byte[] msgDadoMenor = new byte[84];
    public byte  capturarDataVencimentoCartao;
    public int tipoEntradaCartao;
    public byte  temInicial;
    public byte  numeroCapturas;
    public byte[] msgPrevia = new byte[84];
    public byte  tipoEntradaCodigoBarras;
    public byte  omitirMsgAlerta;
    public byte  iniciarPelaEsquerda;
    public byte  notificarCancelamento;
    public byte  alinharDireita;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
                "identificador",
                "tipoDeDado",
                "prompt",
                "numOpcoesMenu",
                "menu",
                "mascaraDeCaptura",
                "tiposEntradaPermitidos",
                "tamanhoMinimo",
                "tamanhoMaximo",
                "valorMinimo",
                "valorMaximo",
                "ocultarDadosDigitados",
                "validacaoDado",
                "aceitaNulo",
                "valorInicial",
                "teclaDeAtalho",
                "msgValidacao",
                "msgConfirmacao",
                "msgDadoMaior",
                "msgDadoMenor",
                "capturarDataVencimentoCartao",
                "tipoEntradaCartao",
                "temInicial",
                "numeroCapturas",
                "msgPrevia",
                "tipoEntradaCodigoBarras",
                "omitirMsgAlerta",
                "iniciarPelaEsquerda",
                "notificarCancelamento",
                "alinharDireita"
        );
    }
}
