'''
params

encSecKey

e2x.data = cr3x({
                params: bKB4F.encText,
                encSecKey: bKB4F.encSecKey
            })
cr3x = function(gL5Q) {
        return xl9c(gL5Q, "&", !0)
    }
xl9c = function(gL5Q, Xf8X, cFB1x) {
        if (!gL5Q)
            return "";
        var bx3x = [];
        for (var x in gL5Q) {
            bx3x.push(encodeURIComponent(x) + "=" + (!!cFB1x ? encodeURIComponent(gL5Q[x]) : gL5Q[x]))
        }
        return bx3x.join(Xf8X || ",")
    }
bKB4F=window.asrsea(JSON.stringify(i2x), buV2x(["流泪", "强"]), buV2x(Rg7Z.md), buV2x(["爱心", "女孩", "惊恐", "大笑"]))
bKB4F=window.asrsea(JSON.stringify(i2x), "010001","00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7", "0CoJUm6Qyw8W8jud")
i2x={
csrf_token: ""
cursor: "-1"
offset: "0"
orderType: "1"
pageNo: "1"
pageSize: "20"
rid: "R_SO_4_1970554437"
threadId: "R_SO_4_1970554437"
}
function asrsea(d, e, f, g) {
        var h = {}
          , i = a(16);
        return h.encText = b(d, g),
        h.encText = b(h.encText, i),
        h.encSecKey = c(i, e, f),
        h
    }
function b(a, b) {
        var c = CryptoJS.enc.Utf8.parse(b)
          , d = CryptoJS.enc.Utf8.parse("0102030405060708")
          , e = CryptoJS.enc.Utf8.parse(a)
          , f = CryptoJS.AES.encrypt(e, c, {
            iv: d,
            mode: CryptoJS.mode.CBC
        });
        return f.toString()
    }

'''

'''
url='https://music.163.com/weapi/comment/resource/comments/get?csrf_token='


'''

import requests
import execjs
import re
from urllib.parse import urlencode
a='''authority: music.163.com
method: POST
path: /weapi/comment/resource/comments/get?csrf_token=
scheme: https
accept: */*
accept-encoding: gzip, deflate, br
accept-language: zh-CN,zh;q=0.9
cache-control: no-cache
content-length: 590
content-type: application/x-www-form-urlencoded
cookie: _iuqxldmzr_=32; _ntes_nnid=7183e25fc95d70a53f149d142d1f4997,1662545146457; _ntes_nuid=7183e25fc95d70a53f149d142d1f4997; NMTID=00OEtYQ9zrDEy0REElhoGwUom14JGAAAAGDF2mx5A; WNMCID=qgbypi.1662545146848.01.0; WEVNSM=1.0.0; WM_TID=PuDXdRurU8pEVFUBARPAC0ZrONPbcgp3; WM_NI=w9qX0h%2FZLHiwqk%2B8uW%2B2fJlBGsXIXUm0%2Fr%2B6JjclQ8zWyggFm30hNIM84qPSvJRqCpV%2BsxkjAjFJHbyZJkb106xuIlvy63sFRXtmwP64C3tcWwz62zBXGDpMT%2BQoV2OkNmw%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6eeb4d260a9ec988ff24af78e8eb2d55a829a9aacc460f4b1a2b4b1709baeb6d1f72af0fea7c3b92ab5e7bfb9b364ed8684a9ec7db3b5e5d9f85de9eeaaabef4a85ad98aadb5faff0e193aa3e8ab6ad8dd434ba89b788e64eafed0089cd438fe8fc92d359b6969e92c27c96f5bd99ea6283b68aabcb6abb86868ce13bf4eab896cc68b6a7fad4f24e9288bc8ef65cf6ec8dd4f76995a9b6d7b172828889a5f34798f59f90d53a8db5828ef637e2a3; JSESSIONID-WYYY=KIiH0AOXkcY9cHpUyOMcMPCyczdASXX2ffhBDSnTdYKm3KOdf%2Fuy08bF8gRYlMrjerONeK2XGENuQepMpMbN5IxZx37qmno0UmPgbnCm900xrJSSIMiO%5CQPofm9pGfkWuyFYjZso%2BQGCljAQCFXNJW3852%5CpEMK7DkHiVlswC3ZXJPja%3A1663050301718
origin: https://music.163.com
pragma: no-cache
referer: https://music.163.com/song?id=1970554437
sec-ch-ua: " Not A;Brand";v="99", "Chromium";v="98", "Google Chrome";v="98"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
sec-fetch-dest: empty
sec-fetch-mode: cors
sec-fetch-site: same-origin
user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36
'''
i2x={
    'csrf_token': "",
   'cursor': "-1",
    'offset': "0",
    'orderType': "1",
    'pageNo': "1",
    'pageSize': "20",
    'rid': "R_SO_4_1970554437",
    'threadId': "R_SO_4_1970554437",
}

