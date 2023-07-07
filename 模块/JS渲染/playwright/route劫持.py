from playwright.sync_api import sync_playwright
import re
from time import sleep
with sync_playwright() as p:
    browser=p.chromium.launch(headless=False)
    page=browser.new_page()

    def cancel_request(route,request):
        route.abort()
    page.route(re.compile(r'(\.png|\.jpg)'),cancel_request)
    page.goto('https://spa6.scrape.center/')
    page.wait_for_load_state('networkidle')
    # page.screenshot(path='./no_picture.png')
    sleep(4)
    browser.close()
