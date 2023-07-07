import pymongo
client=pymongo.MongoClient(host='localhost',port=27017)
db=client['test']
collection=db['students']
student={
    'id':111,
    'name':666
}
student1={
    'id':111,
    'name':667
}
# insert
# result=collection.insert_one(student)
result=collection.insert_many([student,student1])
print(result)
#find
result_find=collection.find_one({'name':666})
print(result_find)