url='https://music.163.com/weapi/comment/resource/comments/get?csrf_token='

def get_data_js():
    file = './params.js'
    node = execjs.get()
    ctx = node.compile(open(file, encoding='utf-8').read())
    js = f'main()'
    result = ctx.eval(js)
    return result
def get_headers(len):
    headers = {
        'authority': 'music.163.com',
        'method': 'POST',
        'path': '/weapi/comment/resource/comments/get?csrf_token=',
        'scheme': 'https',
        'accept': '*/*',
        'accept-encoding': 'gzip, deflate, br',
        'accept-language': 'zh-CN,zh;q=0.9',
        'cache-control': 'no-cache',
        'content-length': f'{len}',
        'content-type': 'application/x-www-form-urlencoded',
        'cookie': '_iuqxldmzr_=32; _ntes_nnid=7183e25fc95d70a53f149d142d1f4997,1662545146457; _ntes_nuid=7183e25fc95d70a53f149d142d1f4997; NMTID=00OEtYQ9zrDEy0REElhoGwUom14JGAAAAGDF2mx5A; WNMCID=qgbypi.1662545146848.01.0; WEVNSM=1.0.0; WM_TID=PuDXdRurU8pEVFUBARPAC0ZrONPbcgp3; WM_NI=w9qX0h%2FZLHiwqk%2B8uW%2B2fJlBGsXIXUm0%2Fr%2B6JjclQ8zWyggFm30hNIM84qPSvJRqCpV%2BsxkjAjFJHbyZJkb106xuIlvy63sFRXtmwP64C3tcWwz62zBXGDpMT%2BQoV2OkNmw%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6eeb4d260a9ec988ff24af78e8eb2d55a829a9aacc460f4b1a2b4b1709baeb6d1f72af0fea7c3b92ab5e7bfb9b364ed8684a9ec7db3b5e5d9f85de9eeaaabef4a85ad98aadb5faff0e193aa3e8ab6ad8dd434ba89b788e64eafed0089cd438fe8fc92d359b6969e92c27c96f5bd99ea6283b68aabcb6abb86868ce13bf4eab896cc68b6a7fad4f24e9288bc8ef65cf6ec8dd4f76995a9b6d7b172828889a5f34798f59f90d53a8db5828ef637e2a3; JSESSIONID-WYYY=sMHeDF4pzeEjhXFx2UT5MubEyqOxg52DSQBHP85w7piizFNSi%2FDhx%2BDbYYOMq8hvWg8rwWspotTdBAqN5RhE2MVpYycnVGvMUQ0E2SByr8aXqIR77%5Ct2bFUnehGVTItvyQvntijWm591gFsRBTVtg2%5CMl6rXaN2WszBB8pScSq9u7uXp%3A1663067853243',
        'origin': 'https://music.163.com',
        'pragma': 'no-cache',
        'referer': 'https://music.163.com/song?id=1970554437',
        'sec-ch-ua': '" Not A;Brand";v="99", "Chromium";v="98", "Google Chrome";v="98"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': 'Windows',
        'sec-fetch-dest': 'empty',
        'sec-fetch-mode': 'cors',
        'sec-fetch-site': 'same-origin',
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36'
    }
    return headers

if __name__ == '__main__':
    data=get_data_js()
    data_dict={
        'params':data['params'],
        'encSecKey':data['encSecKey']
    }
    data = urlencode(data)
    len_data = len(data)
    headers=get_headers(len_data)
    response=requests.post(url=url,data=data,headers=headers).text
    print(response)
