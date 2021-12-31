import numpy as np
import pandas as pd
import json
import tensorflow as tf

# 这里是用户画像和电影画像
user_profile = './profile/userProFile.json'
movie_profile = './profile/movieProFile.json'

# 这里是20%的ratings数据，取user_id和movie_id用于打分
ratings20 = './recallData/ratings20.csv'
ratings = pd.read_csv(ratings20).values

# 训练模型，用32_2的效果最好
h5_load = './LR_32_2.h5'
model = tf.keras.models.load_model(h5_load)

type_dic = {
    'Adventure': 0,
    'Animation': 1,
    'Action': 2,
    'Crime': 3,
    'Children': 4,
    'Comedy': 5,
    'Drama': 6,
    'Documentary': 7,
    'Fantasy': 8,
    'Horror': 9,
    'IMAX': 10,
    'Romance': 11,
    'Mystery': 12,
    'Thriller': 13,
    'Sci-Fi': 14,
    'War': 15,
    'Musical': 16,
    'Western': 17,
    'Film-Noir': 19,
    'Other': 20
}

with open(user_profile, 'r') as load_f:
    user_array = json.load(load_f)

with open(movie_profile, 'r') as load_f:
    movie_array = json.load(load_f)

user_dic = {}
for one in range(0, len(user_array)):
    temp = {
        'id': user_array[one]['basicInfo'][0],
        'count': user_array[one]['basicInfo'][1],
        'mean': user_array[one]['basicInfo'][2],
        'var': user_array[one]['basicInfo'][3],
        'type': user_array[one]['type'],
        'own': user_array[one]
    }

    user_dic[int(user_array[one]['basicInfo'][0])] = temp

movie_dic = {}
for one in range(0, len(movie_array)):
    movie_dic[int(movie_array[one]['movieId'])] = movie_array[one]

score = []
for i in range(len(ratings)):
    train_set = []
    if (i % 10000) == 0:
        print(i)
    id_1 = int(ratings[i][0])
    if id_1 in user_dic.keys():
        count_2 = int(user_dic[id_1]['count'])
        mean_3 = float(user_dic[id_1]['mean'])
        var_4 = float(user_dic[id_1]['var'])
        type_5_24 = user_dic[id_1]['type']
        for t in range(len(type_5_24)):
            if type_5_24[t] is None:
                type_5_24[t] = 0.
            else:
                type_5_24[t] = float(type_5_24[t])
    else:
        count_2 = 0
        mean_3 = 3
        var_4 = 0
        type_5_24 = [0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05,
                     0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05]
    m_id_25 = int(ratings[i][1])
    if m_id_25 in movie_dic.keys():
        temp_type = movie_dic[m_id_25]['genres']
    else:
        temp_type = '(no genres listed)'

    temp_array = temp_type.split('|')
    if temp_array[0] == '(no genres listed)':
        temp_array[0] = 'Other'
    type_26_28 = []
    if len(temp_array) <= 3:
        tl = 3 - len(temp_array)
        for one in range(0, tl):
            temp_array.append('Other')
    for one in range(0, 3):
        type_26_28.append(type_dic[temp_array[one]])

    train_set.append([id_1, count_2, mean_3, var_4,
                      type_5_24[0], type_5_24[1], type_5_24[2], type_5_24[3], type_5_24[4],
                      type_5_24[5], type_5_24[6], type_5_24[7], type_5_24[8], type_5_24[9],
                      type_5_24[10], type_5_24[11], type_5_24[12], type_5_24[13], type_5_24[14],
                      type_5_24[15], type_5_24[16], type_5_24[17], type_5_24[18], type_5_24[19],
                      m_id_25, type_26_28[0], type_26_28[1], type_26_28[2]
                      ])

    train_res = model.predict(train_set)
    score.append([id_1, m_id_25, train_res[0][0]])

output = pd.DataFrame(score)

# 输出.csv文件
output.to_csv('ratings_20_test.csv', header=False, index=False)