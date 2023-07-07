import pymysql
id='20120006'
name='ccc'
age='20'
db=pymysql.connect(host='localhost',user='root',password='',port=3306,db='spiders')
cursor=db.cursor()
sql=f"INSERT INTO students(id,name,age) values({id},'{name}',{age})"
print(sql)
try:
    cursor.execute(sql)
    db.commit()
except:
    db.rollback()
sql='select * from students'
cursor.execute(sql)
res=cursor.fetchall()
print(res)
db.close()