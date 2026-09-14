#!/bin/bash

# Script di test automatico per Simple_translator
# Testa tutti i file .lft nella directory corrente

PASSED=0
FAILED=0
CRASHED=0

echo "╔════════════════════════════════════════════════════════════╗"
echo "║        Test Suite - Simple_translator (30 test)            ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Array di test con risultati attesi
declare -A expected_results
declare -A expect_crash

# Test che NON dovrebbero crashare
expected_results[test01_simple]="OK"
expected_results[test03_assignment]="OK"
expected_results[test05_addition]="OK"
expected_results[test06_precedence]="OK"
expected_results[test07_division]="OK"
expected_results[test08_parentheses]="OK"
expected_results[test09_while_lt]="OK"
expected_results[test10_while_gt]="OK"
expected_results[test11_while_le]="OK"
expected_results[test12_while_ne]="OK"
expected_results[test13_and_logic]="OK"
expected_results[test14_or_logic]="OK"
expected_results[test15_nested_logic]="OK"
expected_results[test16_conditional_simple]="OK"
expected_results[test17_conditional_multi]="OK"
expected_results[test18_conditional_complex]="OK"
expected_results[test19_blocks]="OK"
expected_results[test20_while_block]="OK"
expected_results[test21_conditional_noblock]="OK"
expected_results[test22_many_vars]="OK"
expected_results[test26_mult_div]="OK"
expected_results[test27_while_false]="OK"
expected_results[test28_complex_precedence]="OK"
expected_results[test29_subtraction]="OK"
expected_results[test30_ge]="OK"

# Test che DEVONO crashare
expect_crash[test02_undefined_var]=1
expect_crash[test23_error_undefined]=1
expect_crash[test24_error_syntax]=1
expect_crash[test25_error_operator]=1
expect_crash[test04_user_input]=0  # Questo chiede input, non è un errore

# Ordina i file
test_files=(test{01..30}_*.lft)

for test_file in "${test_files[@]}"; do
    if [ ! -f "$test_file" ]; then
        continue
    fi
    
    test_name="${test_file%.*}"
    
    echo -n "[$test_name] ... "
    
    # Copia il file in input.lft
    cp "$test_file" input.lft
    
    # Esegui il traduttore e cattura stdout e stderr
    output=$(java Simple_translator 2>&1)
    exit_code=$?
    
    # Determina il risultato
    if [ $exit_code -eq 0 ]; then
        status="✅ PASS"
        ((PASSED++))
    else
        # Controlla se era atteso che crashasse
        if [ ${expect_crash[$test_name]:-0} -eq 1 ]; then
            status="✅ CRASH (expected)"
            ((PASSED++))
        else
            status="❌ FAIL (unexpected crash)"
            ((FAILED++))
        fi
        ((CRASHED++))
    fi
    
    echo "$status"
    
    # Mostra prime linee di output per debugging
    if [ $exit_code -ne 0 ]; then
        echo "   Error: $(echo "$output" | head -1)"
    fi
done

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║                      RISULTATI FINALI                      ║"
echo "╠════════════════════════════════════════════════════════════╣"
echo "║  Passed:  $PASSED"
echo "║  Failed:  $FAILED"
echo "║  Crashed: $CRASHED (expected or not)"
echo "╚════════════════════════════════════════════════════════════╝"

if [ $FAILED -eq 0 ]; then
    echo ""
    echo "🎉 Tutti i test sono OK!"
else
    echo ""
    echo "⚠️  $FAILED test hanno fallito"
fi
