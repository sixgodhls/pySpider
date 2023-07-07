import pywasm
import time
import requests

base_url='https://spa14.scrape.center'
total=10

runtime=pywasm.load('./Wasm.wasm')
for i in range(total):
    offset=i*10
    sign=runtime.exec('encrypt',[offset,int(time.time())])
    url=f'{base_url}/api/movie/?limit=10&offset={offset}&sign={sign}'
    response=requests.get(url).text
    print(response)