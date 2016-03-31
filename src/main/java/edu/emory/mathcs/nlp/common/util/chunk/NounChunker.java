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
package edu.emory.mathcs.nlp.common.util.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.emory.mathcs.nlp.common.collection.chunk.Chunk;
import edu.emory.mathcs.nlp.common.treebank.POSLibEn;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 9, 2016
 */
public class NounChunker<N extends NLPNode> extends Chunker<N>{
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Chunk<N>> getChunks(NLPNode[] nodes) {
		List<Chunk<N>> chunks = new ArrayList<>();
		
		int start, end; String head_pos, pos; 
		for(start = 1; start < nodes.length; start++){
			head_pos = nodes[start].getPartOfSpeechTag();
			
			if(head_pos.startsWith(POSLibEn.POS_NN) || head_pos.startsWith(POSLibEn.POS_PRP)){
				for(end = start+1; end < nodes.length; end++){
					pos = nodes[end].getPartOfSpeechTag();
					if(!pos.startsWith(head_pos)) break;
				}
				chunks.add(new Chunk<>(head_pos, (N[])Arrays.copyOfRange(nodes, start, end)));
				start = end;
			}
		}
		return chunks;
	}	
	
}
