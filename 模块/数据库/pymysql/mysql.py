import pymysql
db=pymysql.connect(host='127.0.0.1',user='root',password='',port=3306)
cursor=db.cursor()
cursor.execute('select version()')
data=cursor.fetchone()
print(data)
cursor.execute('create database if not exists spiders default character set utf8mb4')
db.close()
