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
package edu.emory.mathcs.nlp.mining.character.script.mturk.release;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.mining.character.script.mturk.adjuidaction.MTurkAnnoataionToTreeMerger;
import edu.emory.mathcs.nlp.mining.character.structure.Episode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.Season;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;
import edu.emory.mathcs.nlp.mining.character.util.DataLoaderUtil;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 21, 2016
 */
public class MTurkAdjudicationToTreeMerger {
	public static final char DELIM = MTurkAnnoataionToTreeMerger.DELIM;
	
	private int[] metedata; 
	
	public List<Season> merge(List<Season> seasons, Map<String, List<Pair<String, String>>> results){
		Map<Integer, Season> m_seasons = new HashMap<>();
		for(Season season : seasons) m_seasons.put(season.getID(), season);
		
		results = collectResults(results);
		Season season; Episode episode; Scene scene;
		
		for(Entry<String, List<Pair<String, String>>> result : results.entrySet()){
			metedata = DataLoaderUtil.getSceneMetaData(result.getKey());
			
			season = m_seasons.get(metedata[0]); 		if(season == null) continue;
			episode = season.getEpisode(metedata[1]);	if(episode == null) continue;
			scene = episode.getScene(metedata[2]);		if(scene == null) continue;
			
			annotate(expand(scene), result.getValue());
		}
		
		return seasons;
	}
	
	private void annotate(List<StatementNode> nodes, List<Pair<String, String>> annotations){
		int n_idx = 0, a_idx = 0, n_size = nodes.size(), a_size = annotations.size();
		char bilou; StatementNode node; Pair<String, String> annotation; 
		
		while(n_idx < n_size && a_idx < a_size){
			node = nodes.get(n_idx);
			annotation = annotations.get(a_idx);
			
			if(node.getReferantLabel().indexOf(DELIM) > 0 && annotation.o1.startsWith(node.getWordForm().replace('’', '\'').replaceAll("[^\\x00-\\x7e]", ""))){
				
				switch( (bilou = node.getReferantLabel().charAt(0)) ){
				case 'U': n_idx = annotateAux(nodes, n_idx, bilou, annotation.o2); a_idx++; break;
				case 'B': n_idx = annotateAux(nodes, n_idx, bilou, annotation.o2); a_idx++; break;
				}
				if(annotation.o2.indexOf(DELIM) > 0)
					System.out.println(String.format("Disagreement in adjudication (s%d-e%d-s%d):\t%s", metedata[0],  metedata[1],  metedata[2], node));
			}
			n_idx++;
		}
	}
	
	private int annotateAux(List<StatementNode> nodes, int idx, char bilou, String annotation){
		StatementNode node = nodes.get(idx);
		
		if(bilou == 'U')  node.setReferantLabel(String.format("%c-%s", bilou, annotation));
		if(bilou == 'B'){
			node.setReferantLabel(String.format("%c-%s", bilou, annotation));
			for(idx++; idx < nodes.size(); idx++){
				node = nodes.get(idx); bilou = node.getReferantLabel().charAt(0);
				if(bilou != 'I' && bilou != 'L') break;
				node.setReferantLabel(String.format("%c-%s", bilou, annotation));
			}
		}
		return idx;
	}
	
	private List<StatementNode> expand(Scene scene){
		int i; List<StatementNode> nodes = new ArrayList<>();
		
		for(Utterance utterance : scene.getUtterances()){
			for(StatementNode[] tree : utterance.getStatementTrees()){
				for(i = 1; i < tree.length; i++) nodes.add(tree[i]);
			}
		}
		return nodes;
	}
	
	private Map<String, List<Pair<String, String>>> collectResults(Map<String, List<Pair<String, String>>> results){
		Set<String> keys = results.keySet().stream().map(k->k.substring(0,6)).collect(Collectors.toSet());
		Map<String, List<Pair<String, String>>> map = new HashMap<>();
		
		int count; List<Pair<String, String>> sublist;
		List<List<Pair<String, String>>> lists = new ArrayList<>();
		for(String key : keys){
			lists.clear(); count = 0;
			
			while( (sublist = results.get(String.format("%s-%d", key, count++))) != null)
				lists.add(sublist);
			map.put(key, getVotedResult(lists));
		}
		
		return map;
	}
	
	private List<Pair<String, String>> getVotedResult(List<List<Pair<String, String>>> lists){
		if(lists.size() > 0){			
			List<String> labels; Pair<String, String> pair; List<Entry<String, Integer>> entries;
			List<Pair<String, String>> result = new ArrayList<>(); String question;
			int i, idx, count, entry_count = lists.get(0).size();
			
			Map<String, Integer> m_count = new HashMap<>();
			for(idx = 0; idx < entry_count; idx++){
				m_count.clear();
				
				question = lists.get(0).get(idx).o1;
				for(List<Pair<String, String>> list : lists){
					pair = list.get(idx);
					if(question.equals(pair.o1)){
						count = m_count.getOrDefault(pair.o2, 0);
						m_count.put(pair.o2, count+1);
					}
				}
				
				entries = new ArrayList<>(m_count.entrySet());
				Collections.sort(entries, Result_Count_Comp);
				
				count = entries.get(0).getValue();
				for(i = 1; i < entries.size(); i++)
					if(entries.get(i).getValue() < count) break;
				labels = entries.subList(0, i).stream().map(e->e.getKey()).collect(Collectors.toList());
				result.add(new Pair<String, String>(question, Joiner.join(labels, Character.toString(DELIM))));
			}
			return result;
		}
		return null;
	}

	
	private static final Comparator<Entry<String, Integer>> 
	Result_Count_Comp = new Comparator<Map.Entry<String,Integer>>() {
		@Override
		public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
			return o2.getValue() - o1.getValue();
		}
	}; 
}
