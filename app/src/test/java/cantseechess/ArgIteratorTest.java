package cantseechess;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ArgIteratorTest {
    @Test
    public void testEmpty() {
        var i = new ArgIterator("");
        assertFalse(i.hasNext());
    }

    @Test
    public void tag() {
        var i = new ArgIterator("hi");
        assertEquals("hi", i.next().name);
        assertFalse(i.hasNext());
    }

    @Test
    public void tag2() {
        var i = new ArgIterator("foo bar");
        assertEquals("foo", i.next().name);
        assertEquals("bar", i.next().name);
        assertFalse(i.hasNext());
    }

    @Test
    public void tag3() {
        var i = new ArgIterator("foo bar baz");
        assertEquals("foo", i.next().name);
        assertEquals("bar", i.next().name);
        assertEquals("baz", i.next().name);
        assertFalse(i.hasNext());
    }

    @Test
    public void kv() {
        var i = new ArgIterator("foo=bar");
        var arg = i.next();
        assertEquals("foo", arg.name);
        assertEquals("bar", arg.value);
    }

    @Test
    public void kvtag() {
        var i = new ArgIterator("foo=bar baz");
        var arg = i.next();
        assertEquals("foo", arg.name);
        assertEquals("bar", arg.value);
        assertEquals("baz", i.next().name);
    }

    @Test
    public void tagkv() {
        var i = new ArgIterator("baz foo=bar");
        assertEquals("baz", i.next().name);
        var arg = i.next();
        assertEquals("foo", arg.name);
        assertEquals("bar", arg.value);
    }

    @Test
    public void tagkvtag() {
        var i = new ArgIterator("baz foo=bar how");
        assertEquals("baz", i.next().name);
        var arg = i.next();
        assertEquals("foo", arg.name);
        assertEquals("bar", arg.value);
        assertEquals("how", i.next().name);
    }

    @Test
    public void tagq() {
        var i = new ArgIterator("foo=\"hi bar baz\"");
        var arg = i.next();
        assertEquals("foo", arg.name);
        assertEquals("hi bar baz", arg.value);
    }
}
