import logging
from pyppeteer.errors import TimeoutError
from pyppeteer import launch
import asyncio

logging.basicConfig(level=logging.INFO,format='%(asctime)s -%(levelname)s : %(message)s')
index_url='https://spa2.scrape.center/page/{page}'
timeout=10
total_page=10
window_width,window_height=1366,768
headless=False
browser,tab=None,None
#先初始化一个浏览器
async def init():
    global browser, tab
    browser=await launch(headless=headless,args=['--disable-infobars',f'--window-size={window_width},{window_height}'])
    tab=await browser.newPage()

    await tab.setViewport({'width':window_width,'height':window_height})

#通用的爬取
async def scrape_page(url,selector):
    logging.info('scrapeing %s',url)
    try:
        await tab.goto(url)
        #等待selector选择器选择的东西加载出来
        await tab.waitForSelector(selector=selector,options={'timeout':timeout*1000})
    except TimeoutError:
        logging.error('error while scraping %',url)
#爬取指定页数
async def scrape_index(page):
    url=index_url.format(page=page)
    await scrape_page(url,'.item ,name')
#爬取内容
async def parse_index():
    return await tab.querySelectorAllEval('.item .name','nodes=>nodes.map(node=>node.href)')
#详情页加载
async def scrape_detail(url):
    await scrape_page(url,'h2')
#爬取详情页的指定内容
async def parse_detail():
    url=tab.url
    name= await tab.querySelectorEval('h2','node=>node.innerText')
    categories=await tab.querySelectorAllEval('.categories button span','nodes=>nodes.map(node=>node.innerText)')
    return {
        'url':url,
        'name':name,
        'categories':categories
    }
#函数串用
async def main():
    await init()
    try:
        for page in range(1,total_page+1):
            #跳转到相关页面
            await scrape_index(page)
            #得到所有详情页的url
            detail_urls=await parse_index()
            logging.info('detail_url %s',detail_urls)
            #for循环
            for detail_url in detail_urls:
                #跳转到详情页
                await scrape_detail(detail_url)
                #得到详情页内容
                detail_data=await parse_detail()
                logging.info('scraped %s',detail_data)
    finally:
        await browser.close()

if __name__ == '__main__':
    asyncio.get_event_loop().run_until_complete(main())