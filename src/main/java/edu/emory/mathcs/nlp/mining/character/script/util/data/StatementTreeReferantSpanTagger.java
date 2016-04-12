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
package edu.emory.mathcs.nlp.mining.character.script.util.data;

import java.util.List;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.component.template.util.BILOU;
import edu.emory.mathcs.nlp.mining.character.chunk.Chunk;
import edu.emory.mathcs.nlp.mining.character.chunk.EnglighReferentChunker;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 9, 2016
 */
public class StatementTreeReferantSpanTagger {
	public static final String
		unknowB = "B-?", unknowI = "I-?", unknowL = "L-?", O = BILOU.O.toString(), unknowU = "U-?";
	
	private EnglighReferentChunker chunker = new EnglighReferentChunker(DSUtils.createSet("PERSON"));
	
	public void markSpan(StatementNode[] nodes){
		List<Chunk> chunks = chunker.getChunks(nodes);
		
		int i, len; StatementNode[] span;
		
		for(Chunk chunk : chunks){	
			span = (StatementNode[])chunk.getNodes();
			len = span.length;
			
			if(len > 0 && span[0].getReferantLabel().equals(O)){
				if(len== 1) span[0].setReferantLabel(unknowU);
				else{
					span[0].setReferantLabel(unknowB);
					for(i = 1; i < len-1; i++)
						span[i].setReferantLabel(unknowI);
					span[len-1].setReferantLabel(unknowL);
				}
			}
		}
	}
}
