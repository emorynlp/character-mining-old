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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.mining.character.script.structure.MTurkResultEntry;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 18, 2016
 */
public class MTurkAnnoataionExtractor {
	private final String 
		SEASONID_FIELD 	 = "Input.Season_Id", 
		EPISODEID_FIELD  = "Input.Episode_Id", 
		SCENEID_FIELD 	 = "Input.Scene_Id",
		INVALID_QUESTION = "-DO NOT ANSWER-";
	
	private final String 
		ANS_FIELD_FORMAT 		= "Answer.Q%dAnswer",
		ANS_OTHER_FIELD_FORMAT 	= "Answer.Q%dAnswer_Other",
		ANS_OTHER				= "Other",
		QUSESTION_FIELD_FORMAT  = "Input.Question_%d";
	
	private int question_count;
	
	public MTurkAnnoataionExtractor(int question_count){
		this.question_count = question_count;
	}
	
	public Map<String, List<Pair<String, String>>> extractAnnotation(List<MTurkResultEntry> entries){
		Map<String, List<Pair<String, String>>> map = new HashMap<>();
		
		List<Pair<String, String>> list;
		int i, season, episode, scene; String question, ans;
		for(MTurkResultEntry entry : entries){
			season 	= Integer.parseInt(entry.getField(SEASONID_FIELD));
			episode	= Integer.parseInt(entry.getField(EPISODEID_FIELD));
			scene 	= Integer.parseInt(entry.getField(SCENEID_FIELD));
			
			list = new ArrayList<>();
			map.put(getIndexString(map, season, episode, scene), list);
					
			for(i = 1; i <= question_count; i++){
				question = entry.getField(String.format(QUSESTION_FIELD_FORMAT, i));
				if(!question.equals(INVALID_QUESTION))
					question = question.substring(question.indexOf('\'')+1, question.lastIndexOf('\''));
				else continue;
				
				ans = entry.getField(String.format(ANS_FIELD_FORMAT, i));
				if(ans.equals(ANS_OTHER)) ans = entry.getField(String.format(ANS_OTHER_FIELD_FORMAT, i));
				list.add(new Pair<>(question, ans));
			}
		}
		return map;
	}
	
	public Map<String, List<Pair<String, String>>> extractAdjudication(List<MTurkResultEntry> entries){
		Map<String, List<Pair<String, String>>> map = new HashMap<>();
		
		List<Pair<String, String>> list;
		int i, season, episode, scene; String question, ans;
		for(MTurkResultEntry entry : entries){
			season 	= Integer.parseInt(entry.getField(SEASONID_FIELD));
			episode	= Integer.parseInt(entry.getField(EPISODEID_FIELD));
			scene 	= Integer.parseInt(entry.getField(SCENEID_FIELD));
			
			list = new ArrayList<>();
			map.put(getIndexString(map, season, episode, scene), list);
					
			for(i = 1; i <= question_count; i++){
				question = entry.getField(String.format(QUSESTION_FIELD_FORMAT, i));
				if(!question.equals(INVALID_QUESTION))
					question = question.substring(question.indexOf('\'')+1, question.lastIndexOf('\''));
				else continue;
				
				ans = entry.getField(String.format(ANS_FIELD_FORMAT, i));
				if(ans.equals(ANS_OTHER)) ans = entry.getField(String.format(ANS_OTHER_FIELD_FORMAT, i));
				list.add(new Pair<>(question, ans));
			}
		}
		
		return map;
	}
	
	private String getIndexString(Map<String, List<Pair<String, String>>> map, int season, int episode, int scene){
		int count = 0; String path;
		while(true){
			path = String.format("%02d%02d%02d-%d", season, episode, scene, count++);
			if(!map.containsKey(path)) return path;
		}
	}
}
