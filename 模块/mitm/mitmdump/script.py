from mitmproxy import ctx
import json
# def request(flow):
#     flow.request.headers['User-Agent']='MitmProxy'
#     # print(flow.request.headers)
#     ctx.log.info(str(flow.request.headers))
#     ctx.log.warn(str(flow.request.headers))
#     ctx.log.error(str(flow.request.headers))
def response(flow):
    url='https://app5.scrape.center/api/movie/'
    if flow.request.url.startswith(url):
        text=flow.response.text
        if not text:
            return
        data=json.loads(text)
        items=data.get('results')
        for item in items:
            ctx.log.info(str(item))