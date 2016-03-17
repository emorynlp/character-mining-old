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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 9, 2016
 */
public class EnglishNamedEntityChunker extends Chucker{
	private Set<String> target_tags;
	
	public EnglishNamedEntityChunker(Set<String> target_tags){
		this.target_tags = target_tags;
	}

	@Override
	public List<Chunk> getChunks(NLPNode[] nodes) {
		List<Chunk> chunks = new ArrayList<>();
		
		char bilou; String tag;
		int start, end; NLPNode node; 

		for(start = 1; start < nodes.length; start++){
			node = nodes[start];
			bilou = node.getNamedEntityTag().charAt(0);
			
			switch (bilou) {
			case 'B':
				tag = node.getNamedEntityTag().substring(2);
				if(target_tags.contains(tag)){
					for(end = start+1; end < nodes.length; end++){
						bilou = nodes[end].getNamedEntityTag().charAt(0);
						if(bilou == 'L'){ 			break;	}
						if(bilou == 'O'){ 	end--; 	break;	}
					}
					chunks.add(new Chunk(tag, Arrays.copyOfRange(nodes, start, end+1)));
					start = end;					
				}
				break;
			case 'U':
				tag = node.getNamedEntityTag().substring(2);
				if(target_tags.contains(tag))
					chunks.add(new Chunk(tag, Arrays.copyOfRange(nodes, start, start+1)));
				break;
			}
		}
		
		return chunks;
	}	
	
}
