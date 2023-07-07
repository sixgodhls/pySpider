import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
plt.rcParams['font.sans-serif'] = ['MicroSoft YaHei']
plt.rcParams['axes.unicode_minus'] = False
df = pd.read_csv('C:/Users/LENOVO/Desktop/399300.csv', encoding='gbk', index_col=None)
num=50
df.index = pd.to_datetime(df.日期)
df = df.sort_index()
df['yields']= np.log(df.收盘价/df.收盘价.shift(1))
df['volatility']=np.abs(df.yields)
yields_corr=[]
volatility_corr=[]
for i in range(num):
    corr1=df['yields'].dropna().corr(df['yields'].shift(i).dropna())
    yields_corr.append(corr1)
    corr1=df['volatility'].dropna().corr(df['volatility'].shift(i).dropna())
    volatility_corr.append(corr1)

plt.figure(0)
plt.plot(yields_corr,'.')
plt.xlabel('Time(t)')
plt.title("收益率自关联")
plt.figure(1)
plt.loglog(range(num),yields_corr,'.')
plt.xlabel('Time(t)')
plt.title("收益率自关联-对数坐标系")
plt.figure(2)
plt.plot(volatility_corr,'.')
plt.xlabel('Time(t)')
plt.title("波动率自关联")
plt.figure(3)
plt.loglog(range(num),volatility_corr,'.')
plt.xlabel('Time(t)')
plt.title("波动率自关联-对数坐标系")
plt.show()
