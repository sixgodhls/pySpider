import logging
import aiohttp
import asyncio
import json

logging.basicConfig(level=logging.INFO, format='%(asctime)s-%(levelname)s: %(message)s')
index_url = 'https://spa5.scrape.center/api/book/?limit=18&offset={offset}'
detail_url = 'https://spa5.scrape.center/api/book/{id}'
page_size = 18
page_number = 1
concurrency = 5
semaphore = asyncio.Semaphore(concurrency)
session = None

# 解析网页
async def get_page(url):
    async with semaphore:
        try:
            logging.info(f'scraping {url}')
            async with session.get(url) as resp:
                return await resp.json()
        except aiohttp.ClientError:
            logging.error('error')

# 拼接url 调用get_page方法返回json串
async def scrape_page(page):
    url = index_url.format(offset=page_size * (page - 1))
    return await get_page(url)

# 异步mongodb存储基本配置
from motor.motor_asyncio import AsyncIOMotorClient

mongo_connecting = 'mongodb://localhost:27017'
mongo_db_name = 'books'
mongo_collection = 'books'

client = AsyncIOMotorClient(mongo_connecting)
db = client[mongo_db_name]
collection = db[mongo_collection]

# mongodb异步存储
async def save_data(data):
    logging.info('saving data %s', data)
    if data:
        return await collection.update_one(
            {
                'id': data.get('id')
            }, {
                '$set': data
            }, upsert=True
        )
# 解析详情页
async def scrape_detail(id):
    url=detail_url.format(id=id)
    data=await get_page(url)
    await save_data(data)
#函数串用
async def main():
    global session
    session = aiohttp.ClientSession()
    scrape_index_tasks = [asyncio.ensure_future(scrape_page(page)) for page in range(1, page_number + 1)]
    result = await asyncio.gather(*scrape_index_tasks)
    logging.info('result%s', json.dumps(result, ensure_ascii=False, indent=2))
    ids = []
    for index_data in result:
        if not index_data: continue
        for item in index_data.get('results'):
            ids.append(item.get('id'))
    scrape_detail_tasks=[asyncio.ensure_future(scrape_detail(x)) for x in ids]
    await asyncio.wait(scrape_detail_tasks)
    await session.close()

if __name__ == '__main__':
    asyncio.get_event_loop().run_until_complete(main())


