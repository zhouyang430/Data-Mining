import numpy as np
import pandas as pd
from math import sqrt
from math import exp 
from math import pi
from sklearn import datasets
#from sklearn.cross_validation import train_test_split #用于分割数据集
from sklearn.model_selection import KFold      #K折交叉检验


iris=datasets.load_iris()   #下载数据集
df = pd.DataFrame(iris.data, columns=iris.feature_names)
df['label'] = iris.target
df.columns = ['sepal length', 'sepal width', 'petal length', 'petal width', 'label']
data = np.array(df.iloc[:150, :])    #保存为二维数组形式
print(data)
def accuracy_metric(actual, predicted):
    """
    计算准确率
    @actual: 真实值
    @predicted: 预测值
    """
    correct = 0
    for i in range(len(actual)):
        if actual[i] == predicted[i]:#如果真实值和预测的一样
            correct += 1
    return float(correct / float(len(actual)) * 100.0)

def evaluate_algorithm(dataset, algorithm, n_folds):
    """
    评估使用的分类算法（交叉检验）
    @dataset: 数据
    @algorithm: 使用的算法
    @n_folds: 选择要划分的折数
    """
    #这里 按照K折检验法分割了训练集和测试集'''
    scores = list()
    kf = KFold(n_splits=n_folds)
    for train, test in kf.split(dataset):
        predicted = algorithm(dataset[train], dataset[test])
     #   print(dataset[train], dataset[test])
    #    print(predicted)
        actual = [row[-1] for row in dataset[test]]
   #     print(actual)
        accuracy = accuracy_metric(actual, predicted)#当前测试集上的准确率
      #  print(accuracy)
        scores.append(accuracy)
      #  break
    return scores#算出每个折上的准确率

def separate_by_class(dataset):
    """
    将数据集根据类别进行分类
    @dataset: 整个iris数据
    """
    separated = dict()  #创建空字典
    dataset = dataset.reshape((-1,5)) #数据的行列数目，改为任意行，5列
    for i in range(len(dataset)): #逐行遍历数据    
        vector = dataset[i]       #当前行数据
        class_value = vector[-1]  #取出本行最后一个数据 即类别数据     
        if (class_value not in separated):#其实最后只会放三个类别元素进去
            separated[class_value] = list() #字典元素索引是类别，对应的类别下创建list
        separated[class_value].append(vector)#把list加到对应类别中
    return separated              #list还没用

def summarize_dataset(dataset):
    """
    计算每一个特征的统计性指标，这是为了后续计算条件概率
    @dataset: 整个iris数据
    """
    summaries = [(np.mean(column), np.std(column), len(column)) for column in zip(*dataset)]
    del(summaries[-1])#这里计算每个特征的均值，标准差，直接调用numpy函数
    #删除指定值 summaries的最后一个元素len
    return summaries#返回均值和标准差指标

def summarize_by_class(dataset):
    """
    将数据集根据类别分割，然后！！-->分别计算统计量
    @dataset: 整个iris数据
    """
    separated = separate_by_class(dataset)
    summaries = dict()#创建新的字典
    for class_value, rows in separated.items(): #循环时分别处理这两个变量
        summaries[class_value] = summarize_dataset(rows)#分别计算这几个属性的统计量
    return summaries#返回均值和标准差指标（字典）

def calculate_probability(x, mean, stdev):
    """
    根据统计量计算某一个特征的正态分布概率分布函数
    @x: 特征数据
    @mean: 均值
    @stdev: 标准差
    """
    exponent = exp(-((x-mean)**2 / (2 * stdev**2 )))
    return (1 / (sqrt(2 * pi) * stdev)) * exponent

def calculate_class_probabilities(summaries, row):
    """
    根据后验概率来计算先验概率
    @summaries: 统计量
    @row: 一行数据
    """

    probabilities = dict()
    for class_value, class_summaries in summaries.items():
        probabilities[class_value] = 1
        for i in range(len(class_summaries)):
            mean, stdev,_ = class_summaries[i]
            #根据统计量计算某一个特征的正态分布概率分布函数---》算出当前数据特征取值下，属于某个类别的概率
            probabilities[class_value] *= calculate_probability(row[i], mean, stdev)
    return probabilities

def predict(summaries, row):
    """
    预测
    @summaries: 统计量
    @row: 一行数据
    """
    probabilities = calculate_class_probabilities(summaries, row)#属于某个类别的概率矩阵
    best_label, best_prob = None, -1
    for class_value, probability in probabilities.items():
        if best_label is None or probability > best_prob:#比较 找到最大可能性的类别
            best_prob = probability
            best_label = class_value
    return best_label#返回概率矩阵中概率最大的类别

def naive_bayes(train, test):  #传入训练集和测试集
    """
    朴素贝叶斯分类器
    @train: 训练集
    @test: 测试集
    """
    summarize = summarize_by_class(train)#字典，每个类别的统计属性
    predictions = list()#预测
    for row in test:#遍历 每行测试集的属性
        output = predict(summarize, row)
        predictions.append(output)
    return(predictions)
for n_folds in range(4,22):
    scores = evaluate_algorithm(data, naive_bayes, n_folds)
    print('%d折交叉验证下'% n_folds)
    print('朴素贝叶斯分类器在每个折上的准确率: %s '% scores)
    print('朴素贝叶斯算法的平均准确率: %.3f%%' % (sum(scores)/float(len(scores)))) 
    print('\n')
