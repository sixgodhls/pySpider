from seleniumwire import webdriver
import time
options = webdriver.ChromeOptions()
options.add_argument("--proxy-server=https://43.157.119.61:19019")
options.add_experimental_option("detach", True)
br = webdriver.Chrome(options=options)
br.execute_cdp_cmd('Page.addScriptToEvaluateOnNewDocument',{
    'source':'Object.defineProperty(navigator,"webdriver",{get:()=>undefined})'
})
# br=webdriver.Chrome()
br.get("https://chat.openai.com/auth/login")
for i in range(10):
    time.sleep(60)