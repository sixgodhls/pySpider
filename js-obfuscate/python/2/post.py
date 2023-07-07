import requests

data={
    'name': '凯文-杜兰特',
    'image': 'durant.png',
    'birthday': '1988-09-29',
    'height': '208cm',
    'weight': '108.9KG'
  }

url='http://localhost:3000'
response=requests.post(url,json=data)
print(response.text)