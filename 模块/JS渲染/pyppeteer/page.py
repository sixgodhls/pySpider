import asyncio
from pyppeteer import launch
from pyquery import PyQuery as pq

async def main():
    web=await launch()
    page=await web.newPage()
    await page.goto('https://spa2.scrape.center/')
    await page.waitForSelector('.item .name')
    j_res=await page.J('.item .name')
    print(j_res)
    j_res=await page.querySelector('.item .name')
    print(j_res)
    j_res=await page.JJ('.item .name')
    print(j_res)
    j_res=await page.querySelectorAll('.item .name')
    print(j_res)
    await web.close()

asyncio.get_event_loop().run_until_complete(main())