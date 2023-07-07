from pyquery import PyQuery as pq
doc=pq(url='https://cuiqingcai.com')
# 可以用本地文档传递
# doc=pq(filename='./xxx')
print(doc('title'))