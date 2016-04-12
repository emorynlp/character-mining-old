/**
 * Copyright 2016, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.mining.character.script.friends;

import java.util.List;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.script.mturk.result.AbstractMTurkResultAgreementEvaluator;
import edu.emory.mathcs.nlp.mining.character.script.mturk.result.MTurkResultAgreementBatchEvaluator;
import edu.emory.mathcs.nlp.mining.character.script.mturk.result.MTurkResultAgreementIndividualEvaluator;
import edu.emory.mathcs.nlp.mining.character.script.mturk.result.MTurkResultParser;
import edu.emory.mathcs.nlp.mining.character.script.structure.MTurkResultEntry;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 17, 2016
 */
public class MTurkAnnotationParseEvalScript {
	public static final int
		NUM_TURKER 		= 2,
		NUM_QUESTION 	= 50;
	
	public static final double
		THRESHOLD = 0.65d;
	
	public static final String
		RESULT_CSV = "/Users/HenryChen/Desktop/CharacterDetection/MTurk_out/friends/Friends_s2_mturk_result_corrected.csv",
		RESULT_OUT_DIR = "/Users/HenryChen/Desktop/CharacterDetection/MTurk_out/friends/results",
		RESULT_HTML_EXT = ".html",
		RESULT_KEY_EXT = ".key";
	
	public static void main(String[] args){
		MTurkResultParser parser = new MTurkResultParser(NUM_QUESTION);
		AbstractMTurkResultAgreementEvaluator b_evaluator = new MTurkResultAgreementBatchEvaluator(NUM_TURKER, NUM_QUESTION);
		AbstractMTurkResultAgreementEvaluator i_evaluator = new MTurkResultAgreementIndividualEvaluator(NUM_TURKER, NUM_QUESTION, THRESHOLD);
		
		List<MTurkResultEntry> entries = parser.parseMTurkResult(IOUtils.createFileInputStream(RESULT_CSV));
		parser.exportMTurkResult(entries, RESULT_OUT_DIR, RESULT_HTML_EXT, RESULT_KEY_EXT);
		i_evaluator.evaluate(entries); System.out.println(); 
		b_evaluator.evaluate(entries); System.out.println();
	}
}
