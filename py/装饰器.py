def zhuangshi(fun):

    def func(a):
        fun(a)#被装饰函数
        print('x')#添加的东西
    return func
#装饰器原理利用高阶函数和闭包函数 不改变api 在函数上添加一些东西

def ge(a):
    print(a)
ge=zhuangshi(ge)


ge(1)

# @zhuangshi
# def get_name(a):
#     print('a',a)
# get_name(1)