import pandas as pd


class IntentConfigs:
    encode_length = 15
    filter_sizes = [2, 3, 4, 2, 3, 4, 2, 3, 4, 2, 3, 4]
    num_filters = len(filter_sizes)
    learning_step = 1000000000000
    learning_rate = 0.001
    vector_size = 300
    fallback_score = 11
    train_fasttext = False
    tokenizing = True

    root_path = './chat/intent/'
    model_path = root_path + 'model/'
    fasttext_path = root_path + 'fasttext/'

    def __init__(self):
        self.data = pd.read_csv(self.root_path + 'train_intent.csv')
        self.intent_mapping = {}

        idx = -1
        for q, i in self.data.values:
            if i not in self.intent_mapping:
                idx += 1
            self.intent_mapping[i] = idx
        self.label_size = len(self.intent_mapping)

