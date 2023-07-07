from playwright.sync_api import sync_playwright

def on_callback(response):
    print(f'{response.status}:{response.url}')
def onresp(response):
    if '/api/movie' in response.url and response.status==200:
        print(response.json())
with sync_playwright() as p:
    browser=p.chromium.launch(headless=False)
    context=browser.new_context()
    page=context.new_page()
    page.on('response',onresp)
    page.goto('https://spa2.scrape.center/')
    page.wait_for_load_state('networkidle')
    browser.close()