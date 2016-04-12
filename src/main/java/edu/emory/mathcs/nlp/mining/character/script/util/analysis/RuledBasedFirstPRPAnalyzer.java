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
package edu.emory.mathcs.nlp.mining.character.script.util.analysis;

import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 23, 2016
 */
public class RuledBasedFirstPRPAnalyzer {
	private final Set<String> 
		FST_PER_PRP = DSUtils.createSet("i", "me", "my", "mine", "myself"),
		QUOTES		= DSUtils.createSet(StringConst.SINGLE_QUOTE, StringConst.DOUBLE_QUOTE);
	
	public void analyze(Scene scene){
		List<Utterance> utterances = scene.getUtterances();
		int uid, sid = 0, tid, nid; String wordform_low;
		List<StatementNode[]> trees; StatementNode[] nodes;
		
		for(uid = 0; uid < utterances.size(); uid++){
			trees = utterances.get(uid).getStatementTrees();
			
			for(tid = 0; tid < trees.size(); tid++, sid++){
				nodes = trees.get(tid);
				for(nid = 1; nid < nodes.length; nid++){
					wordform_low = nodes[nid].getWordFormSimplifiedLowercase();
					if(FST_PER_PRP.contains(wordform_low) && analyzeAux(trees, tid, nid))
						System.out.println(String.format("%d %d %s", uid, sid, nodes[nid]));
				}
			}
		}
	}
	
	private boolean analyzeAux(List<StatementNode[]> trees, int tid, int nid){
		boolean hasLeft = false, hasRight = false;
		int i, j; StatementNode[] tree = trees.get(tid);
		
		// Check current tree
		for(i = nid-1; !hasLeft && i > 0; i--){
			if(QUOTES.contains(tree[nid].getWordForm()))
				hasLeft = true;
		}
		for(i = nid+1; !hasRight && i < tree.length; i++){
			if(QUOTES.contains(tree[nid].getWordForm()))
				hasRight = true;
		}
		
		// Check other utterance tree
		if(!hasLeft){
			for(i = tid - 1; !hasLeft && i >= 0; i--){
				tree = trees.get(i);
				for(j = tree.length-1; !hasLeft && j > 0; j--){
					if(QUOTES.contains(tree[j].getWordForm()))
							hasLeft = true;
				}
			}
		}
		if(!hasRight){
			for(i = tid + 1; !hasRight && i < trees.size(); i++){
				tree = trees.get(i);
				for(j = 0; !hasRight && j < tree.length; j++){
					if(QUOTES.contains(tree[j].getWordForm()))
						hasRight = true;
				}
			}
		}
		return hasLeft && hasRight;
	}
}
