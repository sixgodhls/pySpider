import requests

url = 'http://localhost:8050/render.png?url=https://spa5.scrape.center/page/1'
# session=requests.Session()
# unpre_url=requests.Request('get',url)
# pre_url=unpre_url.prepare()
# context=session.send(pre_url)
# print(context.text)
# content_text=requests.get(url).text
# print(content_text)
resp=requests.get(url)
with open('x.png','wb') as fp:
    fp.write(resp.content)