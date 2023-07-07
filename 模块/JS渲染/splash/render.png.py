import requests
url='http://localhost:8050/render.png?url=https://www.baidu.com&height=800&width=800'
resp=requests.get(url)
with open('baidu.png','wb') as fp:
    fp.write(resp.content)