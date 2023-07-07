from prompt_toolkit.keys import Keys
from selenium.webdriver import Chrome, ActionChains
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.select import Select
from selenium.webdriver.common.by import By
import re
# option = Options()
# option.add_argument('--disable-blink-features=AutomationControlled')
# web = Chrome(options=option)
# web.execute_cdp_cmd('Page.addScriptToEvaluateOnNewDocument', {
#     'source': 'Object.defineProperty(navigator,"webdriver",{get:()=>undefined})'
# })
# web.implicitly_wait(5)
# dic={'one_chance':{'A':111}}
# web.get('https://www.baidu.com/s?wd=1&ie=utf-8&tn=15007414_2_dg')
# js_xpath1='''
# function x(xpath) {
#   var result = document.evaluate(xpath, document, null, XPathResult.ANY_TYPE, null);
#   return result.iterateNext()}
# '''
# xpath=''' '//*[@id="1"]/div/div/h3/a' '''
# js_xpath2=f'''var aa=x({xpath});aa.click()'''
# print(js_xpath1+js_xpath2)
# web.execute_script(js_xpath1+js_xpath2)
# xpath_1='//*[@id="1"]'
#
# xpath_tar=xpath_1+'/following-sibling::div[1]//a'
# def js_get_click(xpath):
#     js_xpath1='''
#     function x(xpath) {
#   var result = document.evaluate(xpath, document, null, XPathResult.ANY_TYPE, null);
#   return result.iterateNext()}
#     '''
#     js_xpath2=f''' var aa=x('{xpath}');aa.click() '''
#     print(js_xpath1+js_xpath2)
#     web.execute_script(js_xpath1 + js_xpath2)
#
# js_get_click('//*[@id="1"]/div/div/h3/a')

#.Enter tar=web.find_element(by=By.XPATH,value='//*[@id="9"]')
# web.execute_script('arguments[0].scrollIntoView();',tar)
# tar.click()
a='{}'
f=''
c=''
e='ABCDEFGH'
g='''
A. 
流程的客户
B. 
流程目的
C. 
业务边界
D. 
业务活动
E. 
流程角色
F. 
关键活动
G. 
流程KPI'''
for i in e:
    # c+=a
    # '{a}(?P<{aa}_ans>.*?)'
    c+=a.format(f'{i+". "}(?P<{i}_ans>.*?)')
