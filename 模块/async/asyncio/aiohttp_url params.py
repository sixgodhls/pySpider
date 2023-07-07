import aiohttp
import asyncio

async def main():
    params={
        'name':'ger',
        'age':25
    }
    timeout=aiohttp.ClientTimeout(total=1) #设置超时时间
    async with aiohttp.ClientSession(timeout=timeout) as session:
        async with session.get('https://httpbin.org/get',params=params) as resp:
            print(await resp.text())
if __name__ == '__main__':
    loop=asyncio.get_event_loop()
    loop.run_until_complete(main())