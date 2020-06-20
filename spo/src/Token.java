public class Token {

    private Grammar type;
    private String value;

    public Token(Grammar type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(String value) {
        this.type = null;
        this.value = value;
    }

    public Grammar getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}