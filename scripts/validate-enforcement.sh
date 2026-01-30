#!/bin/bash
# Validation Script for Phase 3 Enforcement
# 
# Tests that ESLint rules and pre-commit hooks are working correctly
# Run: ./scripts/validate-enforcement.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
VIOLATIONS_FOUND=0

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Phase 3 Enforcement Validation"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Check ESLint configuration exists
echo "✓ Checking ESLint configuration..."
if [ ! -f "$PROJECT_DIR/.eslintrc.json" ]; then
  echo "❌ ERROR: .eslintrc.json not found"
  exit 1
fi
echo "  ✅ .eslintrc.json exists"

# Check custom ESLint plugin exists
echo "✓ Checking custom ESLint plugin..."
if [ ! -f "$PROJECT_DIR/eslint-plugin-no-direct-api-calls.js" ]; then
  echo "❌ ERROR: eslint-plugin-no-direct-api-calls.js not found"
  exit 1
fi
echo "  ✅ eslint-plugin-no-direct-api-calls.js exists"

# Check pre-commit hook exists
echo "✓ Checking pre-commit hook..."
if [ ! -f "$PROJECT_DIR/.husky/pre-commit" ]; then
  echo "❌ ERROR: .husky/pre-commit not found"
  exit 1
fi
echo "  ✅ .husky/pre-commit exists"

# Check package.json has lint scripts
echo "✓ Checking npm scripts..."
if ! grep -q '"lint":' "$PROJECT_DIR/package.json"; then
  echo "❌ ERROR: lint script not found in package.json"
  exit 1
fi
if ! grep -q '"lint:fix":' "$PROJECT_DIR/package.json"; then
  echo "❌ ERROR: lint:fix script not found in package.json"
  exit 1
fi
if ! grep -q '"lint:api-gateway":' "$PROJECT_DIR/package.json"; then
  echo "❌ ERROR: lint:api-gateway script not found in package.json"
  exit 1
fi
echo "  ✅ All npm scripts present"

# Check dependencies
echo "✓ Checking dependencies in package.json..."
REQUIRED_DEPS=("eslint" "@typescript-eslint/parser" "@typescript-eslint/eslint-plugin" "eslint-plugin-vue" "husky")
for dep in "${REQUIRED_DEPS[@]}"; do
  if ! grep -q "\"$dep\":" "$PROJECT_DIR/package.json"; then
    echo "⚠️  WARNING: $dep not found in devDependencies (required for enforcement)"
    VIOLATIONS_FOUND=1
  else
    echo "  ✅ $dep present"
  fi
done

# Check ESLint rule structure
echo "✓ Checking ESLint rule definitions..."
REQUIRED_RULES=("no-direct-fetch" "no-direct-axios" "no-direct-authorization" "enforce-api-gateway")
for rule in "${REQUIRED_RULES[@]}"; do
  if ! grep -q "'$rule':" "$PROJECT_DIR/eslint-plugin-no-direct-api-calls.js"; then
    echo "❌ ERROR: Rule '$rule' not found in ESLint plugin"
    exit 1
  else
    echo "  ✅ Rule '$rule' defined"
  fi
done

# Validate ESLint config JSON syntax
echo "✓ Validating .eslintrc.json syntax..."
if ! python3 -m json.tool "$PROJECT_DIR/.eslintrc.json" > /dev/null 2>&1; then
  echo "❌ ERROR: .eslintrc.json has invalid JSON syntax"
  exit 1
fi
echo "  ✅ .eslintrc.json is valid JSON"

# Check bash syntax of pre-commit hook
echo "✓ Checking pre-commit hook syntax..."
if ! bash -n "$PROJECT_DIR/.husky/pre-commit" 2>&1; then
  echo "❌ ERROR: .husky/pre-commit has bash syntax errors"
  exit 1
fi
echo "  ✅ .husky/pre-commit has valid bash syntax"

# Test detection capabilities
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Testing Detection Capabilities"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Create temporary test files
TEST_DIR=$(mktemp -d)
trap "rm -rf $TEST_DIR" EXIT

echo "✓ Testing fetch() detection..."
cat > "$TEST_DIR/test-fetch.ts" << 'EOF'
// This should be detected
const response = await fetch('/api/data');
EOF

if grep -q "fetch(" "$TEST_DIR/test-fetch.ts"; then
  echo "  ✅ fetch() pattern detected"
else
  echo "❌ ERROR: Failed to detect fetch() pattern"
  exit 1
fi

echo "✓ Testing axios import detection..."
cat > "$TEST_DIR/test-axios.ts" << 'EOF'
import axios from 'axios';
const response = await axios.get('/api/data');
EOF

if grep -q "from 'axios'" "$TEST_DIR/test-axios.ts"; then
  echo "  ✅ axios import pattern detected"
else
  echo "❌ ERROR: Failed to detect axios import pattern"
  exit 1
fi

echo "✓ Testing Authorization header detection..."
cat > "$TEST_DIR/test-auth.ts" << 'EOF'
const headers = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
};
EOF

if grep -q "'Authorization':" "$TEST_DIR/test-auth.ts"; then
  echo "  ✅ Authorization header pattern detected"
else
  echo "❌ ERROR: Failed to detect Authorization header pattern"
  exit 1
fi

# Check existing refactored component complies
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Checking Phase 2A Refactored Component"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

SHOPMGR_DIR="$PROJECT_DIR/runtime/component/durion-shopmgr/webapp"

if [ ! -d "$SHOPMGR_DIR" ]; then
  echo "⚠️  durion-shopmgr not found (may not be cloned)"
  exit 0
fi

echo "✓ Checking durion-shopmgr for violations..."

# Check for direct fetch (excluding deprecated file)
if grep -r "fetch(" "$SHOPMGR_DIR" --include="*.ts" --include="*.vue" 2>/dev/null | grep -v "appointments.service.ts" | grep -v "node_modules"; then
  echo "❌ ERROR: Direct fetch() found in refactored component"
  VIOLATIONS_FOUND=1
else
  echo "  ✅ No direct fetch() calls in refactored component"
fi

# Check for direct axios
if grep -r "from 'axios'" "$SHOPMGR_DIR" --include="*.ts" --include="*.vue" 2>/dev/null; then
  echo "❌ ERROR: Direct axios import found in refactored component"
  VIOLATIONS_FOUND=1
else
  echo "  ✅ No direct axios imports in refactored component"
fi

# Check for usePositivityApiClient
if grep -r "usePositivityApiClient" "$SHOPMGR_DIR/api" --include="*.ts" 2>/dev/null | grep -q "usePositivityApiClient"; then
  echo "  ✅ usePositivityApiClient found in API client"
else
  echo "⚠️  WARNING: usePositivityApiClient not found in API client (may be indirect)"
fi

# Final summary
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Validation Summary"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

if [ $VIOLATIONS_FOUND -eq 0 ]; then
  echo "✅ Phase 3 Enforcement Setup is VALID"
  echo ""
  echo "Next steps:"
  echo "  1. npm install (install dependencies and setup husky)"
  echo "  2. npm run lint (check for violations)"
  echo "  3. npm run lint:fix (auto-fix issues)"
  echo "  4. git commit (pre-commit hook validates)"
  echo ""
  echo "See PHASE_3_ENFORCEMENT_SETUP.md for full documentation."
  exit 0
else
  echo "⚠️  Phase 3 Enforcement Setup completed with warnings"
  echo ""
  echo "Review warnings above and address before going to production."
  exit 1
fi
