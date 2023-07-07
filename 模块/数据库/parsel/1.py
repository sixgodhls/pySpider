from parsel import Selector
html='''
    <div>
    <ul>
    <li class='item-0'>first item</li>
    <li class=''item-1><a href='link2.html'> second item</a></li>
    <li class='item-0 active'><a href='link3.html'><span class='blod'>third item</a></li>
    <li class='item-1 active'><a href='link4.html'>fourth item</a></li>
    <li class='item-0'><a href='link5.html'>fifth item</li>
    </ul>
    </div>
'''
selector=Selector(text=html)
items=selector.css('.item-0')
print(items)
items1=selector.xpath('//li[@class="item-0"]')
print(items1)
print(items.get())
print(items.getall())
print(items.xpath('.//text()').get())
attr1=selector.css('.item-0 a::attr(href)').get()
print(attr1)
attr2=selector.xpath('//li[@class="item-0"]/a/@href').get()
print(attr2)
test=selector.xpath('//li[contains(text,fir)]').extract_first()
print(test)