import csv
from math import *

import numpy as np

file = open("/Users/mac/Desktop/ml-25m/final.csv", 'r', encoding='UTF-8')
data = {}  # 存放每位用户评论的电影和评分
for line in file.readlines()[1:]:
    line = line.strip().split(',')
    # 如果字典中没有某位用户，则使用用户ID来创建这位用户
    if not line[0] in data.keys():
        data[line[0]] = {line[2]: line[1]}
    # 否则直接添加以该用户ID为key字典中
    else:
        data[line[0]][line[2]] = line[1]


def Euclidean(user1, user2):
    # 取出两位用户评论过的电影和评分
    user1_data = data[user1]
    user2_data = data[user2]
    distance = 0
    for key in user1_data.keys():
        if key in user2_data.keys():
            distance += pow(float(user1_data[key]) - float(user2_data[key]), 2)
    return 1 / (1 + sqrt(distance))


# 相似度
def user_simliar(userID):
    res = []
    for userid in data.keys():
        if not userid == userID:
            simliar = Euclidean(userID, userid)
            res.append((userid, simliar))
    res.sort(key=lambda val: val[1])
    return res[:3]


# 基于相似用户推荐电影
def recommend(user):
    top_sim_user = user_simliar(user)[0][0]
    items = data[top_sim_user]
    recommendations = []
    for item in items.keys():
        if item not in data[user].keys():
            recommendations.append((item, items[item]))
    recommendations.sort(key=lambda val: val[1], reverse=True)  # 按照评分排序
    return recommendations[:5]


f = open('./data/result/recall_1.csv', 'w', encoding='utf-8', newline='')
csv_writer = csv.writer(f)
csv_writer.writerow(["用户ID", "movie1", "movie2", "movie3", "movie4", "movie5"])

for i in range(162541):
    Recommendations = recommend(str(i + 1))
    print(i)
    print(Recommendations)
    csv_writer.writerow([i + 1,
                         list(np.array(Recommendations).T[0])[0],
                         list(np.array(Recommendations).T[0])[1],
                         list(np.array(Recommendations).T[0])[2],
                         list(np.array(Recommendations).T[0])[3],
                         list(np.array(Recommendations).T[0])[4]])

f.close()
