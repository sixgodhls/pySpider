import requests
import json
import numpy as np
import matplotlib.pyplot as plt
from prophet import Prophet
from statsmodels.tsa.arima.model import ARIMA
import pandas as pd
import prophet

urlTem='https://q.stock.sohu.com/hisHq?code=cn_{}&start=20201014&end=20230210&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.19766130813654437&0.3826183247609136'

def getDataDict(code):

    url = urlTem.format(code)
    dataText = requests.get(url)
    dataText.encoding='gbk'
    dataText=dataText.text.split("historySearchHandler([")[1].split(")")[0][:-1]
    dataDict=eval(dataText)
    return dataDict

def getPriceGen(dict):
    list=dict["hq"]
    for i in list:
        yield i[2]


def ARIMAyuce(list):
    # 将数据转换为时间序列
    data = np.array(list)
    # 构建ARIMA模型
    model = ARIMA(data, order=(1, 1, 1))
    model_fit = model.fit()

    # 预测下一段时间的数据
    forecast = model_fit.forecast(steps=30)
    # 构造x轴数据
    x = np.arange(len(data) + len(forecast))

    # 可视化预测结果和原始数据
    plt.plot(x[:len(data)], data, label='Original Data')
    plt.plot(x[len(data):], forecast, label='Forecast')
    plt.legend()
    plt.show()
def Prophetyuce(list):
    # 创建数据框，将数据设置为时间序列
    df = pd.DataFrame(list, columns=['y'])
    df['ds'] = pd.date_range(start='2023-02-11', periods=len(list), freq='D')

    # 构建Prophet模型
    model = Prophet()
    model.fit(df)

    # 预测未来30天的数据
    future = model.make_future_dataframe(periods=30)
    forecast = model.predict(future)

    # 可视化预测结果
    model.plot(forecast)
    plt.show()
if __name__ == '__main__':
    code=600963
    dataDict=getDataDict(code)
    dataListGen=getPriceGen(dataDict)
    dataList=[]
    for i in dataListGen:
        dataList.append(float(i))
    ARIMAyuce(dataList)