.
├── 01_DFA/                    # Modulo 1: Automi a Stati Finiti Deterministici
│   ├── Trezeri.java           # DFA di esempio (3 zeri consecutivi)
│   ├── Identificatori.java    # Esercizio 1.1 (DFA per identificatori con underscore)
│   └── Commenti.java          # Esercizio 1.2 (DFA per riconoscimento commenti)
│
├── 02_Lexer/                  # Modulo 2: Analisi Lessicale
│   ├── Tag.java               # Costanti dei token
│   ├── Token.java             # Classe base Token
│   ├── Word.java              # Gestione identificatori e parole chiave
│   ├── NumberTok.java         # Gestione costanti numeriche
│   └── Lexer.java             # Analizzatore lessicale completo (Es. 2.1, 2.2, 2.3)
│
├── 03_Parser/                 # Modulo 3: Analisi Sintattica
│   ├── ParserExpr.java        # Parser a discesa ricorsiva per espressioni aritmetiche
│   └── ParserP.java           # Esercizio 3.1: Parser LL(1) per il linguaggio P
│
└── 04_Translator/             # Modulo 4: Generazione Bytecode JVM
    ├── OpCode.java            # Enumerazione istruzioni JVM
    ├── Instruction.java       # Rappresentazione singola istruzione
    ├── CodeGenerator.java     # Gestione emissione codice e etichette Jasmin
    ├── SymbolTable.java       # Tabella dei simboli (indirizzi variabili)
    ├── SimpleTranslator.java  # Traduttore per il linguaggio Psimple
    └── Translator.java        # Esercizio 4.1 & 4.2: Traduttore finale per il linguaggio P
