## 📁 Struttura del Progetto

| Modulo | File | Descrizione |
| :--- | :--- | :--- |
| **01_DFA**<br>*(Automi Finita)* | `TreZeri.java` | DFA baseline per il riconoscimento di 3 zeri consecutivi |
| | `Identificatori.java` | **Esercizio 1.1:** DFA per identificatori con underscore |
| | `Commenti.java` | **Esercizio 1.2:** DFA per il riconoscimento dei commenti C-style |
| **02_Lexer**<br>*(Analisi Lessicale)* | `Tag.java` | Tabella delle costanti numeriche dei token |
| | `Token.java` | Classe base per l'astrazione delle unità lessicali |
| | `Word.java` | Gestione identificatori e parole chiave con attributi |
| | `NumberTok.java` | Gestione delle costanti numeriche intere |
| | `Lexer.java` | **Esercizi 2.1–2.3:** Analizzatore lessicale completo |
| **03_Parser**<br>*(Analisi Sintattica)* | `ParserExpr.java` | Parser a discesa ricorsiva per espressioni aritmetiche |
| | `ParserP.java` | **Esercizio 3.1:** Parser LL(1) per il linguaggio P |
| **04_Translator**<br>*(Generazione Codice)* | `OpCode.java` | Enumerazione delle istruzioni mnemoniche JVM |
| | `Instruction.java` | Rappresentazione e formattazione istruzioni Jasmin |
| | `CodeGenerator.java` | Gestione emissione codice ed etichette di salto |
| | `SymbolTable.java` | Tabella dei simboli e mappatura registri variabili |
| | `SimpleTranslator.java` | Traduttore baseline per il sottoinsieme Psimple |
| | `Translator.java` | **Esercizio 4.1 & 4.2:** Traduttore finale per il linguaggio P |
