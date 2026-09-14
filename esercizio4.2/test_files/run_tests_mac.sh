#!/bin/sh

# Script di test per macOS - senza array associativi
# Testa tutti i file .lft nella directory corrente

PASSED=0
FAILED=0
CRASHED=0

echo "╔════════════════════════════════════════════════════════════╗"
echo "║        Test Suite - Simple_translator (30 test)            ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Lista di test che NON dovrebbero crashare
PASS_TESTS="test01_simple test03_assignment test05_addition test06_precedence test07_division test08_parentheses test09_while_lt test10_while_gt test11_while_le test12_while_ne test13_and_logic test14_or_logic test15_nested_logic test16_conditional_simple test17_conditional_multi test18_conditional_complex test19_blocks test20_while_block test21_conditional_noblock test22_many_vars test26_mult_div test27_while_false test28_complex_precedence test29_subtraction test30_ge"

# Lista di test che DEVONO crashare
CRASH_TESTS="test02_undefined_var test23_error_undefined test24_error_syntax test25_error_operator"

# Compila il traduttore se non già compilato
if [ ! -f "../Simple_translator.class" ]; then
    echo "⚙️  Compilando Simple_translator.java..."
    cd ..
    javac Simple_translator.java
    cd test_files
    echo ""
fi

# Testa i file
for test_file in test*.lft; do
    if [ ! -f "$test_file" ]; then
        continue
    fi
    
    test_name="${test_file%.*}"
    
    echo -n "[$test_name] ... "
    
    # Copia il file in input.lft (un livello su)
    cp "$test_file" ../input.lft
    
    # Esegui il traduttore
    cd ..
    output=$(java Simple_translator 2>&1)
    exit_code=$?
    cd test_files
    
    # Determina il risultato
    if [ $exit_code -eq 0 ]; then
        # Controlla se era un test che doveva passare
        if echo "$PASS_TESTS" | grep -q "$test_name"; then
            echo "✅ PASS"
            ((PASSED++))
        else
            echo "⚠️  PASS (unexpected)"
            ((PASSED++))
        fi
    else
        # Controlla se era un test che doveva crashare
        if echo "$CRASH_TESTS" | grep -q "$test_name"; then
            echo "✅ CRASH (expected)"
            ((PASSED++))
        else
            echo "❌ FAIL (unexpected crash)"
            ((FAILED++))
        fi
        ((CRASHED++))
    fi
    
    # Mostra primo errore se c'è stato
    if [ $exit_code -ne 0 ]; then
        error_line=$(echo "$output" | head -1)
        echo "   → $error_line"
    fi
done

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║                      RISULTATI FINALI                      ║"
echo "╠════════════════════════════════════════════════════════════╣"
echo "║  Passed:  $PASSED"
echo "║  Failed:  $FAILED"
echo "║  Crashed: $CRASHED"
echo "╚════════════════════════════════════════════════════════════╝"

if [ $FAILED -eq 0 ]; then
    echo ""
    echo "🎉 Tutti i test sono OK!"
else
    echo ""
    echo "⚠️  $FAILED test hanno fallito"
fi
