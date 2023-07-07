import requests
url='http://localhost:8050/render.html?url=https://www.baidu.com'
resp=requests.get(url)
print(resp.text)