package zx.soft.negative.sentiment.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.file.ReadFileUtils;

public class EmotionanalysisClassify extends ClassifySentiment {

	private static Logger logger = LoggerFactory.getLogger(NegativeClassify.class);
	private static EmotionanalysisClassify instance = null;

	public EmotionanalysisClassify() {
		super();
		logger.info("Initing negative-words , positive-words Starting ...");
		// negwords
		emotionDictionary.setCatewords(ReadFileUtils.getFileToListFromResources(BASE_DIR + "negative"));
		emotionDictionary.setPositivewords(ReadFileUtils.getFileToListFromResources(BASE_DIR + "positive"));
		logger.info("Initing negative-words , posttive-words Finishing ...");
	}

	public static EmotionanalysisClassify getInstance() {
		if (instance == null) {
			instance = new EmotionanalysisClassify();
		}
		return instance;
	}

	@Override
	public float getSentenceScore(String sentence) {
		List<String> words = analyzerTool.analyzerTextToList(sentence);
		float negScore = 0.0f;
		float baseScore = 0.0f;
		int wordIndex = 0;
		int sentimentWordsIndex = 0;
		//		System.out.println(words);
		for (String word : words) {

			//calculate negative score
			if (emotionDictionary.getCatewords().contains(word)) {
				baseScore = 1;
				sentimentWordsIndex=wordIndex-sentimentWordsIndex>5?5:sentimentWordsIndex;
				for (int i = sentimentWordsIndex; i < wordIndex; i++) {
					baseScore *= sentimentLevel(words.get(i));
				}
				//				System.out.println("负面  " + word);
				negScore -= baseScore;
				sentimentWordsIndex = wordIndex + 1;
				//calculate positive score
			} else if (emotionDictionary.getPositivewords().contains(word)) {
				baseScore = 1;
				sentimentWordsIndex=wordIndex-sentimentWordsIndex>5?5:sentimentWordsIndex;
				for (int i = sentimentWordsIndex; i < wordIndex; i++) {
					baseScore *= sentimentLevel(words.get(i));
				}
				//				System.out.println("正面  " + word);
				negScore += baseScore;
				sentimentWordsIndex = wordIndex + 1;
			} else if ((word.equals("!") || word.equals("！"))) {

				if (emotionDictionary.getCatewords().contains(words.get(wordIndex - 1))) {
					negScore -= 2;
				} else if (emotionDictionary.getPositivewords().contains(words.get(wordIndex - 1))) {
					negScore += 2;
				}

			}
			wordIndex++;
		}
		//		System.out.println(negScore);

		return negScore;
	}

	@Override
	public float sentimentLevel(String degreeWord) {
		if (emotionDictionary.getMost().contains(degreeWord)) {
			return 2.0f;
		} else if (emotionDictionary.getVery().contains(degreeWord)) {
			return 1.5f;
		} else if (emotionDictionary.getMore().contains(degreeWord)) {
			return 1.25f;
		} else if (emotionDictionary.getIsh().contains(degreeWord)) {
			return 0.5f;
		} else if (emotionDictionary.getInsufficiently().contains(degreeWord)) {
			return 0.25f;
		} else if (emotionDictionary.getInverse().contains(degreeWord)) {
			return -1f;
		} else {
			return 1f;
		}
	}

}
