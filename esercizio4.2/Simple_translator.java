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
        int lnext_prog = code.newLabel(); // Crea l'etichetta per la fine del programma
        statlist(lnext_prog);             // Analizza e traduce la lista delle istruzioni
        code.emitLabel(lnext_prog);       // Emette l'etichetta di fine
        match(Tag.EOF);                   // Assicura di essere arrivati alla fine del file
        try {
          code.toJasmin();                // Genera il file .j per il compilatore Jasmin
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
        int lnext_statlist = code.newLabel();               // Crea etichetta di fine per la prima istruzione
        stat(lnext_statlist);                               // Traduce l'istruzione singola
        code.emitLabel(lnext_statlist);                     // Emette l'etichetta
        statlistp(lnext);                                   // Analizza le istruzioni successive
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
      case Tag.EOF:                                           // Caso epsilon: termina la sequenza e salta alla fine del blocco contenitore
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
        exprlist(OpCode.iadd);                               // Valuta l'espressione o lista da stampare (di default iadd)
        code.emit(OpCode.invokestatic,1); // Invoca Output.printInteger
        match(']');
        code.emit(OpCode.GOto,lnext);             // Salta all'istruzione successiv
        break;
 
      case Tag.CONDITIONAL:
        match(Tag.CONDITIONAL);
        match('[');
          int ldefault = code.newLabel();     // Etichetta per il ramo default
          caselist(lnext, ldefault);          // Gestisce i vari casi della lista case
        match(']');
        match(Tag.DEFAULT);
          code.emitLabel(ldefault);           // Punto di ingresso del ramo default
          stat(lnext);                        // Esegue la stat di default
        break;  
 
      case Tag.ID:
        String id_name= ((Word)look).lexeme;
        match(Tag.ID);
          int id_addr = st.lookupAddress(id_name);    // Cerca la variabile nella tabella simboli
            if (id_addr==-1) {                        // Se nuova, la alloca
              id_addr = count;
              st.insert(id_name, count++);
            }
        match(Tag.ASSIGN);
            if (look.tag == Tag.USER) {               // Caso ID := read / user
              match(Tag.USER);
              code.emit(OpCode.invokestatic, 0);  // Invoca Output.readInteger
            }else{
              expr();                                     // Calcola l'espressione da assegnare
            }
          code.emit(OpCode.istore, id_addr);            // Memorizza il valore nello slot della variabile
          code.emit(OpCode.GOto,lnext);
          break;
 
      case Tag.WHILE:
        match(Tag.WHILE);
        match('(');
          int lstart = code.newLabel();           // Etichetta di inizio ciclo (re-evaluation condizione)
            code.emitLabel(lstart);
          int ltrue = code.newLabel();            // Etichetta d'ingresso se la BExpr è vera
          bexpr(ltrue, lnext);                    // Valuta la condizione booleana in notazione prefissa
          match(')');
          match(Tag.DO);
            code.emitLabel(ltrue);                        // Inizio del corpo del ciclo
          int lnext_stat = code.newLabel();               // [FIX] Etichetta per l'uscita dalla stat del corpo
          stat(lnext);                                    // Esegue l'istruzione del ciclo
          code.emitLabel(lnext_stat);                     // Emette l'etichetta di fine del corpo
          code.emit(OpCode.GOto,lstart);                  // Salta indietro a lstart per ri-valutare la condizione
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
        int lnext_case = code.newLabel();       // Etichetta per passare al caso successivo se la condizione fallisce
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
 
        default:                        // Caso epsilon: se nessun case ha avuto successo, prosegue verso il default
            break;
    }
}
 
  private void caseitem(int lnext, int lnext_case) {
    switch(look.tag) {
      case Tag.CASE:
        match(Tag.CASE);
        match('(');
          int ltrue = code.newLabel();        // Etichetta se la condizione del case è vera
          bexpr(ltrue, lnext_case);           // Se falsa, salta a lnext_case
          match(')');
          match(Tag.DO);
          code.emitLabel(ltrue);
          stat(lnext);                        // Esegue lo statement e salta alla fine del conditional (lnext)
          break;
    }
  }
            
  private void expr() {
  switch(look.tag) {
    case '+':
      match('+');
      if (look.tag == '[') {      // Somma N-aria: + [ expr, expr, ... ]
        match('[');
        exprlist(OpCode.iadd);               // Passa OpCode.iadd per emettere somme
        match(']');
      } else {                    // Somma Binaria prefissa: + expr1 expr2
        expr();
        expr();
        code.emit(OpCode.iadd);
      }
      break;
    case '-':                     // Sottrazione Binaria prefissa: - expr1 expr2
      match('-');
      expr();
      expr();
      code.emit(OpCode.isub);
      break;
    case '*':
      match('*');
      if (look.tag == '[') {      // Moltiplicazione N-aria: * [ expr, expr, ... ]
        match('[');
        exprlist(OpCode.imul);    // Passa OpCode.imul per emettere moltiplicazioni
        match(']');
      } else {                    // Moltiplicazione Binaria prefissa: * expr1 expr2
        expr();
        expr();
        code.emit(OpCode.imul);
      }
      break;
    case '/':
      match('/');
      expr();
      expr();
      code.emit(OpCode.idiv);
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
    case '(':
      match('(');
      expr();
      match(')');
      break;
    default:
      error("Error in grammar (expr) with " + look);	
  }
}
 
  private void exprlist(OpCode op) {
    expr();         // Valuta il primo elemento dell'espressione
    exprlistp(op);    // Passa l'operatore alla funzione ricorsiva per i successivi
  }
 
  private void exprlistp(OpCode op) {
    switch(look.tag) {
      case ',':
        match(',');
        expr();         // Valuta l'elemento successivo
        code.emit(op);    // Emette l'istruzione passata come parametro (iadd per +, imul per *)
        exprlistp(op);    // Continua la scansione della lista
        break;
      case ']':
      case ')':
      case ';':
      case '}':
      case Tag.EOF:     // Caso epsilon: fine della lista N-aria
        break;
      default:
        error("Error in grammar (exprlistp) with " + look);
    }
  }
 
  private void bexpr(int ltrue, int lfalse) {
    switch(look.tag) {
      case Tag.AND:                     // Congiunzione Prefissa: && B1 B2
        match(Tag.AND);
        int ltrue_b1 = code.newLabel();   // Se B1 è vero salta a ltrue_b1 per valutare B2
        bexpr(ltrue_b1, lfalse);          // Se B1 è falso cortocircuita e salta subito a lfalse
        code.emitLabel(ltrue_b1);
        bexpr(ltrue, lfalse);             // Valuta B2
        break;
 
      case Tag.OR:                        // Disgiunzione Prefissa: || B1 B2
        match(Tag.OR);
        int lfalse_b1 = code.newLabel();  // Se B1 è falso salta a lfalse_b1 per valutare B2
        bexpr(ltrue, lfalse_b1);          // Se B1 è vero cortocircuita e salta subito a ltrue
        code.emitLabel(lfalse_b1);
        bexpr(ltrue, lfalse);             // Valuta B2
        break;
 
      case '<':                     // Relazioni in Notazione Prefissa: relop expr1 expr2
      case '>':
      case Tag.LE:
      case Tag.GE:
      case Tag.EQ:
      case Tag.NE:
        OpCode opcode = relop();      // Legge l'operatore relazionale
        expr();                       // Valuta il primo operando
        expr();                         // Valuta il secondo operando
        code.emit(opcode,ltrue);        // Salta a ltrue se la relazione è verificata
        code.emit(OpCode.GOto,lfalse);   // Salta a lfalse se la relazione non è verificata
        break;
 
      default:
        error("Error in grammar (bexpr) with " + look); 
    }
  }
 
  private OpCode relop() {
    OpCode opcode = OpCode.if_icmplt;
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