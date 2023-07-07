from elasticsearch import Elasticsearch
es=Elasticsearch()
res=es.indices.create(index='test',ignore=400)
print(res)