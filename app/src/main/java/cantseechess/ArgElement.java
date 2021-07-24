package cantseechess;

public class ArgElement {
    public final String name;
    public final String value;

    public ArgElement(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ArgElement(String name) {
        this.name = name;
        this.value = null;
    }

    public boolean isTag() {
        return value == null;
    }
}
