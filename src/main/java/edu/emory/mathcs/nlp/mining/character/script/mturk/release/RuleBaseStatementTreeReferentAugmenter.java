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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.treebank.POSLibEn;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.util.BILOU;
import edu.emory.mathcs.nlp.mining.character.structure.Episode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.Season;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 21, 2016
 */
public class RuleBaseStatementTreeReferentAugmenter {
	public final Set<String>
		FST_PER_PRP = new HashSet<>(Arrays.asList("i", "im", "me", "my", "mine", "myself")),
		NON_PER_PRP = new HashSet<>(Arrays.asList("it", "its", "itself")),
		COLLECT_PRP = new HashSet<>(Arrays.asList("we", "us", "our", "ourselves", "ours", "they", "them", "their", "themselves", "theirs")),
		ALL_PRP		= new HashSet<>(Arrays.asList("i", "im", "ive", "me", "my", "mine", "myself", "you", "youre", "youve", "your", "yours", "yourself", "yourselves", "he", "his",  
			"him", "himself", "she", "her", "hers", "herself", "we", "us", "our", "ourselves", "ours", "they", "them", "their", "themselves", "theirs")),
		COMMON_NN	= DSUtils.createStringHashSet(IOUtils.createFileInputStream("src/main/resource/dictionary/Person_Nouns.txt"), true, true);
	
	public void augmentAll(List<Season> seasons){
		for(Season season : seasons){
			for(Episode episode : season){
				for(Scene scene : episode){
					System.out.println(String.format("Augmenting s%d-e%d-s%d", season.getID(), episode.getID(), scene.getID()));
					for(Utterance utterance : scene) augment(utterance);
				}
			}
		}
	}
	
	public void augment(Utterance utterance){
		String speaker = utterance.getSpeaker(), speaker_phrase = null,
			label, word_form, word_form_low, pos, ner;
		int i; char bilou; StatementNode node;
		
		for(StatementNode[] tree : utterance.getStatementTrees()){
			for(i = 1; i < tree.length; i++){
				node = tree[i];
				label = node.getReferantLabel(); bilou = label.charAt(0);
				word_form = node.getWordForm(); word_form_low = node.getWordFormSimplifiedLowercase();
				
				if(bilou != 'O'){
					if(label.indexOf(CharConst.QUESTION) > 0){
						pos = node.getPartOfSpeechTag(); ner = node.getNamedEntityTag();
						
						// non-person (3rd person) pronoun
							 if(NON_PER_PRP.contains(word_form_low)) 
								 node.setReferantLabel(BILOU.O.toString());
						// Collective pronoun
						else if(COLLECT_PRP.contains(word_form_low))
								 node.setReferantLabel(String.format("%c-%s", bilou, "Collective"));
						// Proper noun
						else if(pos.startsWith(POSLibEn.POS_NN) && ner.endsWith("PERSON")){
								switch (bilou) {
								case 'U':	node.setReferantLabel(String.format("%c-%s", bilou, word_form)); break;
								case 'B':	
									speaker_phrase = extractBLPhrase(tree, i);
									node.setReferantLabel(String.format("%c-%s", bilou, speaker_phrase));
									break;
								case 'I':
									if(speaker_phrase != null)
										node.setReferantLabel(String.format("%c-%s", bilou, speaker_phrase));
									break;						
								case 'L':
									if(speaker_phrase != null)
										node.setReferantLabel(String.format("%c-%s", bilou, speaker_phrase));
									speaker_phrase = null; 
									break;
								}
						}
						// First-person pronoun
						else if(FST_PER_PRP.contains(word_form_low)) 
							node.setReferantLabel(String.format("%c-%s", bilou, speaker));
						else System.out.println("Exception case:\t" + node);
					}
					else if(label.endsWith("Error")){
						if(ALL_PRP.contains(word_form_low) || COMMON_NN.contains(word_form_low))
							node.setReferantLabel(String.format("%c-%s", bilou, "Unknown"));
						else node.setReferantLabel(BILOU.O.toString());
					}
					else if(label.endsWith("Collective(You)"))
						node.setReferantLabel(String.format("%c-%s", bilou, "Collective"));
				}
			}
		}
	}
	
	private String extractBLPhrase(StatementNode[] tree, int idx){
		StringJoiner joiner = new StringJoiner(StringConst.SPACE);
		
		char bilou; boolean start = false;
		StatementNode node = tree[idx];
		for(StatementNode n : tree){
			bilou = n.getReferantLabel().charAt(0);
			
			if(n == node && bilou == 'B') start = true;
			if(start){
				joiner.add(n.getWordForm());
				if(bilou == 'L') break;
			}
		}
		return joiner.toString();
	}
}
