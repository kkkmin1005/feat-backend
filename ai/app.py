from tensorflow.keras.applications.resnet import preprocess_input
from tensorflow.keras.preprocessing import image
import tensorflow as tf
import numpy as np
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity
import io
import requests
from flask import Flask, request, jsonify

# Lambda 레이어에 사용된 사용자 정의 함수를 등록
@tf.keras.utils.register_keras_serializable()
def preprocess_input_function(x):
    return preprocess_input(x)

# 모델 로드
model = tf.keras.models.load_model('files/image_classification_model2.h5', custom_objects={'preprocess_input': preprocess_input_function})

#음악 데이터 로드
music_df = pd.read_csv('files/music_data_with_urls2.csv')

#레이블 정의
class_labels = ['likegirls', 'obsence', 'communication', 'nighttime', 'dating', 'energy', 'danceability', 'romance', 'familygospel', 'music', 'violence', 'valence', 'sadness', 'shaketheaudience', 'worldlife']

# 음악 데이터를 벡터화하는 작업
probabilities_vectors = []

for index, row in music_df.iterrows():
    probabilities_vector = []
    for label2 in class_labels:
        try:
            probability = round(float(row[label2]), 4)
            probabilities_vector.append(probability)
        except ValueError:
            probabilities_vector.append(0.0)
    probabilities_vectors.append(probabilities_vector)

# 모델을 이용해 예측 수행

def predict(image_path):
    response = requests.get(image_path)
    img = image.load_img(io.BytesIO(response.content), target_size=(299, 299))  # InceptionV3에 맞게 크기 조정
    img_array = image.img_to_array(img)  # 이미지 배열로 변환
    img_array = np.expand_dims(img_array, axis=0)  # 배치 차원 추가
    img_array = preprocess_input(img_array)  # InceptionV3 전처리 수행

    prediction = model.predict(img_array)

    predicted_probabilities = prediction[0]

    # 음악 데이터와 이미지의 코사인 유사도 측정
    similarities = []
    for vector in probabilities_vectors:
        similarity = cosine_similarity([predicted_probabilities], [vector])[0][0]
        similarities.append(similarity)

    most_similar_indexes = np.argsort(similarities)[-5:][::-1]

    retunList = []

    for i in most_similar_indexes:
        retunList.append(music_df.iloc[i][-1])

    return retunList

app = Flask(__name__)

@app.route('/ai', methods=['POST'])
def ai():
    data = request.get_json()
    image = data['url']
    result = predict(image)

    return result

if __name__ == '__main__':
    app.run(debug=True)

