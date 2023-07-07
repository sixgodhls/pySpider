from requests import Request

from core.config import *
#继承Request类对象 添加回调参数 失败次数 超时时间 三个参数
class MovieRequest(Request):
    def __init__(self,url,callback,methoud='GET',headers=None,fail_time=0,timeout=TIMEOUT):
        Request.__init__(self,methoud,url,headers)
        self.callback=callback
        self.fail_time=fail_time
        self.timeout=timeout
