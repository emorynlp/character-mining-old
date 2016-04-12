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
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.mining.character.eval.CohenKappaEvaluator;
import edu.emory.mathcs.nlp.mining.character.script.structure.MTurkResultEntry;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 17, 2016
 */
public class MTurkResultAgreementIndividualEvaluator extends AbstractMTurkResultAgreementEvaluator{

	private double threshold = 0d;
	private int flagged_count;
	
	public MTurkResultAgreementIndividualEvaluator(int turker_count, int question_count) {
		super(turker_count, question_count);	
		flagged_count = 0;
	}
	
	public MTurkResultAgreementIndividualEvaluator(int turker_count, int question_count, double threshold) {
		super(turker_count, question_count);
		this.threshold = threshold;
		flagged_count = 0;
	}

	@Override
	public void evaluate(List<MTurkResultEntry> entries) {
		evaluator = new CohenKappaEvaluator(turker_count, collectOptions(entries));
		Collections.sort(entries, entry_comp);
		
		Set<String> decisions = new HashSet<>();
		StringJoiner w_joiner, t_joiner; MTurkResultEntry entry;
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
			
			// Print out eval info
			if(evaluator.getAgreementScore() < threshold){
				entry = entries.get(i); flagged_count++;
				System.out.printf("S%02d E%02d s%02d:\t", 
						Integer.parseInt(entry.getField("Input.Season_Id")), 
						Integer.parseInt(entry.getField("Input.Episode_Id")), 
						Integer.parseInt(entry.getField("Input.Scene_Id")));
				System.out.printf("A:%.2f\tK:%.2f\t", evaluator.getAgreementScore(), evaluator.getCohenKappaCoefficient());
				
				w_joiner = new StringJoiner(", "); t_joiner = new StringJoiner(", ");
				for(k = 0; k < turker_count; k++){
					w_joiner.add(entries.get(i+k).getField("WorkerId"));
					t_joiner.add(entries.get(i+k).getField("AssignmentId"));
				}
				System.out.printf("Assign: [ %s ]\tWorker: [ %s ]\n", t_joiner.toString(), w_joiner.toString());
			}
			evaluator.clear();
		}
		System.out.printf("Flagged: %d/%d (A < %.2f)\n", flagged_count, entries.size(), threshold);
	}

}
