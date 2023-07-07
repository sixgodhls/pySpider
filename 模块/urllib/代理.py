from urllib.error import URLError
from urllib.request import ProxyHandler,build_opener
import 
proxy_handler=ProxyHandler(
    {
        'http':'http://127.0.0.1:8080',
        'https':'https://127.0.0.1:8080'
    }
)
opener=build_opener(proxy_handler)
try:
    response=opener.open('https://www/baidu/com')
    print(response)
except URLError as e:
    print(e.reason)