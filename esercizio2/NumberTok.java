public class NumberTok extends Token {
    public int value;
    public NumberTok(int tag, int v) { super(tag); value=v; }
    public String toString() { return "<" + tag + ", " + value + ">"; }
}

