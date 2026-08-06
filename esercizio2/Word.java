package esercizio2;
public class Word extends Token {
    public String lexeme = "";
    public Word(int tag, String s) { super(tag); lexeme=s; }
    public String toString() { return "<" + tag + ", " + lexeme + ">"; }
    public static final Word
	t_print = new Word(Tag.PRINT, "print"),
	t_while = new Word(Tag.WHILE, "while"),
	t_do = new Word(Tag.DO, "do"),
	t_conditional = new Word(Tag.CONDITIONAL, "conditional"),
	t_case = new Word(Tag.CASE, "case"),
	t_break = new Word(Tag.BREAK, "break"),
	t_default = new Word(Tag.DEFAULT, "default"),
	t_user = new Word(Tag.USER, "user");
}
