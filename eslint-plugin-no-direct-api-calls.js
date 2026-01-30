/**
 * ESLint Plugin: no-direct-api-calls
 * 
 * Enforces ADR-0010: Frontend Domain Responsibilities
 * - Prevents direct fetch() calls outside durion-positivity component
 * - Prevents direct axios imports
 * - Prevents manual Authorization header construction
 * - Enforces centralized API gateway pattern
 */

module.exports = {
  rules: {
    'no-direct-fetch': {
      meta: {
        type: 'problem',
        docs: {
          description: 'Disallow direct fetch() calls in durion-* components. Use durion-positivity gateway instead.',
          category: 'Best Practices',
          recommended: true,
          url: 'https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md',
        },
        fixable: null,
        messages: {
          forbidden: '❌ ARCHITECTURE VIOLATION: Direct fetch() call detected in {{ componentPath }}.\n\nREQUIRED: Route all API calls through durion-positivity component.\n\nCORRECT PATTERN:\n  import { usePositivityApiClient } from "durion-positivity";\n  const apiClient = usePositivityApiClient();\n  const result = await apiClient.request({\n    service: "your-service",\n    endpoint: "/your/endpoint",\n    method: "POST",\n    data: requestData\n  });\n\nSee ADR-0010 for full documentation: https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md',
        },
      },
      create(context) {
        const filename = context.getFilename();
        
        // Skip durion-positivity/webapp directory (allowed to use fetch)
        if (filename.includes('durion-positivity/webapp')) {
          return {};
        }
        
        // Skip non-component files
        if (!filename.includes('durion-') || (!filename.includes('.ts') && !filename.includes('.js') && !filename.includes('.vue'))) {
          return {};
        }

        return {
          CallExpression(node) {
            // Check for fetch() calls
            if (node.callee.name === 'fetch') {
              context.report({
                node,
                messageId: 'forbidden',
                data: {
                  componentPath: filename.replace(process.cwd(), ''),
                },
              });
            }
            
            // Check for fetch in window.fetch
            if (
              node.callee.type === 'MemberExpression' &&
              node.callee.object.name === 'window' &&
              node.callee.property.name === 'fetch'
            ) {
              context.report({
                node,
                messageId: 'forbidden',
                data: {
                  componentPath: filename.replace(process.cwd(), ''),
                },
              });
            }
          },
        };
      },
    },

    'no-direct-axios': {
      meta: {
        type: 'problem',
        docs: {
          description: 'Disallow direct axios imports in durion-* components. Use durion-positivity gateway instead.',
          category: 'Best Practices',
          recommended: true,
          url: 'https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md',
        },
        fixable: null,
        messages: {
          forbidden: '❌ ARCHITECTURE VIOLATION: Direct axios import detected in {{ componentPath }}.\n\nREQUIRED: Route all API calls through durion-positivity component.\n\nCORRECT PATTERN:\n  import { usePositivityApiClient } from "durion-positivity";\n  const apiClient = usePositivityApiClient();\n  const result = await apiClient.request({\n    service: "your-service",\n    endpoint: "/your/endpoint",\n    method: "POST",\n    data: requestData\n  });\n\nSee ADR-0010 for full documentation: https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md',
        },
      },
      create(context) {
        const filename = context.getFilename();
        
        // Skip durion-positivity/webapp directory
        if (filename.includes('durion-positivity/webapp')) {
          return {};
        }
        
        // Skip non-component files
        if (!filename.includes('durion-') || (!filename.includes('.ts') && !filename.includes('.js') && !filename.includes('.vue'))) {
          return {};
        }

        return {
          ImportDeclaration(node) {
            // Check for axios imports
            if (node.source.value === 'axios') {
              context.report({
                node,
                messageId: 'forbidden',
                data: {
                  componentPath: filename.replace(process.cwd(), ''),
                },
              });
            }
          },
        };
      },
    },

    'no-direct-authorization': {
      meta: {
        type: 'problem',
        docs: {
          description: 'Disallow manual Authorization header construction. Let durion-positivity gateway handle auth.',
          category: 'Best Practices',
          recommended: true,
          url: 'https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md',
        },
        fixable: null,
        messages: {
          forbidden: '❌ SECURITY VIOLATION: Manual Authorization header detected in {{ componentPath }}.\n\nREQUIRED: Let durion-positivity gateway inject auth tokens automatically.\n\nWRONG:\n  headers: { "Authorization": `Bearer ${token}` }\n\nCORRECT:\n  import { usePositivityApiClient } from "durion-positivity";\n  const apiClient = usePositivityApiClient();\n  // Gateway automatically injects Authorization header\n\nSee ADR-0010 for full documentation: https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md',
        },
      },
      create(context) {
        const filename = context.getFilename();
        
        // Skip durion-positivity/webapp directory
        if (filename.includes('durion-positivity/webapp')) {
          return {};
        }
        
        // Skip non-component files
        if (!filename.includes('durion-') || (!filename.includes('.ts') && !filename.includes('.js') && !filename.includes('.vue'))) {
          return {};
        }

        return {
          Property(node) {
            // Check for 'Authorization' key in object literals
            if (
              (node.key.name === 'Authorization' || node.key.value === 'Authorization') &&
              node.value.type === 'TemplateLiteral'
            ) {
              context.report({
                node,
                messageId: 'forbidden',
                data: {
                  componentPath: filename.replace(process.cwd(), ''),
                },
              });
            }
            
            // Check for string literal 'Authorization' key
            if (
              node.key.type === 'Literal' &&
              node.key.value === 'Authorization'
            ) {
              context.report({
                node,
                messageId: 'forbidden',
                data: {
                  componentPath: filename.replace(process.cwd(), ''),
                },
              });
            }
          },
        };
      },
    },

    'enforce-api-gateway': {
      meta: {
        type: 'suggestion',
        docs: {
          description: 'Enforce use of durion-positivity gateway pattern in components.',
          category: 'Best Practices',
          recommended: true,
          url: 'https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md',
        },
        fixable: null,
        messages: {
          suggestApiClient: 'Consider using durion-positivity API gateway for consistency.',
        },
      },
      create(context) {
        const filename = context.getFilename();
        
        // Only check durion-* components with API/store patterns
        if (!filename.includes('durion-') || !filename.includes('/webapp/')) {
          return {};
        }
        
        // Skip durion-positivity gateway itself
        if (filename.includes('durion-positivity/webapp')) {
          return {};
        }

        // This rule is for documentation purposes
        // The other rules handle the hard blocks
        return {};
      },
    },
  },
};
