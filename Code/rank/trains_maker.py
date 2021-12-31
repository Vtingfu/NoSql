import numpy as np
import pandas as pd
import json
import tensorflow as tf

recall_LFM_1 = './recallData/recall_LFM_1.csv'
recall_popular = './recallData/recall_popular.csv'
recall_random = './recallData/recall_random.csv'

user_profile = './profile/userProFile.json'
movie_profile = './profile/movieProFile.json'
ratings80 = './profile/ratings80.csv'

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

recall_1 = pd.read_csv(recall_LFM_1).values
recall_2 = pd.read_csv(recall_popular).values
recall_3 = pd.read_csv(recall_random).values

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

recall_user = []
for i in range(len(recall_1)):  # len(recall_1)
    recall_user.append([
        recall_1[i][0], recall_1[i][1], recall_1[i][2], recall_1[i][3], recall_1[i][4], recall_1[i][5],
        recall_2[i][0], recall_2[i][1], recall_2[i][2], recall_2[i][3], recall_2[i][4],
        recall_3[i][0], recall_3[i][1], recall_3[i][2], recall_3[i][3], recall_3[i][4],
    ])

score = []
for i in range(10000):   # len(recall_user)
    train_set = []
    for j in range(1, 16):
        id_1 = int(recall_user[i][0])
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
        m_id_25 = int(recall_user[i][j])
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
    for t in range(15):
        score.append([recall_user[i][0], recall_user[i][t + 1], train_res[t]])

rank = sorted(score, key=lambda x: (x[0], x[2]))
result = []
temp = []
for one in range(len(rank)):
    temp.append(rank[one][1])
    if (one + 1) % 15 == 0:
        result.append([rank[one][0], temp[14], temp[13], temp[12], temp[11], temp[10]])
        temp = []

output = pd.DataFrame(result)
output.to_csv('ranking_1000.csv', header=False, index=False)
