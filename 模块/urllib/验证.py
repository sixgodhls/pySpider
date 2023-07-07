from urllib.request import HTTPPasswordMgrWithDefaultRealm,HTTPBasicAuthHandler,build_opener
from urllib.error import URLError

username='admin'
pwd='admin'
url='https://ssr3.scrape.center/'

p=HTTPPasswordMgrWithDefaultRealm()
p.add_password(None,url,username,pwd)
auth_handler=HTTPBasicAuthHandler(p)
opener=build_opener(auth_handler)

try:
    result=opener.open(url)
    html=result.read().decode('utf-8')
    print(html)
except URLError as e :
    print(e.reason)