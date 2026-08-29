package esercizio2;
import java.io.*; 


public class Lexer {
    // current line number of input file
    public static int line = 1;

    // current symbol read from input file
    private char peek = ' ';

    /* determines whether, at the start of the execution of lexical_scan,
    at least one new symbol should be read from the input */
    public static boolean advance = true;

    private void readch(BufferedReader br) {
        try {
            peek = ( char ) br.read();
        } catch (IOException exc) {
            peek = ( char ) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r' 
               || advance) {
            readch(br);
            if (peek == '\n') line++;
	    if (advance) advance = false;
        }

        switch (peek) {
            case '+':
                advance = true;
                return Token.plus;
            case '-':
                advance = true;
                return Token.minus;
            case '*':
                advance = true;
                return Token.mult;
//es2.3
            case '/':
                readch(br);
                if(peek=='/'){
                    while (peek != '\n' && peek !=(char)-1) {
                        readch(br);  
                    }
                    if(peek == '\n') line++;
                    advance = true;
                    return lexical_scan(br);
                } else if(peek=='*'){
                    int state = 0;
                    while(state !=2){
                        readch(br);
                        if(peek==(char)-1){
                            System.err.println("errore lessicale:commento non chiuso");
                            return null;
                            }
                        if(peek == '\n') line++;

                        switch(state){
                            case 0:
                                if(peek=='*'){
                                    state = 1;
                                }
                                break;
                            case 1:
                                if(peek=='/'){
                                    state = 2;
                                } else if(peek != '*'){
                                    state = 0;
                                }
                                break;
                        }
                    }
                    advance = true;
                    return lexical_scan(br); //ricorsione per passare al. token successivo
                } else{
                    return Token.div;
                }
//fine es2.3
            case ';':
                advance = true;
                return Token.semicolon;
            case ',':
                advance = true;
                return Token.comma;
            case '(':
                advance = true;
                return Token.lpt;
            case ')':
                advance = true;
                return Token.rpt;
            case '{':
                advance = true;
                return Token.lpg;
            case '}':
                advance = true;
                return Token.rpg;
            case '[':
                advance = true;
                return Token.lpq;
            case ']':
                advance = true;
                return Token.rpq;
            case '<':
                readch(br);
                if (peek == '=') {
                advance = true;
                    return Token.le;
                } else {
                    return Token.lt;
                }
            case '>':
                readch(br);
                if (peek == '=') {
                    advance = true;
                    return Token.ge;
                } else {
                    return Token.gt;
                }
            case '=':
                readch(br);
                if (peek == '=') {
                    advance = true;
                    return Token.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after = : "  + peek );
                    return null;
                }          
            case '!':
                readch(br);
                if (peek == '=') {
                    advance = true;
                    return Token.ne;
                } else {
                    System.err.println("Erroneous character"
                            + " after ! : "  + peek );
                    return null;
                }          
            case '&':
                readch(br);
                if (peek == '&') {
                    advance = true;
                    return Token.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }          
            case '|':
                readch(br);
                if (peek == '|') {
                    advance = true;
                    return Token.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            case ':':
                readch(br);
                if (peek == '=') {
                    advance = true;
                    return Token.assign;
                } else {
                    System.err.println("Erroneous character"
                            + " after : : "  + peek );
                    return null;
                }          		
            case (char)-1:
                return new Token(Tag.EOF);
            default:

//es2.2
                if (Character.isLetter(peek) || peek == '_') {
                    String s = "";
                    boolean containsLetterOrDigit = false;

                    do {
                       if(peek != '_'){
                           containsLetterOrDigit = true;
                       }
                          s += peek;
                          readch(br);
                    } while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_');

                    //verifico che l'identificatore contenga almeno una lettera o un numero
                    if (!containsLetterOrDigit) {
                        System.err.println("Erroneous identifier: " + s);
                        return null;
                    }

                    switch (s) {
                        case "print":
                            return Word.t_print;
                        case "while":
                            return Word.t_while;
                        case "do":
                            return Word.t_do;
                        case "conditional":
                            return Word.t_conditional;
                        case "case":
                            return Word.t_case;
                        case "break":
                            return Word.t_break;
                        case "default":
                            return Word.t_default;
                        case "user":
                            return Word.t_user;
                        default:
                            return new Word(Tag.ID,s);
                    }
                    
                } else if (Character.isDigit(peek)) {
                    int v=0;
                    do {
                        v = 10*v + Character.digit(peek,10);
                        readch(br);
                    } while (Character.isDigit(peek));
                    return new NumberTok(Tag.NUM, v);
                } else {
                    System.err.println("Erroneous character: " + peek );
                    return null;
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "input.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
