package cantseechess;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ArgIterator implements Iterator<ArgElement>, Iterable<ArgElement> {
    private final String input;
    private int head = 0;
    private int tail = 0;

    public ArgIterator(String input) {
        this.input = input;
    }

    @Override
    public boolean hasNext() {
        return head < input.length();
    }

    @Override
    public ArgElement next() {
        boolean hasValue = false;
        boolean inQuotes = false;

        int neck = 0;
        while (head < input.length()) {
            var c = input.charAt(head);
            if (!inQuotes && Character.isWhitespace(c)) {
                break;
            } else if (c == '"') {
                if (inQuotes) {
                    ++head;
                    break;
                } else if (neck == head - 1) {
                    inQuotes = true;
                }
            } else if (c == '=') {
                neck = head;
                hasValue = true;
            }
            ++head;
        }
        if (!hasValue) {
            var arg = new ArgElement(input.substring(tail, head));
            ++head;
            tail = head;
            return arg;
        }
        var arg = new ArgElement(input.substring(tail, neck),
                inQuotes ? input.substring(neck + 2, head - 1) : input.substring(neck + 1, head));
        ++head;
        tail = head;
        return arg;
    }

    @NotNull
    @Override
    public Iterator<ArgElement> iterator() {
        return this;
    }
}
