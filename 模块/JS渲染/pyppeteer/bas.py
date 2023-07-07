import asyncio
from pyppeteer import launch
from pyquery import PyQuery as pq

async def main():
    web=await launch(headless=False,devtools=1)#默认是无头模式,可以选择弹出调试工具
    page=await web.newPage()
    # 在页面加载之前隐藏webdrive 防止检测用的
    await page.evaluateOnNewDocument('Object.defineProperty(navigator,"webdriver",{get:()=>undefined})')
    await page.goto('https://spa2.scrape.center/')
    await page.waitForSelector('.item .name') #等待这个节点加载出来后执行下一个步骤
    # print(await page.content())
    doc=pq(await page.content())
    # print(doc)
    names=[item.text() for item in doc('.item .name').items()]
    print(names)
    await web.close()
asyncio.get_event_loop().run_until_complete(main())

