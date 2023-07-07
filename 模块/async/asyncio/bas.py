import asyncio

async def bas(x):
        print(x)
coroutine=bas(1)
print(2)
loop=asyncio.get_event_loop()
print(3)
task=loop.create_task(coroutine)
print(4)
loop.run_until_complete(task)
print(task)


