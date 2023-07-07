from selenium.webdriver import Chrome, ActionChains
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.select import Select
from selenium.webdriver.common.by import By
import selenium.webdriver.support.ui as ui
import selenium.webdriver.support.expected_conditions as EC
import time
class common_(object):
    def __init__(self,web):
        self.web=web

    def begin(self):
        try:
            self.web.find_element(by=By.XPATH, value='//*[@id="app"]/div[1]/div[2]/div/div/div/button/span').click()
        except:
            self.web.find_element(by=By.XPATH,
                             value='//*[@id="app"]/div/div[2]/div[2]/div[2]/div[1]/a/span/button/span').click()
            self.web.find_element(by=By.XPATH, value='//*[@id="app"]/div[1]/div[2]/div/div/div/button/span').click()

    def js_get_click(self,xpath):
        js_xpath1 = '''
        function x(xpath) {
      var result = document.evaluate(xpath, document, null, XPathResult.ANY_TYPE, null);
      return result.iterateNext()}
        '''
        js_xpath2 = f''' var aa=x('{xpath}');aa.click() '''


        self.web.execute_script(js_xpath1 + js_xpath2)



    def zuoti(self,dic):
        for k, v in dic.items():
            try:
                xpath_ques = '//div[text()="' + k + '"]'
                if type(v) == dict:
                    for v_v in v.values():
                        xpath_ans = xpath_ques + '/../div[3]//span[text()="' + v_v + '"]'
                        ui.WebDriverWait(self.web,2).until(EC.visibility_of_element_located((By.XPATH,xpath_ans)))
                        self.js_get_click(xpath_ans)
                        time.sleep(0.5)
                else:
                    xpath_ans = xpath_ques + '/../div[3]/label//span[text()="' + v + '"]'
                    ui.WebDriverWait(self.web, 2).until(EC.visibility_of_element_located((By.XPATH, xpath_ans)))
                    self.js_get_click(xpath_ans)
                    time.sleep(0.5)

            except:
                print('没找到'+':'+k)

    def test(self,dic):
        for k, v in dic.items():
            try:
                xpath_ques = '//div[text()="' + k + '"]'
                if type(v) == dict:
                    for v_v in v.values():
                        xpath_ans = xpath_ques + '/../div[3]//span[text()="' + v_v + '"]'
                        print(v_v)
                        # self.js_get_click(xpath_ans)
                else:
                    xpath_ans = xpath_ques + '/../div[3]/label//span[text()="' + v + '"]'
                    # self.js_get_click(xpath_ans)

            except:
                print('没找到'+':'+k)