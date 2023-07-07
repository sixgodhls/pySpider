import aiohttp
import asyncio

async def get(url):
    session=aiohttp.ClientSession()
    resp=await session.get(url)
    # 必须要加session.close
    await session.close()

    return resp

async def request():
    url='https://www.httpbin.org/delay/5'
    print(f'get{url}')
    resp=await get(url)
    print(f'get{resp}]')


tasks=[asyncio.ensure_future(request()) for _ in range(4)]
loop=asyncio.get_event_loop()
loop.run_until_complete(asyncio.wait(tasks))
