import pika
import pickle
import requests

queue_name='center'
max_priority=100
connecting=pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channal=connecting.channel()
session=requests.Session()

def scrape(request):
    try:
        resp=session.send(request.prepare())
        print(f'get{resp.url}')
    except:
        print('error')

while True:
    method,header,body=channal.basic_get(queue=queue_name,auto_ack=True)
    if body:
        requests=pickle.loads(body)
        print(f'get{requests}]')
        print(method)
        scrape(requests)