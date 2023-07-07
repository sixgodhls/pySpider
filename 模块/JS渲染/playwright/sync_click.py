from playwright.sync_api import sync_playwright
from time import sleep
with sync_playwright() as p:
    browser=p.chromium.launch(headless=False)
    context=browser.new_context()
    page=context.new_page()
    page.goto('https://spa2.scrape.center/')
    #xpath定位第二个电影点击 有很多选择器可以使用
    page.click('xpath=//div[@class="el-col el-col-18 el-col-offset-3"]/div[2]//img')
    sleep(4)
    browser.close()