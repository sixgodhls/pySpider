from selenium import webdriver
from pyquery import PyQuery as pq
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait
import re

def parse_name(doc):
    has_whole=doc('.whole')
    if has_whole:
        return doc.text()
    else:
        chars=doc('.char')
        items=[]
        #遍历
        for char in chars.items():
            items.append({
                'text':char.text().strip(),
                'left':int(re.search('(\d+)px',char.attr('style')).group(1))
            })
        items=sorted(items,key=lambda x:x['left'],reverse=False)
        return ''.join([item.get('text') for item in items])
browser=webdriver.Chrome()
browser.get('https://antispider3.scrape.center/')
WebDriverWait(browser,10).until(EC.presence_of_all_elements_located((By.CSS_SELECTOR,'.item')))
html=browser.page_source
doc=pq(html)
names=doc('.item .name')
for name in names.items():
    name_item=parse_name(name)
    print(name_item)
browser.close()