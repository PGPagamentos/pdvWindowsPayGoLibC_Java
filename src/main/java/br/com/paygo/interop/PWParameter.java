package br.com.paygo.interop;

public class PWParameter {

    private short code;
    private String name;
    private String value;

    public PWParameter() {}

    public PWParameter(short code, String name, String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "PWParameter{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
