# Test Suite per Simple_translator

Tutti i 30 file di test sono qui. Ecco come usarli:

## Opzione 1: Testare uno per uno (manuale)

```bash
cp test01_simple.lft input.lft
java Simple_translator
# Vedi il risultato
```

Ripeti per ogni test.

## Opzione 2: Script di test automatico (bash)

Crea uno script `run_tests.sh`:

```bash
#!/bin/bash

for test_file in test*.lft; do
  echo "=========================================="
  echo "Testing: $test_file"
  echo "=========================================="
  cp "$test_file" input.lft
  java Simple_translator 2>&1 | head -50
  echo ""
done
```

Poi:
```bash
chmod +x run_tests.sh
./run_tests.sh > results.txt 2>&1
```

## Opzione 3: Script Python (più ordinato)

```python
import os
import subprocess

test_files = sorted([f for f in os.listdir('.') if f.startswith('test') and f.endswith('.lft')])

for test_file in test_files:
    print(f"\n{'='*50}")
    print(f"Testing: {test_file}")
    print('='*50)
    os.system(f'cp {test_file} input.lft')
    result = subprocess.run(['java', 'Simple_translator'], capture_output=True, text=True)
    print(result.stdout)
    if result.stderr:
        print("STDERR:", result.stderr)
```

---

## Risultati attesi

### ✅ NON dovrebbero crashare (test OK):
- test01_simple.lft → Stampa 0
- test03_assignment.lft → Stampa 5
- test05_addition.lft → Stampa 5 (2+3)
- test06_precedence.lft → Stampa 14 (2+3*4)
- test07_division.lft → Stampa 5 (20/4)
- test08_parentheses.lft → Stampa 20 ((2+3)*4)
- test09_while_lt.lft → Stampa 5 (loop incrementa da 1 a 5)
- test10_while_gt.lft → Stampa 5 (loop decrementa da 10 a 5)
- test11_while_le.lft → Stampa 16 (loop incrementa da 10 a 15, +1)
- test12_while_ne.lft → Stampa 10 (loop incrementa da 5 a 10)
- test13_and_logic.lft → Stampa 5 (condizione && è falsa)
- test14_or_logic.lft → Stampa 11 (condizione || è vera per 6 iterazioni)
- test15_nested_logic.lft → Stampa 10 (logica booleana complessa)
- test16_conditional_simple.lft → Input 1 → Stampa 100; Altro → Stampa input
- test17_conditional_multi.lft → Input 1/2/3 → 100/200/300; Altro → 999
- test18_conditional_complex.lft → Dipende da input
- test19_blocks.lft → Stampa 3 (1+2), poi 1
- test20_while_block.lft → Stampa 0, 1, 2, poi 100
- test21_conditional_noblock.lft → Input < 0 → 1; Altro → 0
- test22_many_vars.lft → Stampa 10 (1+2+3+4)
- test26_mult_div.lft → Stampa 40 (100/5*2)
- test27_while_false.lft → Stampa 10 (loop non eseguito)
- test28_complex_precedence.lft → Stampa 26 (2*3+4*5)
- test29_subtraction.lft → Stampa 7 (10-3)
- test30_ge.lft → Stampa 14 (loop decrementa da 20 a 15)

### 🔴 DEVONO crashare (errore previsto):
- test02_undefined_var.lft → Error: Unknown variable
- test04_user_input.lft → Dipende da input utente (non è un errore)
- test23_error_undefined.lft → Error: Unknown variable
- test24_error_syntax.lft → Error: syntax error (parentesi non chiusa)
- test25_error_operator.lft → Error: operatore % non supportato

---

## Come interpretare i risultati

**Se il test "OK" crasha**: C'è un bug nel traduttore
**Se il test "ERROR" NON crasha**: Il traduttore non gestisce bene gli errori
**Se stampano numeri diversi**: Bug nella logica di traduzione

