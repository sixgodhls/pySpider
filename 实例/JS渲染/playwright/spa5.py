from playwright.async_api import async_playwright
import asyncio
from pyquery import PyQuery as pq
import time
import nest_asyncio
nest_asyncio.apply()
async def main(urls,sem):

        async with async_playwright() as p:
            web=await p.chromium.launch(headless=False)
            context=await web.new_context()
            async def get_content(url,):
                async with sem:
                    page=await context.new_page()
                    await page.goto(url)
                    await page.wait_for_selector('.el-card__body .name')
                    doc=pq(await page.content())
                    name=[item.text() for item in doc('.el-card__body .name').items()]
                    await page.close()
                    print(name)
            sem=asyncio.Semaphore(sem)
            task=[get_content(url) for url in urls]
            asyncio.get_event_loop().run_until_complete(asyncio.wait(task))
        # page=await web_bro.new_page()
        # for i in range(100):
        #     await page.goto(url.format(i+1))
        #     await page.wait_for_selector('.el-card__body .name')
        #     doc=pq(await page.content())
        #     name=[item.text() for item in doc('.el-card__body .name').items()]
        #     print(name)
def get_urls(page_num):
    url = 'https://spa5.scrape.center/page/{}'
    urls = []
    for i in range(page_num):
        urls.append(url.format(i + 1))
    return urls
if __name__ == '__main__':
    start = time.time()
    urls=get_urls(99)
    sem=8
    asyncio.get_event_loop().run_until_complete(main(urls,sem))
    end = time.time()
    print(end - start)