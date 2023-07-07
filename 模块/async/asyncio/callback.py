import asyncio
import requests
async def request():
    url='https://www.baidu.com'
    status=requests.get(url)
    return status
def callback(task):
    print(task.result())

task=asyncio.ensure_future(request())
task.add_done_callback(callback)
print(task)
print(1)
loop=asyncio.get_event_loop()
print(2)
loop.run_until_complete(task)
print(task)