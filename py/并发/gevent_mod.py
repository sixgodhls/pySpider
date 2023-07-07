import asyncio,time
from gevent import monkey;monkey.patch_all()
import gevent
def a():
    print('a start')
    time.sleep(3)
    print('a end')
def b():
    print('b start')
    time.sleep(2)
    print('b end')
async def main():
    g1=gevent.spawn(a)
    g2=gevent.spawn(b)
    g1.join()
    g2.join()

asyncio.run(main())