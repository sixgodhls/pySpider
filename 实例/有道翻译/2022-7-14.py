import requests
import execjs
import re
from urllib.parse import urlencode
parmars='''
i:,
from:AUTO,
to:AUTO,
smartresult:dict,
client:fanyideskweb,
salt:,
sign:,
lts:,
bv: ,
doctype:json,
version:2.1,
keyfrom:fanyi.web,
action:FY_BY_REALTlME,
'''
def turn_dict(parmars):
    com=re.compile('(.*?):(.*?),')
    result=re.findall(com,parmars)
    parmars_strs=''
    for i in result:
        parmars_str='"%s":"%s",'%(str(i[0]),str(i[1]))
        parmars_strs+=parmars_str
    parmars_strs='{'+str(parmars_strs[:-1])+'}'
    parmars_dict = eval(parmars_strs)
    return parmars_dict



def get_parmars(parmars_dict):
    item = input('要翻译什么：')
    parmars_dict['i'] = item
    file = './rti.js'
    node = execjs.get()
    ctx = node.compile(open(file, encoding='utf-8').read())
    js = f'get_dict("{item}")'
    result = ctx.eval(js)
    cookie = int(result.get('lts')) - 2
    for key in result:
        parmars_dict[key] = result.get(key)
    return cookie,parmars_dict


def get_headers(cookie):
    headers = {
        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
        'Cookie': f'OUTFOX_SEARCH_USER_ID=805725873@10.110.96.154; OUTFOX_SEARCH_USER_ID_NCOO=490906215.9815523; fanyi-ad-id=307888; fanyi-ad-closed=1; ___rl__test__cookies={cookie}',
        'Referer': 'https://fanyi.youdao.com/',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36',
    }
    return headers
if __name__ == '__main__':
    parmars_dict=turn_dict(parmars)
    cookie,parmars_dict=get_parmars(parmars_dict)
    headers=get_headers(cookie)
    data=urlencode(parmars_dict)
    base_url='https://fanyi.youdao.com/translate_o?smartresult=dict&smartresult=rule'
    response=requests.post(url=base_url,data=data,headers=headers).text
    print(eval(response))