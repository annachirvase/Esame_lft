import java.io.*;

public class Simple_translator {

  private Lexer lex;
  private BufferedReader pbr;
  private Token look;
    
  SymbolTable st = new SymbolTable();
  CodeGenerator code = new CodeGenerator();
  int count=0;

  public Simple_translator(Lexer l, BufferedReader br) {
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
    } else 
      error("syntax error with " + look + ", was expecting " + t);
  }

  public void prog() {        
    switch(look.tag) {
      case Tag.PRINT: 	    
      case Tag.CONDITIONAL: 	    
      case '{':
        int lnext_prog = code.newLabel();
        statlist(lnext_prog);
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
          code.toJasmin();
        }
        catch(java.io.IOException e) {
          System.out.println("IO error\n");
        };
        break;
      default:
        error("Error in grammar (prog) with " + look);	
    }
  }

  public void statlist(int lnext) {        
    switch(look.tag) {
      case Tag.PRINT: 	    
      case Tag.CONDITIONAL: 	    
      case '{':
        int lnext_statlist = code.newLabel();
        stat(lnext_statlist);
        code.emitLabel(lnext_statlist);
        statlistp(lnext);
        break;
      default:
        error("Error in grammar (statlist) with " + look);	
    }
  }

  public void statlistp(int lnext) {
    switch(look.tag) {
      case ';':
        match(';');
        int lnext_statlist_p = code.newLabel();
        stat(lnext_statlist_p);
        code.emitLabel(lnext_statlist_p);
        statlistp(lnext);
        break;
      case '}':
      case Tag.EOF:
        code.emit(OpCode.GOto,lnext);
        break;
      default:
        error("Error in grammar (statlist_p) with " + look);
        break; 
    }
  }

  public void stat(int lnext) {
    switch(look.tag) {
      case Tag.PRINT:
        match(Tag.PRINT);
        match('[');
        expr();
        code.emit(OpCode.invokestatic,1);
        match(']');
        code.emit(OpCode.GOto,lnext);
        break;
      case Tag.CONDITIONAL:
        match(Tag.CONDITIONAL);
        match('(');
        int ltrue_cond = code.newLabel();
        bexpr(ltrue_cond,lnext);
        match(')');
        code.emitLabel(ltrue_cond);
        stat(lnext);
        break;
      case '{':
        match('{');
        statlist(lnext);
        match('}');
        break;
      default:
        error("Error in grammar (stat) with " + look);
        break;
    }
  }
            
  private void expr() {
    switch(look.tag) {
      case '(':
      case Tag.NUM:
      case Tag.ID:    
        term();	    
        exprp();
        break;
      default:
        error("Error in grammar (expr) with " + look);	
    }
  }
    
  private void exprp() {
    switch(look.tag) {
      case '+':
        match('+');
        term();
        code.emit(OpCode.iadd);
        exprp();
        break;
      case '-':
        match('-');
        term();
        code.emit(OpCode.isub);
        exprp();
        break;
      case ')':
      case ']':
      case '<':
      case '>':
      case Tag.LE:
      case Tag.GE:
      case Tag.EQ:
      case Tag.NE:
        break;        
      default:
        error("Error in grammar (exprp) with " + look);
    }
  }
    
  private void term() {
    switch(look.tag) {
      case '(':
      case Tag.NUM:	    
      case Tag.ID:    
        fact();
        termp();
        break;
      default:
    error("Error in grammar (term) with " + look);	
    }
  }
    
  private void termp() {
    switch(look.tag) {
      case '*':
        match('*');
        fact();
        code.emit(OpCode.imul);
        termp();
        break;
      case '/':
        match('/');
        fact();
        code.emit(OpCode.idiv);
        termp();
        break;
      case '+':
      case '-':
      case ')':
      case ']':
      case '<':
      case '>':
      case Tag.LE:
      case Tag.GE:
      case Tag.EQ:
      case Tag.NE:
        break;        
      default:
        error("Error in grammar (termp) with " + look);
    }
  }
    
  private void fact() {
    switch (look.tag) {
      case '(':
        match('(');
        expr();
        match(')');
        break;
      case Tag.NUM:
        code.emit(OpCode.ldc,((NumberTok)look).value);
        match(Tag.NUM);                
        break;
      case Tag.ID:
        int id_addr = st.lookupAddress(((Word)look).lexeme);
        if (id_addr != -1) {
          code.emit(OpCode.iload,id_addr);
          match(Tag.ID);
        }
        else
          error("Unknown variable");
        break;
      default:
        error("Error in grammar (fact) with " + look);
    }
  }

  private void bexpr(int ltrue, int lfalse) {
    switch(look.tag) {
      case '(':
      case Tag.NUM:
      case Tag.ID:    
        expr();
        OpCode opcode = relop();
        expr();
        code.emit(opcode,ltrue);
        code.emit(OpCode.GOto,lfalse);
        break;
      default:
        error("Error in grammar (bexpr) with " + look);	
    }
  }

  private OpCode relop() {
    OpCode opcode = OpCode.if_icmplt; // initialised only to avoid error
    switch(look.tag) {
      case '<':
        match('<');
        opcode = OpCode.if_icmplt;
        break;
      case '>':
        match('>');
        opcode = OpCode.if_icmpgt;
        break;
      case Tag.LE:
        match(Tag.LE);
        opcode = OpCode.if_icmple;
        break;
      case Tag.GE:
        match(Tag.GE);
        opcode = OpCode.if_icmpge;
        break;
      case Tag.EQ:
        match(Tag.EQ);
        opcode = OpCode.if_icmpeq;
        break;
      case Tag.NE:
        match(Tag.NE);
        opcode = OpCode.if_icmpne;
        break;
      default:
        error("Error in grammar (relop) with " + look);	
    }
    return opcode;
  }

  public static void main(String[] args) {
    Lexer lex = new Lexer();
    String path = "input.lft";
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
      Simple_translator simple_translator = new Simple_translator(lex, br); 
      simple_translator.prog();
      System.out.println("Input OK");
      br.close();
    } catch (IOException e) {e.printStackTrace();}    
  }
}