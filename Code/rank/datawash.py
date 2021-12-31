import numpy as np
import pandas as pd
import json

user_profile = './profile/userProFile.json'
movie_profile = './profile/movieProFile.json'
ratings80 = './profile/ratings80.csv'

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

rating_data = pd.read_csv(ratings80, encoding='utf-8')
rating_dic = {}

train_data_x = []
temp_data = []
train_data_y = []
for one in range(0, len(rating_data)):
    tempList = rating_data.iloc[one].values[0:]
    if one % 1000 == 0:
        print(one)
    rating_dic[one] = tempList
    id_1 = int(tempList[0])
    count_2 = int(user_dic[id_1]['count'])
    mean_3 = float(user_dic[id_1]['mean'])
    var_4 = float(user_dic[id_1]['var'])
    type_5_24 = user_dic[id_1]['type']
    for i in range(len(type_5_24)):
        if type_5_24[i] is None:
            type_5_24[i] = 0.
        else:
            type_5_24[i] = float(type_5_24[i])
    m_id_25 = int(tempList[1])
    temp_type = movie_dic[m_id_25]['genres']
    temp_array = temp_type.split('|')
    if temp_array[0] == '(no genres listed)':
        temp_array[0] = 'Other'
    type_26_28 = []
    if len(temp_array) <= 3:
        tl = 3 - len(temp_array)
        for i in range(0, tl):
            temp_array.append('Other')
    for i in range(0, 3):
        type_26_28.append(type_dic[temp_array[i]])

    train_data_x.append([id_1, count_2, mean_3, var_4,
                         type_5_24[0], type_5_24[1], type_5_24[2], type_5_24[3], type_5_24[4],
                         type_5_24[5], type_5_24[6], type_5_24[7], type_5_24[8], type_5_24[9],
                         type_5_24[10], type_5_24[11], type_5_24[12], type_5_24[13], type_5_24[14],
                         type_5_24[15], type_5_24[16], type_5_24[17], type_5_24[18], type_5_24[19],
                         m_id_25, type_26_28[0], type_26_28[1], type_26_28[2]
                         ])
    score = rating_dic[one][2]
    if score > 4:
        train_data_y.append(1)
    else:
        train_data_y.append(0)

out_x = pd.DataFrame(train_data_x)
out_y = pd.DataFrame(train_data_y)
out_x.to_csv('train_x.csv', header=False, index=False)
out_y.to_csv('train_y.csv', header=False, index=False)