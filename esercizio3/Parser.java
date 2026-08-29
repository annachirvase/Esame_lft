package esercizio3;
import java.io.*;

public class Parser {
  private Lexer lex;
  private BufferedReader pbr;
  private Token look;

  public Parser(Lexer l, BufferedReader br) {
    lex = l;
    pbr = br;
    move();
  }
    
  void move() {
    look = lex.lexical_scan(pbr);
    System.out.println("token = " + look);
  }

  void error(String s) {
    throw new Error("near line " + Lexer.line + ": " + s);
  }
  
  void match(int t) {
    if (look.tag == t) {
      if (look.tag != Tag.EOF) move();
    } else error("syntax error");
  }
    
  public void start() {      //prog()
    switch(look.tag) {
      case Tag.PRINT:
      case Tag.ID:
      case Tag.CONDITIONAL:
      case Tag.WHILE:
      case '{':
        statlist();
        match(Tag.EOF);
        break;
      default:
        error("Error in grammar (start)");	
    }
  }
    

  private void statlist() {
    switch(look.tag) {
      case Tag.PRINT:
      case Tag.ID:
      case Tag.CONDITIONAL:
      case Tag.WHILE:
      case '{':
        stat();
        statlistp();
        break;
      default:
        error("Error in grammar (statlist)");
    }
  }

  private void statlistp() {
    switch(look.tag) {
      case ';':
        match(';');
        stat();
        statlistp();
        break;
      case Tag.EOF:
      case '}':
        break;
    default:
      error("Error in grammar (statlistp)"); 
    }
  }

  private void stat() {
    switch(look.tag) {
      case Tag.PRINT:
        match(Tag.PRINT);
        match('[');
        exprlist();
        match(']');
        break;
      case Tag.ID:
        match(Tag.ID);
        match(Tag.ASSIGN);
        statp();
        break;
      case Tag.WHILE:
        match(Tag.WHILE);
        match('(');
        bexpr();
        match(')');
        match(Tag.DO);
        stat();
        break;
      case Tag.CONDITIONAL:
        match(Tag.CONDITIONAL);
        match('[');
        caselist();
        match(']');
        match(Tag.DEFAULT);
        stat();
        break;
      case '{':
        match('{');
        statlist();
        match('}');
        break;
      default:
        error("Error in grammar (stat)");
    }
  }

  private void statp() {
    switch (look.tag) {
      case '+':
      case '*':
      case '-':
      case '/':
      case Tag.NUM:
      case Tag.ID:
        expr();               
        break;
     case Tag.USER:
        match(Tag.USER);                
        break;
      default:
        error("Error in grammar (statp)");
    }
  }

  private void caselist() {
    switch (look.tag) {
      case (Tag.CASE):
        caseitem();
        caselistp();
        break;
      default:
        error("Error in grammar (caselist)");
    }
  }

  private void caselistp() {
    switch (look.tag) {
      case Tag.CASE:
        caseitem();
        caselistp();
        break;
      case ']':
        break;
      default:
        error("Error in grammar (caselistp)");
    }
  }

  private void caseitem() {
    switch (look.tag) {
      case Tag.CASE:
        match(Tag.CASE);
        match('(');
        bexpr();
        match(')');
        match(Tag.DO);
        stat();
        caseitemb();
        break;
      default:
        error("Error in grammar (caseitem)");
    }
  }

  private void caseitemb() {
    switch (look.tag) {
      case Tag.BREAK:
        match(Tag.BREAK);
        break;
      case Tag.CASE:
      case ']':
        break;
      default:
        error("Error in grammar (caseitemb)");
    }
  }
    
  private void bexpr() {
    switch(look.tag) {
      case Tag.LE:
      case Tag.GE:
      case Tag.EQ:
      case '>':
      case '<':
      case Tag.NE:
        relop();
        expr();
        expr();
      break;
    default:
        error("Error in grammar (bexpr)");
    }
  }
  
  private void relop() {
    switch(look.tag) {
      case Tag.LE:
        match(Tag.LE);                
        break;
      case Tag.GE:
        match(Tag.GE);                
        break;
      case Tag.EQ:
        match(Tag.EQ);                
        break;
      case '<':
        match('<');
        break;
      case '>':
        match('>');
        break;
      case Tag.NE:
        match(Tag.NE);                
        break;
      default:
        error("Error in grammar (relop)");	
    }
  }

  private void expr() {
    switch(look.tag) {
      case '+':
        match('+');
        exprp();
        break;
      case '*':
        match('*');
        exprp();
        break;
      case '-':
        match('-');
        expr();
        expr();
        break;
      case '/':
        match('/');
        expr();
        expr();
        break;
      case Tag.NUM:
        match(Tag.NUM);                
        break;
      case Tag.ID:
        match(Tag.ID);                
        break;
      default:
        error("Error in grammar (expr)");	
    }
  }
    
  private void exprp() {
    switch(look.tag) {
      case '+':
      case '*':
      case '-':
      case '/':
      case Tag.NUM:
      case Tag.ID:
        expr();
        expr();                
        break;
      case '[':
        match('[');
        exprlist();
        match(']');
        break;
      default:
        error("Error in grammar (exprp)");
    }
  }

  private void exprlist() {
    switch(look.tag) {
      case '+':
      case '*':
      case '-':
      case '/':
      case Tag.NUM:
      case Tag.ID:
        expr();
        exprlistp();                
        break;
      default:
        error("Error in grammar (exprlist)");
    }
  }
    
  private void exprlistp() {
    switch(look.tag) {
      case ',':
        match(',');
        expr();
        exprlistp();
        break;
      case ']':
        break;
      default:
        error("Error in grammar (exprlistp)");
    }
  }
    
  public static void main(String[] args) {
    Lexer lex = new Lexer();
    String path = "input.lft";
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
      Parser parser = new Parser(lex, br); 
      parser.start();
      System.out.println("Input OK");
      br.close();
    } catch (IOException e) {e.printStackTrace();}    
  }
}

