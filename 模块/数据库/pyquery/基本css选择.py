from pyquery import PyQuery as pq
html='''
    <div id='container'>
    <ul>
    <li class='item-0'>first item</li>
    <li class=''item-1><a href='link2.html'> second item</a></li>
    <li class='item-0 active'><a href='link3.html'><span class='blod'>third item</a></li>
    <li class='item-1 active'><a href='link4.html'>fourth item</a></li>
    <li class='item-0'><a href='link5.html'>fifth item</li>
    </ul>
    </div>
'''
doc=pq(html)
print(doc('#container li'))
for item in doc('#container li').items():
    print(item.text())