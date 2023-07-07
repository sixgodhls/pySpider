import pika
import requests
import pickle

queue_name='center'
max_priority=100
total=100
connection=pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channal=connection.channel()
channal.queue_declare(queue=queue_name,durable=True)

for i in range(1,total+1):
    url=f'http://ssr1.scrape.center/detail/{i}'
    request=requests.Request('get',url)
    channal.basic_publish(
        exchange='',
        routing_key=queue_name,
        properties=pika.BasicProperties(
            delivery_mode=2
        ),
        body=pickle.dumps(request)
    )
    print(f'put {url}')