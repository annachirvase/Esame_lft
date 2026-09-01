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
      case Tag.ID:
      case Tag.WHILE:
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
      case Tag.ID:
      case Tag.WHILE:
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
        match('[');
          int ldefault = code.newLabel();
          caselist(lnext, ldefault);
        match(']');
        match(Tag.DEFAULT);
          code.emitLabel(ldefault);
          stat(lnext);
        break;

      case Tag.ID:
        String id_name= ((Word)look).lexeme;
        match(Tag.ID);
          int id_addr = st.lookupAddress(id_name);
            if (id_addr==-1) {
              id_addr = count;
              st.insert(id_name, count++);
            }
        match(Tag.ASSIGN);
            if (look.tag == Tag.USER) {
              match(Tag.USER);
              code.emit(OpCode.invokestatic, 0);
            }else{
              expr();
            }
          code.emit(OpCode.istore, id_addr);
          code.emit(OpCode.GOto,lnext);
          break;

      case Tag.WHILE:
        match(Tag.WHILE);
        match('(');
          int lstart = code.newLabel();
            code.emitLabel(lstart);
          int ltrue = code.newLabel();
          bexpr(ltrue, lnext);
          match(')');
          match(Tag.DO);
            code.emitLabel(ltrue);
          stat(lstart);
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

 private void caselist(int lnext, int ldefault) {
    if (look.tag == Tag.CASE) {
        int lnext_case = code.newLabel();
        caseitem(lnext, lnext_case);
        code.emitLabel(lnext_case);
        caselistp(lnext, ldefault);
    } else {
        error("Error in caselist");
    }
  }

  private void caselistp(int lnext, int ldefault) {
    switch (look.tag) {
        case Tag.CASE:
            int lnext_case = code.newLabel();
            caseitem(lnext, lnext_case);
            code.emitLabel(lnext_case);
            caselistp(lnext, ldefault);
            break;

        default:
            break;
    }
}

  private void caseitem(int lnext, int lnext_case) {
    switch(look.tag) {
      case Tag.CASE:
        match(Tag.CASE);
        match('(');
          int ltrue = code.newLabel();
          bexpr(ltrue, lnext_case);
          match(')');
          match(Tag.DO);
          code.emitLabel(ltrue);
          stat(lnext);
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
      case ';':
      case '}':
      case Tag.DO:
      case Tag.EOF:
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
      case ';':
      case '}':
      case Tag.DO:
      case Tag.EOF:
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