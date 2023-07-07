from selenium import webdriver
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait
import logging
logging.basicConfig(level=logging.INFO,format='%(asctime)s-%(levelname)s:%(message)s')

index_url='https://spa2.scrape.center/page/{page}'
time_out=10
total_pages=10

options=webdriver.ChromeOptions()
options.add_argument('--headless')


web=webdriver.Chrome(options=options)
wait=WebDriverWait(web,time_out)
#跳转到相关页 url：网址对象 condition：判断条件 locator：条件
def scrape_page(url,condition,locator):
    logging.info('scraping%s',url)
    try:
        web.get(url)
        #相当于传进来的参数变成EC.visibility_of_all_elements_located(By.CSS_SELECTOR,'#index .item'))
        wait.until(condition(locator))
    except TimeoutException:
        logging.error('error while scraping %s',url,exc_info=True)


def scrape_index(page):
    url=index_url.format(page=page)
    scrape_page(url,condition=EC.visibility_of_all_elements_located,locator=(By.CSS_SELECTOR,'#index .item'))

from urllib.parse import urljoin

def parse_index():
    #css选择和xpath选择都可以
    # elements = web.find_elements(By.CSS_SELECTOR, '#index .item .name')
    # for href in elements:
    #     href = href.get_attribute('href')
    #     yield urljoin(index_url, href)
    elements=web.find_elements(By.XPATH,'//a[@class="name"]')
    for href in elements:
        href=href.get_attribute('href')
        #生成了一个可迭代对象 相比返回一个list对象可以减少内存空间的占用
        yield urljoin(index_url,href)


def scrape_detaile(url):
    scrape_page(url,condition=EC.visibility_of_all_elements_located,locator=(By.TAG_NAME,'h2'))

def parse_detail():
    url=web.current_url
    name=web.find_element(By.TAG_NAME,'h2').text
    categories=[elements.text for elements in web.find_elements(By.CSS_SELECTOR,'.categories button span')]
    cover=web.find_element(By.CSS_SELECTOR,'.cover').get_attribute('src')
    score=web.find_element(By.CLASS_NAME,'score').text
    drama=web.find_element(By.CSS_SELECTOR,'.drama p').text
    return {
        'url':url,
        'name':name,
        'categories':categories,
        'cober':cover,
        'score':score,
        'drama':drama
    }
from os import makedirs
from os.path import exists
import json
result_dir='results'
exists(result_dir) or makedirs(result_dir)
def save_data(data):
    name=data.get('name')
    data_path=f'{result_dir}/{name}.json'
    json.dump(data,open(data_path,'w',encoding='utf-8'),ensure_ascii=False,indent=2)
def main():
    try:
        for page in range(1,total_pages+1):
            #get相关页数的网址
            scrape_index(page)
            #将该页数的网址的源代码解析得到一个包含详情网址的可迭代对象
            detail_urls=parse_index()
            #迭代生成的可迭代对象
            for detail_url in list(detail_urls):
                logging.info('get detail url %s',detail_url)
                scrape_detaile(detail_url)
                detail_data=parse_detail()
                logging.info('tetail data %s',detail_data)
                save_data(detail_data)
    finally:
        web.close()




if __name__ == '__main__':
    main()