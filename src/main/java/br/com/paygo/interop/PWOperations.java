package br.com.paygo.interop;

import com.sun.jna.Structure;

import br.com.paygo.helper.TextFormatter;

@Structure.FieldOrder(value = {"bOperType", "szText", "szValue"})
public class PWOperations extends Structure {
	public byte  bOperType;
	public byte[] szText = new byte[21];
	public byte[] szValue = new byte[21];
	
	public byte getOperacao() { return this.bOperType; }
	public String getTexto() { return TextFormatter.formatByteMessage(this.szText); }
	public String getValor() { return TextFormatter.formatByteMessage(this.szValue); }
}
