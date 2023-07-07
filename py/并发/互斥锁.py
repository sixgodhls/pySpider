from multiprocessing import Process,Lock
import time

def test(name,lock):
    lock.acquire()
    print(f'第{name}进程 1')
    time.sleep(1)
    print(f'第{name}进程 2')
    time.sleep(1)
    print(f'第{name}进程 3')
    lock.release()
if __name__ == '__main__':
    lock = Lock()
    for i in range(3):
        p=Process(target=test,args=(f'{i+1}',lock))
        p.start()