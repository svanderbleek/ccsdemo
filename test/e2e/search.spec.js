const { test, expect } = require('@playwright/test');

test('search page loads the first data table', async ({ page }) => {
  await page.goto('/index.html');
  await page.getByRole('link', { name: 'Search' }).click();
  await expect(page.getByRole('heading', { name: 'Search' })).toBeVisible();

  await page.getByRole('button', { name: 'Start' }).click();

  const table = page.locator('table');
  await expect(table).toBeVisible();
  await expect(table.locator('thead th').first()).toBeVisible();
  await expect(table.locator('tbody tr').first()).toBeVisible();
});
