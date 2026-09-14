#!/bin/sh

PASSED=0
FAILED=0
CRASHED=0

echo "╔════════════════════════════════════════════════════════════╗"
echo "║    Test Suite - Simple_translator P PREFISSO (27 test)    ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

PASS_TESTS="test01_simple test02_addition test03_subtraction test04_multiply test05_division test06_nested_expr test07_assignment test08_assignment_expr test10_while_lt test11_while_gt test12_while_le test13_while_ge test14_while_eq test15_and_logic test16_or_logic test17_nested_logic test18_conditional_simple test19_conditional_multi test20_conditional_complex test21_blocks test22_while_block test23_many_vars test24_sum_list test25_mult_list test26_while_false"

CRASH_TESTS="test27_error_undefined"

# Compila il traduttore se non già compilato
if [ ! -f "../Simple_translator.class" ]; then
    echo "⚙️  Compilando Simple_translator.java..."
    cd ..
    javac Simple_translator.java
    cd test_files_prefix
    echo ""
fi

# Testa i file
for test_file in test*.lft; do
    if [ ! -f "$test_file" ]; then
        continue
    fi
    
    test_name="${test_file%.*}"
    
    printf "[%-30s] " "$test_name"
    
    # Copia il file in input.lft (un livello su)
    cp "$test_file" ../input.lft
    
    # Esegui il traduttore
    cd ..
    output=$(java Simple_translator 2>&1)
    exit_code=$?
    cd test_files_prefix
    
    # Determina il risultato
    if [ $exit_code -eq 0 ]; then
        if echo "$PASS_TESTS" | grep -q "$test_name"; then
            echo "✅ PASS"
            ((PASSED++))
        else
            echo "⚠️  PASS (unexpected)"
            ((PASSED++))
        fi
    else
        if echo "$CRASH_TESTS" | grep -q "$test_name"; then
            echo "✅ CRASH (expected)"
            ((PASSED++))
        else
            echo "❌ FAIL (unexpected crash)"
            ((FAILED++))
        fi
        ((CRASHED++))
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
