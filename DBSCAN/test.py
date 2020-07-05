from Cluster import Cluster
from Point import Point
import numpy as np
import math
from sklearn import datasets
import pandas as pd

def removePoint(item, list):
    if item in list:
        unvisited_points.remove(item)
        
def removeNeighbour(item):
    if item in neighbours:
        neighbours.remove(item)

def distance(first, second):
    return math.sqrt(
        ((first.x() - second.x()) ** 2) +
        ((first.y() - second.y()) ** 2) +
        ((first.z() - second.z()) ** 2) +
        ((first.o() - second.o()) ** 2)
    )
 
"""
下载iris数据集
"""
iris=datasets.load_iris()   #下载数据集
df = pd.DataFrame(iris.data, columns=iris.feature_names)
df['label'] = iris.target
df.columns = ['sepal length', 'sepal width', 'petal length', 'petal width', 'label']
dataset = np.array(df.iloc[:150, :])    #保存为二维数组形式
print(dataset)

epsilon = 0.4 #定义邻居点的最大半径
minPoints = 4 #定义邻居密集点最小个数的阈值

clusters = []#定义cluster数组 储存分出来的出来的cluster

"""创建数据点集"""
points = []#数据点集合
unvisited_points = []#没有被访问过的数据点集
noise_points = []#噪声点集

"""
把数据集中的数据点转化成Point object
"""
for data in dataset:
    p = Point(data[0], data[1], data[2], data[3],data[4])#前四维属性值在四维坐标上构成一个object
    points.append(p)#将这个对象放入数据点集中
    unvisited_points.append(p)#初始，将这个对象放入未被访问过的数据点集中
round=0#while循环进行了多少轮
while len(unvisited_points) > 0:#只要还有没被访问过的obect（终止条件）
    neighbours = []#建立邻居数组
    neighboursTemp = []#临时邻居数组
    """随机选择起始点"""
    start_point_index = np.random.randint(0, len(unvisited_points))#从没有被访问过的objects中随机选（下标）
    start_point = unvisited_points[start_point_index]#选中该object

    start_point.setVisited(True)#标记这个object为访问过
    if start_point in unvisited_points:#从未被访问的集合中删除
        unvisited_points.remove(start_point)

    """找到start point的邻居"""
    for point in points:#points是所有的点
        if distance(point, start_point) <= epsilon:
            neighbours.append(point)

    removeNeighbour(start_point)#因为遍历所有点时，把start point自己也加了进去

    """看邻居的个数是否满足minPoints"""
    if len(neighbours) < minPoints:
        noise_points.append(start_point)#不满足就添加到噪声集合中标记为噪声点
        start_point.setNoise(True)#在对象中标记为噪声点
    else:
        #满足条件 该点是一个core object

        """创建一个新的cluster"""
        cluster = Cluster()#下面是构建一个cluster的流程
                      
        clusters.append(cluster)#把这个cluster加到cluster数组中
        cluster.addPoint(start_point)#将当前符合条件的点加入
        start_point.setType(clusters.index(cluster))      
        """从选中的start point开始，检查所有的邻居（集合N）"""
        #之前创建了待处理集合neighbours，寻找邻居是为了扩大找的范围 
        while len(neighbours) > 0:
            """Pop出一个邻居"""
            selected_neighbour = neighbours.pop()
            #遍历每一个邻居
            if selected_neighbour.getVisited()==False:#如果他还没有被访问过
                removePoint(selected_neighbour, unvisited_points)#从没有访问过的节点中删除
                selected_neighbour.setVisited(True)#标记为已经访问过
                """找到这个邻居的所有符合条件的邻居"""
                for point_1 in points:
                    if distance(point_1, selected_neighbour) <= epsilon:
                        #neighbours.append(point_1)
                        neighboursTemp.append(point_1)#保存到临时数组，下面还要看它们数量满不满足minpoints
                if len(neighboursTemp) >= minPoints:#如果满足minpoints 就把这些点放到N集合中
                    for point_2 in neighboursTemp:
                        neighbours.append(point_2)
    
            """把选中的邻居加到cluster中"""            
            if selected_neighbour.getType()==-1:#如果还没有被添加到某一个cluster中
                cluster.addPoint(selected_neighbour)#就把它加到当前的这个cluster里
                selected_neighbour.setType(clusters.index(cluster))#标记类别编号

    round=round+1
    
print("Epsilon: {}" . format(epsilon))
print("MinPoints: {}" . format(minPoints), end='\n\n')

print("Cluster count: {}" . format(len(clusters)))

cluster_count = 0

rates = []

"""Plot all data on a page"""
for cluster in clusters:
    cluster_count += 1
    print("Cluster {}th: {}" . format(cluster_count, len(cluster.points())))
    for cluster.point in cluster.points():
        print(cluster.point.getLabel())
    #rates.append(len(cluster.points()))
    #print(rates)

print("Noise count: {}" . format(len(noise_points)), end='\n\n')
for noise_point in noise_points:
    print(noise_point.getLabel())

