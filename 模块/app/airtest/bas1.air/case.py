from airtest.core.api import *
from poco.drivers.android.uiautomation import AndroidUiautomationPoco
from loguru import logger

poco = AndroidUiautomationPoco(
    use_airtest_input=True, screenshot_each_action=False)
width,height=poco.get_screen_size()
packageName='com.goldze.mvvmhabit'
total_num=100

def scrape_index():
    elements = poco(f'{packageName}:id/item')
    elements.wait_for_appearance()
    return elements

def scrape_detail(element):
    element.click()
    panel = poco(f'{packageName}:id/content')
    panel.wait_for_appearance(5)
    title = poco(f'{packageName}:id/title').attr('text')
    categories = poco(f'{packageName}:id/categories_value').attr('text')
    score = poco(f'{packageName}:id/score_value').attr('text')
    published_at = poco(f'{packageName}:id/published_at_value').attr('text')
    drama = poco(f'{packageName}:id/drama_value').attr('text')
    keyevent('BACK')
    return {
        'title': title,
        'categories': categories,
        'score': score,
        'published_at': published_at,
        'drama': drama
    }

def main():
    elements = scrape_index()
    for element in elements:
        logger.debug(element)
        panel=poco('android.support.v7.widget.RecyclerView')
        panel.wait_for_appearance()
        element_data = scrape_detail(element)
        logger.debug(f'scraped data {element_data}')


if __name__ == '__main__':
    init_device('Android')
    stop_app(packageName)
    start_app(packageName)
    main()


