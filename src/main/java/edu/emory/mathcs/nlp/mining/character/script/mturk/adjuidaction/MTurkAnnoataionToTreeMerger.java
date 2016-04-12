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
package edu.emory.mathcs.nlp.mining.character.script.mturk.adjuidaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.mining.character.structure.Episode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.Season;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;
import edu.emory.mathcs.nlp.mining.character.util.DataLoaderUtil;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 18, 2016
 */
public class MTurkAnnoataionToTreeMerger {
	public static final char DELIM = '|';
	
	public List<Season> merge(List<Season> seasons, Map<String, List<Pair<String, String>>> results){
		Map<Integer, Season> m_seasons = new HashMap<>();
		for(Season season : seasons) m_seasons.put(season.getID(), season);
		
		int[] metedata; Season season; Episode episode; Scene scene;
		for(Entry<String, List<Pair<String, String>>> result : results.entrySet()){
			metedata = DataLoaderUtil.getSceneMetaData(result.getKey());
			
			season = m_seasons.get(metedata[0]); 		if(season == null) continue;
			episode = season.getEpisode(metedata[1]);	if(episode == null) continue;
			scene = episode.getScene(metedata[2]);		if(scene == null) continue;
			
			annotate(expand(scene), result.getValue());
		}
		
		return seasons;
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
	
	private void annotate(List<StatementNode> nodes, List<Pair<String, String>> annotations){
		int n_idx = 0, a_idx = 0, n_size = nodes.size(), a_size = annotations.size();
		char bilou; StatementNode node; Pair<String, String> annotation; 
		
		while(n_idx < n_size && a_idx < a_size){
			node = nodes.get(n_idx);
			annotation = annotations.get(a_idx);
			
			if(annotation.o1.startsWith(node.getWordForm().replace('’', '\'').replaceAll("[^\\x00-\\x7e]", ""))){
				switch( (bilou = node.getReferantLabel().charAt(0)) ){
				case 'U': n_idx = annotateAux(nodes, n_idx, bilou, annotation.o2); a_idx++; break;
				case 'B': n_idx = annotateAux(nodes, n_idx, bilou, annotation.o2); a_idx++; break;
				}
			}
			n_idx ++;
		}
	}
	
	private int annotateAux(List<StatementNode> nodes, int idx, char bilou, String annotation){
		StatementNode node = nodes.get(idx);
		String label = node.getReferantLabel().substring(2);
		String[] original = Splitter.splitPipes(label);
		
		if(original.length > 0 && !label.equals(StringConst.QUESTION)){
			for(String previous : original)
				if(previous.equals(annotation)) return idx;
			annotation = label + DELIM + annotation;
		}
		
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
}
