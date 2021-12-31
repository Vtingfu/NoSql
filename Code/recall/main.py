# -*- coding = utf-8 -*-
import csv

import utils
from ItemCF import ItemBasedCF
from LFM import LFM
from UserCF import UserBasedCF
from dataset import DataSet
from most_popular import MostPopular
from random_pred import RandomPredict
from utils import LogTime


def run_model(model_name, dataset_name, test_size=0.3, clean=False):
    print('*' * 70)
    print('\tThis is %s model trained on %s with test_size = %.2f' % (model_name, dataset_name, test_size))
    print('*' * 70 + '\n')
    model_manager = utils.ModelManager(dataset_name, test_size)
    try:
        trainset = model_manager.load_model('trainset')
        testset = model_manager.load_model('testset')
    except OSError:
        ratings = DataSet.load_dataset(name=dataset_name)
        trainset, testset = DataSet.train_test_split(ratings, test_size=test_size)
        model_manager.save_model(trainset, 'trainset')
        model_manager.save_model(testset, 'testset')
    model_manager.clean_workspace(clean)
    if model_name == 'UserCF':
        model = UserBasedCF()
    elif model_name == 'ItemCF':
        model = ItemBasedCF()
    elif model_name == 'Random':
        model = RandomPredict()
    elif model_name == 'MostPopular':
        model = MostPopular()
    elif model_name == 'UserCF-IIF':
        model = UserBasedCF(use_iif_similarity=True)
    elif model_name == 'ItemCF-IUF':
        model = ItemBasedCF(use_iuf_similarity=True)
    elif model_name == 'LFM':
        # K, epochs, alpha, lamb, n_rec_movie
        model = LFM(10, 20, 0.1, 0.01, 10)
    else:
        raise ValueError('No model named ' + model_name)
    model.fit(trainset)

    f = open('./data/result/recall_1.csv', 'w', encoding='utf-8', newline='')
    csv_writer = csv.writer(f)
    csv_writer.writerow(["用户ID", "movie1", "movie2", "movie3", "movie4", "movie5"])

    # recommend_test(model, [1, 100, 233, 666, 888])

    for i in range(900):
        recommend = model.recommend(str(i + 1))
        if recommend == 'error':
            csv_writer.writerow([i + 1, 0
                                      , 0
                                      , 0
                                      , 0
                                      , 0
                                 ])
        else:
            csv_writer.writerow([i + 1, recommend[0]
                                      , recommend[1]
                                      , recommend[2]
                                      , recommend[3]
                                      , recommend[4]
                                 ])

    f.close()
    # model.test(testset)


if __name__ == '__main__':
    main_time = LogTime(words="Main Function")
    dataset_name = 'ml-1m'
    # model_type = 'UserCF'
    # model_type = 'UserCF-IIF'
    model_type = 'ItemCF'
    # model_type = 'Random'
    # model_type = 'MostPopular'
    # model_type = 'ItemCF-IUF'
    # model_type = 'LFM'
    test_size = 0.9
    run_model(model_type, dataset_name, test_size, False)
    main_time.finish()
