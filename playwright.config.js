// Run against an already running dev server (clj -M:dev).
// Set BASE_URL to match the port in resources/config.edn.
const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './test/e2e',
  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:3001',
  },
});
