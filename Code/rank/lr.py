import tensorflow.keras as keras
import tensorflow as tf
import pandas as pd
import numpy as np

batch_size = 32
input_size = 28
output_size = 1

x = pd.read_csv('train_x.csv').values
y = pd.read_csv('train_y.csv').values

x_ = []
y_ = []
tempX = []
tempY = []
for i in range(1000):
    tempX.append(x[i])
    tempY.append(y[i])
    if (i + 1) % 32 == 0:
        x_.append(tempX)
        y_.append(tempY)
        tempX = []
        tempY = []

if len(tempX) > 0:
    for i in range(32 - len(tempX)):
        tempX.append(x[i])
        tempY.append(y[i])
    x_.append(tempX)
    y_.append(tempY)

# example
model = tf.keras.Sequential()
model.add(tf.keras.layers.Dense(64))
model.add(tf.keras.layers.Dense(64))
model.add(tf.keras.layers.Dense(1))
model.compile(optimizer='adam', loss='mse')
# This builds the model for the first time:
model.fit(x, y, batch_size=32, epochs=10, validation_split=0.2)
# inputs = keras.layers.Input(shape=input_size)
# layer = keras.layers.Dense(units=64, activation=tf.nn.relu)(inputs)
# outputs = keras.layers.Dense(units=output_size, activation=tf.nn.relu)(layer)

# model define
# model = keras.models.Model(inputs=x, outputs=y)
#
# model.compile(optimizer='adam', loss='mse')

# model.fit(x=x, y=y, validation_split=0.2, epochs=15, steps_per_epoch=200)
