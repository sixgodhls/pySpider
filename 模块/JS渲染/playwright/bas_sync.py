from playwright.sync_api import sync_playwright

with sync_playwright() as p:
    for browser_type in [p.chromium,p.firefox,p.webkit]:
        browser=browser_type.launch(headless=False)
        page=browser.new_page()
        page.goto('https://www.baidu.com')
        print(page.title())
        browser.close()