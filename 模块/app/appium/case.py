from appium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from loguru import logger
from selenium.common.exceptions import NoSuchElementException
server='http://localhost:4723/wd/hub'
desired_capabilities={
  "platformName": "Android",
  "deviceName": "VOG_AL10",
  "appPackage": "com.goldze.mvvmhabit",
  "appActivity": ".ui.MainActivity",
  "noReset": True
}

PackageName=desired_capabilities['appPackage']
totalNum=20

driver=webdriver.Remote(server,desired_capabilities)
wait=WebDriverWait(driver,30)
window_size=driver.get_window_size()
width,height=window_size.get('width'),window_size.get('height')

def scrape_index():
    items=wait.until(EC.presence_of_all_elements_located((By.XPATH,f'//android.widget.LinearLayout[@resource-id="{PackageName}:id/item"]')))
    return items

def  scrape_detail(element):
    logger.debug(f'scraping {element}')
    element.click()
    wait.until(EC.presence_of_element_located((By.ID,f'{PackageName}:id/detail')))
    title=wait.until(EC.presence_of_element_located((By.ID,f'{PackageName}:id/title'))).get_attribute('text')
    categories=wait.until(EC.presence_of_element_located((By.ID,f'{PackageName}:id/categories_value'))).get_attribute('text')
    scrore=wait.until(EC.presence_of_element_located((By.ID,f'{PackageName}:id/score_value'))).get_attribute('text')
    driver.back()
    return {'title':title,
            'categories':categories,
            'score':scrore}

def scroll_up():
    driver.swipe(width*0.5,height*0.8,width*0.5,height*0.4,1000)

def get_element_title(element):
    try:
        element_title=element.find_element(by=By.ID,value=f'{PackageName}:id/tv_title').get_attribute('text')
        return element_title
    except NoSuchElementException:
        return None
scraped_titles=set()


def main():
    while len(scraped_titles)<totalNum:
        elements=scrape_index()
        for element in elements:
            element_title=get_element_title(element)
            scraped_titles.add(element_title)
            element_location=element.location
            element_y=element_location.get('y')
            if element_y/height>0.6:
                logger.debug('scroll up')
                scroll_up()
            element_data=scrape_detail(element)
            logger.debug(f'scraped data {element_data}')

if __name__ == '__main__':
    main()
