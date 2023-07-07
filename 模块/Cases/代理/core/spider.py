from requests import Session
from core.db import RedisQueue
import requests
from core.config import *
from core.request import MovieRequest
from pyquery import PyQuery as pq
from urllib.parse import urljoin
from loguru import logger
import re
from requests.exceptions import RequestException

#全局变量
BASE_URL = 'https://antispider5.scrape.center/'
HEADERS = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36'
    }

class Spider():
    #首先实例化一个session对象和队列
    session=Session()
    queue=RedisQueue()

    #拿代理IP
    def get_proxy(self):
        #开启代理池后从自己搭建的网站拿一个IP
        response=requests.get(PROXY_POOL_URL)
        if response.status_code==200:
            logger.debug(f'get proxy {response.text}')
            return response.text
    #从首页开始
    def start(self):
        #让所有session带有请求头
        self.session.headers.update(HEADERS)
        start_url=BASE_URL
        #请求首页得到一个对象
        request=MovieRequest(url=start_url,callback=self.parse_index)
        self.queue.add(request)
    #解析响应
    def parse_index(self,response):
        #pq实例化一个文本对象
        doc=pq(response.text)
        #如果响应是详情页
        items=doc('.item .name').items()
        for item in items:
            detail_url=urljoin(BASE_URL,item.attr('href'))
            #回调参数用解析详情页的方法
            resquest=MovieRequest(url=detail_url,callback=self.parse_detail)
            yield resquest
        #如果响应是下一页
        next_href=doc('.next').attr('href')
        if next_href:
            next_url=urljoin(BASE_URL,next_href)
            #继续调用自己这个参数
            resquest=MovieRequest(url=next_url,callback=self.parse_index)
            yield resquest


    #解析详情页的方法
    def parse_detail(self,response):
        doc = pq(response.text)
        cover = doc('img.cover').attr('src')
        name = doc('a > h2').text()
        categories = [item.text()
                      for item in doc('.categories button span').items()]
        published_at = doc('.info:contains(上映)').text()
        published_at = re.search('(\d{4}-\d{2}-\d{2})', published_at).group(1) \
            if published_at and re.search('\d{4}-\d{2}-\d{2}', published_at) else None
        drama = doc('.drama p').text()
        score = doc('p.score').text()
        score = float(score) if score else None
        yield {
            'cover': cover,
            'name': name,
            'categories': categories,
            'published_at': published_at,
            'drama': drama,
            'score': score
        }


    #发送请求
    def request(self,request):

        try:
            proxy=self.get_proxy()
            logger.debug(f'get proxy {proxy}')
            proxies={
                'http':'http://'+proxy,
                'https':'https://'+proxy
            }if proxy else None
            #对象发送准备过的requset进行请求
            return self.session.send(request.prepare(),
                                     timeout=TIMEOUT,
                                     proxies=proxies
                                     )
        except RequestException:
            logger.exception(f'requesting {request.url} failed')
    #IP有问题模块
    def error(self, request):
        """
        error handling
        :param request: request
        :return:
        """
        request.fail_time = request.fail_time + 1
        logger.debug(
            f'request of {request.url} failed {request.fail_time} times')
        if request.fail_time < MAX_FAILED_TIME:
            self.queue.add(request)
    #调度模块
    def schedule(self):
        #首先判断队列空了没
        while not self.queue.empty():
            #拿到队列中的一个需要请求的请求对象
            request=self.queue.pop()
            #拿到这个对象的回调方法
            callback=request.callback
            logger.debug(f'executing request {request.url}')
            #用自定义的request方法得到一个响应对象
            response=self.request(request)
            logger.debug(f'response status {response} of {request.url}')
            if not response or not response.status_code in VALID_STATUSES:
                self.error(request)
                continue
            #list生成器生成的可迭代对象
            results=list(callback(response))
            if not results:
                self.error(request)
                continue
            for result in results:
                #如果是需要请求的请求则放到队列里
                if isinstance(result,MovieRequest):
                    logger.debug(f'generated new request {result.url}')
                    self.queue.add(result)
                #如果是详情页的响应对象则结束
                if isinstance(result,dict):
                    logger.debug(f'scraped new data {result}')
    #设置一个入口
    def run(self):
        self.start()
        self.schedule()
#不知道为什么不能运行 一定要在上级文件夹同目录下调用Spider才能运行 下面这段代码有没有没差
# if __name__ == '__main__':
#     spider=Spider()
#     spider.run()

