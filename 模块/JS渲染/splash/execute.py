import requests
from urllib.parse import quote

lua='''
function main(splash)
return 'hello'
end
'''
print(quote(lua))
url='http://localhost:8050/execute?lua_source='+quote(lua)
res=requests.get(url)
print(res.text)