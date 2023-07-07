import asyncio
from pyppeteer import launch
from pyquery import PyQuery as pq

async def main():
    web=await launch(headless=0)
    page=await web.newPage()
    await page.evaluateOnNewDocument('Object.defineProperty(navigator,"webdriver",{get:()=>undefined})')
    await page.goto('https://spa2.scrape.center/')
    await page.waitForSelector('.item .name')
    await asyncio.sleep(1)
    #第一个参数 选择器设置 第二个参数 鼠标点击事件参数
    await page.click('.item .name',options={
        'button':'right',#左中右键
        'clickCount':1,#点击几下
        'delay':3000,#延迟几毫秒
    })
    await web.close()
asyncio.get_event_loop().run_until_complete(main())
# from pyppeteer import launch
# import asyncio
# import time
# async def main():
#     # 启动一个浏览器 设置浏览器窗口大小
#     browser = await launch(headless=False,args=['--disable-infobars','--window-size=1366,768'])
#     # 创建一个页面
#     page = await browser.newPage()
#     # 跳转到百度 设置可视端口大小
#     await page.setViewport({'width':1366,'height':768})
#     await page.goto("http://www.baidu.com/")
#     await asyncio.sleep(1)
#     # 输入要查询的关键字，type第一个参数是元素的selector，第二个是要输入的关键字
#     await page.type('#kw', 'pyppeteer')
#     await asyncio.sleep(1)
#     # 点击提交按钮 click通过selector点击指定的元素
#     await page.click('#su')
#     time.sleep(3)
#     await browser.close()
# asyncio.get_event_loop().run_until_complete(main())