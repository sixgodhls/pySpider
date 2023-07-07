import requests

url='https://q.stock.sohu.com/hisHq?code=cn_600963&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&0.36249741963120896'
print(requests.get(url).text)