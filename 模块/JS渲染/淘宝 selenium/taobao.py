
from selenium.webdriver import Chrome
from selenium.webdriver import ChromeOptions
option=ChromeOptions()
option.add_experimental_option('excludeSwitches',['enable-automation'])
option.add_experimental_option('useAutomationExtension',False)

web=Chrome(options=option)
#cjnb可以规避检测
web.execute_cdp_cmd('Page.addScriptToEvaluateOnNewDocument',{
    'source':'Object.defineProperty(navigator,"webdriver",{get:()=>undefined})'
})

web.get('https://login.taobao.com')
web.find_element_by_xpath('//*[@id="fm-login-id"]').send_keys('15867854410')
web.find_element_by_xpath('//*[@id="fm-login-password"]').send_keys('hhj123321')
web.find_element_by_xpath('//*[@id="login-form"]/div[4]/button').click()
