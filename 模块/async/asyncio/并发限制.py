import aiohttp
import asyncio

max = 5
url = 'https://www.baidu.com'

semaphore = asyncio.Semaphore(max)


# session=None

async def getapi():
    async with semaphore:
        print(url)
        async with session.get(url) as resp:
            await asyncio.sleep(1)
            return await resp.text()


async def main():
    global session
    session = aiohttp.ClientSession()
    scrape_index_tasks = [asyncio.ensure_future(getapi()) for _ in range(200)]
    await asyncio.gather(*scrape_index_tasks)


if __name__ == '__main__':
    asyncio.get_event_loop().run_until_complete(main())
