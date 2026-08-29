package esercizio3;
public class Token {
    public final int tag;
    public Token(int t) { tag = t;  }
    public String toString() {return "<" + tag + ">";}
    public static final Token
	assign = new Token(Tag.ASSIGN),
	and = new Token(Tag.AND),
	or = new Token(Tag.OR),
	le = new Token(Tag.LE),
	ge = new Token(Tag.GE),
	eq = new Token(Tag.EQ),
	ne = new Token(Tag.NE),
	lt = new Token('<'),
	gt = new Token('>'),
	lpt = new Token('('),
	rpt = new Token(')'),
	lpg = new Token('{'),
	rpg = new Token('}'),
	lpq = new Token('['),
	rpq = new Token(']'),
	plus = new Token('+'),
	minus = new Token('-'),
	mult = new Token('*'),
	div = new Token('/'),
	comma = new Token(','),
	semicolon = new Token(';');
}
