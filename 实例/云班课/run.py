import time
from login import get_login
from read_txt import read_fp
from selenium.webdriver import Chrome, ActionChains
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.select import Select
from selenium.webdriver.common.by import By
from time import sleep
from common import common_
def open_web():

    option = Options()
    option.add_argument('--disable-blink-features=AutomationControlled')

    web = Chrome(options=option)
    web.execute_cdp_cmd('Page.addScriptToEvaluateOnNewDocument', {
                'source': 'Object.defineProperty(navigator,"webdriver",{get:()=>undefined})'
            })
    web.implicitly_wait(5)

    return web

if __name__ == '__main__':
    usename=15867854410
    # usename=13064176862
    pwd='hhj.123321'
    # pwd='1732885686p.'
    web=open_web()
    login=get_login(web,usename,pwd)
    login.login()
    KC_list=['业务流程管理','大数据思维与决策','信息系统项目管理','数据挖掘']
    login.choice(KC_list[1],par1='第5章 大数据安全与伦理',par2='大数据安全（v35）')

    shuati=common_(web)
    txt=read_fp()
    dict_all=txt.read_txt(txt_path='./res/07.txt')

    shuati.begin()
    shuati.zuoti(dict_all)





