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
package edu.emory.mathcs.nlp.mining.character.chunk;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.treebank.POSLibEn;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.util.BILOU;
import edu.emory.mathcs.nlp.mining.character.constant.Dictionary;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 10, 2016
 */
public class EnglighReferentChunker extends Chucker{

	private EnglishNounChunker nn_chunker;
	private EnglishNamedEntityChunker ner_chunker;
	private Set<String> dict_person_nouns;
	
	public EnglighReferentChunker(Set<String> target_ner_tags) {
		nn_chunker = new EnglishNounChunker();
		ner_chunker = new EnglishNamedEntityChunker(target_ner_tags);
		dict_person_nouns = DSUtils.createStringHashSet(IOUtils.createFileInputStream(Dictionary.PERSON_NOUN));
	}
	
	@Override
	public List<Chunk> getChunks(NLPNode[] nodes) {
		List<Chunk> 
			nn_chunks  = nn_chunker.getChunks(nodes),
			ner_chunks = ner_chunker.getChunks(nodes);

		NLPNode head;
		for(int i = nn_chunks.size()-1; i >= 0; i--){
			head = nn_chunks.get(i).getHeadNode();
			if(head.getPartOfSpeechTag().startsWith(POSLibEn.POS_NN))
				if(!head.getNamedEntityTag().equals(BILOU.O.toString()) || 
				   !dict_person_nouns.contains(head.getWordFormSimplifiedLowercase()))			
				nn_chunks.remove(i);
		}
		nn_chunks.addAll(ner_chunks);
		Collections.sort(nn_chunks);
		
		return nn_chunks;
	}
}
