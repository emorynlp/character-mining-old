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
package edu.emory.mathcs.nlp.mining.character.script.mturk.result;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.mining.character.eval.CohenKappaEvaluator;
import edu.emory.mathcs.nlp.mining.character.script.structure.MTurkResultEntry;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 17, 2016
 */
public class MTurkResultAgreementBatchEvaluator extends AbstractMTurkResultAgreementEvaluator{
	
	public MTurkResultAgreementBatchEvaluator(int turker_count, int question_count) {
		super(turker_count, question_count);
	}

	@Override
	public void evaluate(List<MTurkResultEntry> entries) {
		evaluator = new CohenKappaEvaluator(turker_count, collectOptions(entries));
		Collections.sort(entries, entry_comp);
		
		Set<String> decisions = new HashSet<>();
		int i, j, k, bound; String[][] m_answer = new String[turker_count][];
		for(i = 0; i <= entries.size()-turker_count; i+=turker_count){
			for(k = 0, bound = question_count; k < turker_count; k++){
				m_answer[k] = extractAnswers(entries, i+k);
				bound = Math.min(bound, getAnswerBoundary(m_answer[k]));
			}
			 
			for(j = 0; j < bound; j++){
				decisions.clear();
				for(k = 0; k < turker_count; k++) {
					decisions.add(m_answer[k][j]);
					evaluator.addEntry(k, m_answer[k][j]);
				}
				if(decisions.size() == 1) 	evaluator.addAgreement();
				else						evaluator.addDisagreement();
			}
		}
	
		System.out.println(String.format("Agreement:\t%.2f", 100*evaluator.getAgreementScore()));
		System.out.println(String.format("Cohen Kappa:\t%.2f", 100*evaluator.getCohenKappaCoefficient()));
		System.out.println(String.format("Unknown:Total = %d / %d (%.2f)", unknown_count, total_count, (double)unknown_count/total_count));
	}

}
