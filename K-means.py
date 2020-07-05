from random import sample
import numpy as np
#from sklearn.cluster import KMeans
#from sklearn import datasets
from sklearn.datasets import load_iris
#导入鸢尾花数据集
#以二维数据为例 假设k=2，X为鸢尾花数据集前两维
iris = load_iris()
X = iris.data[:,0:4] ##表示我们取特征空间中的四个维度（所有的属性值） X类型是np.array
#print(len(X))

#从X中随机选择k个样本作为初始“簇中心”向量： μ(1),μ(2),...,,μ(k)
#随机获得两个数据
n = 3 #表示n个聚类
print("随机选择三个向量作为聚类中心")
u = sample(X.tolist(),n) #随机选择3个X中的向量作为聚类中心
iterTimes = 0 #记录迭代次数
#u_before = u
flag = 0
flag2=0
while flag2<3:
#while flag2!=3:#循环条件：当聚类中心不再改变时停止循环
    #第一次根据随机划分的三个聚类中心分配聚类
    #以后每算出一个mean point 都根据它再算距离，重新分配聚类
    c = []
    
   # print(u_before,u)
    for objectPoint in range(len(X)):#遍历所有的object
        min = 1000
        index = 0 
        for cluster in range(n):#对于每个object，算出它到三个聚类中心的距离，
            vec1 = X[objectPoint]#当前的这个object
            vec2 = u[cluster]#当前的第j个聚类中心
            dist = np.sqrt(np.sum(np.square(vec1 - vec2)))#算出他们之间的距离
            if dist<min:
                min = dist
                index =cluster
        c.append(index)#离哪个聚类中心最近，就分给哪一类

    
    #对每个cluster，为了重新划分cluster计算均值点
    for cluster in range(n):
        sum = np.zeros(4)  # 初始化4维向量求和
        count = 0  # 统计不同类别中object的个数
        for objectPoint in range(len(X)):#对于每一个object
            if c[objectPoint]==cluster:#如果这个object是属于当前类别的
                sum = sum+X[objectPoint]#求和
                count = count+1#计数
                
        average = sum/count#重新计算mean point
        #判断每个聚类的质心是否再发生变化
        for k in range(len(average)):
            
            if average[k]==u[cluster][k]:
                flag=flag+1
                k=k+1
        if flag==4:
            flag2=flag2+1
        else:
            u[cluster]= average                
    print("第",iterTimes,"次划分的聚类中心向量是",u)#产生了新的均值点向量
    #设置迭代次数
    iterTimes = iterTimes + 1

print("最终的划分结果是***********************")    
print(np.array(c))#打印分类结果

