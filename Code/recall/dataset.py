# -*- coding = utf-8 -*-

import collections
import os
import itertools
import random
from collections import namedtuple

BuiltinDataset = namedtuple('BuiltinDataset', ['url', 'path', 'sep', 'reader_params'])

BUILTIN_DATASETS = {
    'ml-25m'  :
        BuiltinDataset(
            path='data/ml-1m/ratings.csv',
            sep=',',
            reader_params=dict(line_format='user item rating timestamp',
                               rating_scale=(1, 5),
                               sep=',')
        ),
}

# modify the random seed will change dataset spilt.
# if you want to use the model saved before, please don't modify this seed.
random.seed(0)


class DataSet:
    def __init__(self):
        pass

    @classmethod
    def load_dataset(cls, name='ml-100k'):
        try:
            dataset = BUILTIN_DATASETS[name]
        except KeyError:
            raise ValueError('unknown dataset ' + name +
                             '. Accepted values are ' +
                             ', '.join(BUILTIN_DATASETS.keys()) + '.')
        if not os.path.isfile(dataset.path):
            raise OSError(
                "Dataset data/" + name + " could not be found in this project.\n"
                                         "Please download it from " + dataset.url +
                ' manually and unzip it to data/ directory.')
        with open(dataset.path) as f:
            ratings = [cls.parse_line(line, dataset.sep) for line in itertools.islice(f, 0, None)]
        print("Load " + name + " dataset success.")
        return ratings

    @classmethod
    def parse_line(cls, line: str, sep: str):

        user, movie, rate = line.strip('\r\n').split(sep)[:3]
        return user, movie, rate

    @classmethod
    def train_test_split(cls, ratings, test_size=0.2):

        train, test = collections.defaultdict(dict), collections.defaultdict(dict)
        trainset_len = 0
        testset_len = 0
        for user, movie, rate in ratings:
            if random.random() <= test_size:
                print(rate)
                # rate.strip()  # 删除s开头、结尾的rm
                # rate.lstrip()  # 删除s开头的rm
                # rate.rstrip()  # 删除s结尾的rm
                test[user][movie] = float(rate)
                testset_len += 1
            else:
                print(rate)
                train[user][movie] = float(rate)
                trainset_len += 1
        print('split rating data to training set and test set success.')
        print('train set size = %s' % trainset_len)
        print('test set size = %s\n' % testset_len)
        return train, test
