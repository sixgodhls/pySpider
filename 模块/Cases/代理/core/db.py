from redis import StrictRedis
from core.config import *
from core.request import MovieRequest
from pickle import loads,dumps

class RedisQueue():
    #初始化连接
    def __init__(self):
        self.db=StrictRedis(host=REDIS_HOST,port=REDIS_PORT,password=REDIS_PASSWORD)

    def add(self,request):
        #如果传进来的对象是MovieRequest类型则将序列化后的请求对象传进队列里
        if isinstance(request,MovieRequest):
            return self.db.rpush(REDIS_KEY,dumps(request))
        return False

    def pop(self):
        #判断队列长度 如果不是空的则pop掉一个 返回一个反序列化的MovieRequest对象
        if self.db.llen(REDIS_KEY):
            return loads(self.db.lpop(REDIS_KEY))
        return False


    def clear(self):
        self.db.delete(REDIS_KEY)

    def empty(self):
        return self.db.llen(REDIS_KEY)==0




# from redis import StrictRedis
# from core.config import *
# from pickle import dumps, loads
# from core.request import MovieRequest
#
# class RedisQueue():
#     def __init__(self):
#         """
#         init redis connection
#         """
#         self.db = StrictRedis(
#             host=REDIS_HOST, port=REDIS_PORT, password=REDIS_PASSWORD)
#
#     def add(self, request):
#         """
#         add request to queue
#         :param request: request
#         :param fail_time: fail times
#         :return: result
#         """
#         if isinstance(request, MovieRequest):
#             return self.db.rpush(REDIS_KEY, dumps(request))
#         return False
#
#     def pop(self):
#         """
#         get next request
#         :return: Request or None
#         """
#         if self.db.llen(REDIS_KEY):
#             return loads(self.db.lpop(REDIS_KEY))
#         return False
#
#     def clear(self):
#         self.db.delete(REDIS_KEY)
#
#     def empty(self):
#         return self.db.llen(REDIS_KEY) == 0