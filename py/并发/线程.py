from threading import Thread,Lock
n=0
import time
def a(mutex):
    global n
    mutex.acquire()
    n+=1
    time.sleep(1)
    print(n)
    mutex.release()
mutex=Lock()
for i in range(5):
    t=Thread(target=a,args=(mutex,))
    t.start()