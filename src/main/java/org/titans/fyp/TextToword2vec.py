from gensim.models.keyedvectors import KeyedVectors

model = KeyedVectors.load_word2vec_format("D:/Project/fyp/word2vec/code/work8/up2/doc_vector_representation_LegalIR/Serialized_folder/word2vec.txt", binary=False)
model.save_word2vec_format("D:/Project/fyp/word2vec/code/work8/up2/doc_vector_representation_LegalIR/Serialized_folder/docvec_in_word2vec.bin", binary=True)

print "Word2vec.bin file saved."