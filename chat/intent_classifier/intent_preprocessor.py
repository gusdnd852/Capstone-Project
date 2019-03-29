import pandas as pd
from gensim.models import FastText
from konlpy.tag import Okt

from util.tokenizer import tokenize

data = pd.read_csv('train_intent.csv')
data = data.dropna()
intent_mapping = {
    '잡담': 0,
    '날씨': 1,
    '위키': 2,
    '먼지': 3,
    '댄스': 4,
    '동화': 5,
    '노래': 6,
    '이슈': 7,
    '알람': 8,
    '심심': 9,
    '농담': 10,
    '번역': 11,
    '명언': 12,
    '시간': 13,
    '날짜': 14,
    '맛집': 15,
    '배고파': 16,
    '환율': 17,
    '뉴스': 18,
    '날씨먼지문맥': 19,
    "번역환율문맥": 20,
    '날씨문맥전환': 21,
    '먼지문맥전환': 22
}

vector_size = 128


def intent_size():
    return len(intent_mapping)


def preprocess():

    data['intent'] = data['intent'].map(intent_mapping)

    for i in data['question']:
        data.replace(i, tokenize(i), regex=True, inplace=True)

    encode = []
    decode = []
    for q, i in data.values:
        encode.append(q)
        decode.append(i)

    return {'encode': encode, 'decode': decode}


def train_vector_model():
    mecab = Okt()
    str_buf = preprocess()['encode']
    pos1 = mecab.pos(''.join(str_buf))
    pos2 = ' '.join(list(map(lambda x: '\n' if x[1] in ['Punctuation'] else x[0], pos1))).split('\n')
    morphs = list(map(lambda x: mecab.morphs(x), pos2))
    model = FastText(size=vector_size,
                     window=3,
                     workers=8,
                     min_count=1,
                     sg=1,
                     iter=500)
    model.build_vocab(morphs)
    model.train(morphs, total_examples=model.corpus_count, epochs=model.epochs)
    return model