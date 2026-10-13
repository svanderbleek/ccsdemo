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

test('numeric columns sort descending first, text columns ascending', async ({ page }) => {
  await page.goto('/index.html');
  await page.getByRole('link', { name: 'Search' }).click();
  await page.getByRole('button', { name: 'Start' }).click();
  await expect(page.locator('tbody tr').first()).toBeVisible();

  const header = (name) => page.locator('thead th', { hasText: name });
  const column = async (index) =>
    page.locator(`tbody tr td:nth-child(${index})`).allInnerTexts();

  await header('payout').click();
  await expect(header('payout')).toHaveAttribute('aria-sort', 'descending');
  const desc = (await column(2)).map(Number);
  expect(desc).toEqual([...desc].sort((a, b) => b - a));

  await header('payout').click();
  await expect(header('payout')).toHaveAttribute('aria-sort', 'ascending');
  const asc = (await column(2)).map(Number);
  expect(asc).toEqual([...asc].sort((a, b) => a - b));

  await header('state').click();
  await expect(header('state')).toHaveAttribute('aria-sort', 'ascending');
  const states = await column(1);
  expect(states).toEqual([...states].sort());
});
