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
package edu.emory.mathcs.nlp.component.character;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.collection.chunk.Chunk;
import edu.emory.mathcs.nlp.common.dictionary.PersonalCommonNoun;
import edu.emory.mathcs.nlp.common.dictionary.Pronoun;
import edu.emory.mathcs.nlp.common.treebank.POSLibEn;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.chunk.Chunker;
import edu.emory.mathcs.nlp.common.util.chunk.NamedEntityChunker;
import edu.emory.mathcs.nlp.common.util.chunk.NounChunker;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.node.StatementNode;
import edu.emory.mathcs.nlp.component.template.util.BILOU;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 30, 2016
 */
public class CharacterMentionRecognizer {
	final static Set<String> TARGET_NAMED_ENTITY = DSUtils.toHashSet("PERSON");
	final static String UNKNOWN_B = "B-?", UNKNOWN_I = "I-?", UNKNOWN_L = "L-?", UNKNOWN_U = "U-?";
	
	private Chunker<StatementNode> nn_chunker, ner_chunker;
	private PersonalCommonNoun personal_nouns;
	
	public CharacterMentionRecognizer(){
		nn_chunker 	= new NounChunker<>();
		personal_nouns = new PersonalCommonNoun();
		ner_chunker = new NamedEntityChunker<>(TARGET_NAMED_ENTITY);
	} 
	
	public StatementNode[] process(NLPNode[] nodes){
		StatementNode[] s_nodes = (StatementNode[]) 
			Arrays.stream(nodes).map(StatementNode::new).toArray();
		s_nodes[0].toRoot();
		
		return process(s_nodes);
	}
	
	public StatementNode[] process(StatementNode[] nodes){
		int i, len; StatementNode node; String word;
		List<Chunk<StatementNode>> candidates = nn_chunker.getChunks(nodes);
		for(i = nodes.length-1; i > 0; i--){
			node = candidates.get(i).getHeadNode();
			
			if(node.getPartOfSpeechTag().startsWith(POSLibEn.POS_NN)){
				word = node.getWordFormSimplifiedLowercase();
				if(Pronoun.NONPERSON.contains(word) || !personal_nouns.isPersonalCommonNoun(word)
						|| !node.getNamedEntityTag().equals(BILOU.O.toString()))
					candidates.remove(i);
			}
		}
		candidates.addAll(ner_chunker.getChunks(nodes));
		Collections.sort(candidates);
		
		for(Chunk<StatementNode> candidate : candidates){
			nodes = candidate.getNodes(); len = nodes.length;
			
			if(len == 1)	nodes[0].setReferantLabel(UNKNOWN_U);
			else{
				nodes[0].setReferantLabel(UNKNOWN_B);
				for(i = 1; i < len-1; i++)
					nodes[i].setReferantLabel(UNKNOWN_I);
				nodes[len-1].setReferantLabel(UNKNOWN_L);
			}
		}
		return nodes;
	}
}
