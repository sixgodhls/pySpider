import asyncio
from pyppeteer import launch
from pyquery import PyQuery as pq
import time
import nest_asyncio


nest_asyncio.apply()#异步嵌套异步
async def main(urls,sem):
    prefs = {"profile.managed_default_content_settings.images": 2}
    web=await launch(headless=False,options=prefs)


    #终极无敌版 最小化io
    # async def get_content(url,):
    #     async with sem:
    #         page=await web.newPage()


    # sem=asyncio.Semaphore(sem)
    # task=[get_content(url) for url in urls]
    # asyncio.get_event_loop().run_until_complete(asyncio.wait(task))
    #终极版 控制信号量 只要开一个浏览器 还可以优化 造page和关page都是io 可以固定页面

    async def get_content(url,):
        async with sem:
            page = await web.newPage()
            await page.goto(url)
            await page.waitForSelector('.el-card__body .name')
            doc = pq(await page.content())
            name = [item.text() for item in doc('.el-card__body .name').items()]
            await page.close()
            print(name)
    sem=asyncio.Semaphore(sem)
    task=[get_content(url) for url in urls]
    asyncio.get_event_loop().run_until_complete(asyncio.wait(task))
    await web.close()


def get_urls(page_num):
    url = 'https://spa5.scrape.center/page/{}'
    urls = []
    for i in range(page_num):
        urls.append(url.format(i + 1))
    return urls
if __name__ == '__main__':
    start=time.time()
    urls=get_urls(page_num=99)
    sem=8
    asyncio.get_event_loop().run_until_complete(main(urls,sem))
    end=time.time()
    print(end-start)