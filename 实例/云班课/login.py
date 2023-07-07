import time

from selenium.webdriver import Chrome, ActionChains
from selenium.webdriver.chrome.options import Options

from selenium.webdriver.support.select import Select
from selenium.webdriver.common.by import By
from time import sleep

class get_login:
    def __init__(self,web,usename,pwd):
        self.web=web
        self.usename=usename
        self.pwd=pwd

    def login(self):
        self.web.get('https://www.mosoteach.cn/web/index.php')

        #usename
        self.web.find_element(by=By.XPATH,value='//*[@id="account-name"]').send_keys(self.usename)

        # self.web.find_element(by=By.XPATH,value='//*[@id="account-name"]').send_keys('13064176862')

        #password
        self.web.find_element(by=By.XPATH,value='//*[@id="user-pwd"]').send_keys(self.pwd)
        # self.web.find_element(by=By.XPATH,value='//*[@id="user-pwd"]').send_keys('1732885686p.')

        #login
        self.web.find_element(by=By.XPATH, value='//*[@id="login-button-1"]').click()

    def js_get_click(self,xpath):
        js_xpath1 = '''
        function x(xpath) {
      var result = document.evaluate(xpath, document, null, XPathResult.ANY_TYPE, null);
      return result.iterateNext()}
        '''
        js_xpath2 = f''' var aa=x('{xpath}');aa.click() '''

        self.web.execute_script(js_xpath1 + js_xpath2)

    def choice(self,KC_name,par1=None,par2=None):
        self.web.find_element(by=By.XPATH,value=f'//span[@title="{KC_name}"]').click()
        try:
            self.web.find_element(by=By.XPATH,value=f'//span[@title="{par1}"]').click()
        except:
            pass
        try:
            self.js_get_click(f'//div[@data-row-status="IN_PRGRS"]//span[@title="{par2}"]')
            # print(f'//div[@style="display: none;"]//span[@title="{par2}"]')
            # self.web.find_element(by=By.XPATH, value=f'//div[@style="display: none;"]//span[@title="{par2}"]').click()

        except:
            pass
    def close(self):
        self.web.close()