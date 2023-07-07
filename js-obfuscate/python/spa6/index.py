import hashlib
import time
import base64
from typing import List,Any
import requests

index_url='https://spa6.scrape.center/api/movie/?limit={limit}&offset={offset}&token={token}'
limit=10
offset=0

def get_token(args:List[Any]):
    timestamp=str(int(time.time()))
    args.append(timestamp)
    sign=hashlib.sha1(','.join(args).encode('utf-8')).hexdigest()
    return base64.b64encode(','.join([sign,timestamp]).encode('utf-8')).decode('utf-8')

args=['/api/movie']
token=get_token(args)
index_url=index_url.format(limit=limit,offset=offset,token=token)
response=requests.get(index_url)
print(response.text)