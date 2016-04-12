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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.mining.character.eval.CohenKappaEvaluator;
import edu.emory.mathcs.nlp.mining.character.script.structure.MTurkResultEntry;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Feb 8, 2016
 */
public abstract class AbstractMTurkResultAgreementEvaluator {	
	protected static String 
		SEASON_FIELDNAME 	= "Input.Season_Id", 
		EPISODE_FIELDNAME 	= "Input.Episode_Id", 
		SCENE_FIELDNAME 	= "Input.Scene_Id",
		Q_FORMAT 			= "Answer.Q%dAnswer",
		NA 					= "N/A";
	
	protected static  Comparator<MTurkResultEntry> entry_comp = new Comparator<MTurkResultEntry>() {
		@Override
		public int compare(MTurkResultEntry o1, MTurkResultEntry o2) {
			int diff; 
			
			diff = Integer.parseInt(o1.getField(SEASON_FIELDNAME)) - Integer.parseInt(o2.getField(SEASON_FIELDNAME));
			if(diff != 0)	return diff;
			
			diff = Integer.parseInt(o1.getField(EPISODE_FIELDNAME)) - Integer.parseInt(o2.getField(EPISODE_FIELDNAME));
			if(diff != 0)	return diff;
			
			diff = Integer.parseInt(o1.getField(SCENE_FIELDNAME)) - Integer.parseInt(o2.getField(SCENE_FIELDNAME));
			return diff;
		}
	};
	
	protected int turker_count, question_count;
	protected int unknown_count, total_count;
	protected CohenKappaEvaluator evaluator;
	
	public AbstractMTurkResultAgreementEvaluator(int turker_count, int question_count){
		this.turker_count = turker_count;
		this.question_count = question_count;
		reset();
	}
	
	public void reset(){
		unknown_count = total_count = 0;
	}

	abstract public void evaluate(List<MTurkResultEntry> entries);
	
	protected String[] collectOptions(List<MTurkResultEntry> list){
		Set<String> set = new HashSet<>();
	
		for(MTurkResultEntry entry : list){
			for(int i = 1; i <= question_count; i++)
				set.add(entry.getField(String.format(Q_FORMAT, i)));
		}
		return set.toArray(new String[set.size()]);
	}
	
	protected String[] extractAnswers(List<MTurkResultEntry> list, int index){
		String[] answers = new String[question_count];
		
		MTurkResultEntry entry = list.get(index);
		for(int i = 1; i <= question_count; i++)
			answers[i-1] = entry.getField(String.format(Q_FORMAT, i));
		return answers;
	}
	
	protected int getAnswerBoundary(String[] answer){
		for(int i = 0; i < question_count; i++){
			if(answer[i].equals("-N/A-") || answer[i].equals("Unknown"))
				unknown_count++;
			if(answer[i].equals(NA)) {
				total_count += i+1; return i;
			}
		}
		return question_count;
	}
}
