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

import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 10, 2016
 */
public class EnglishReferantChunkerTest {
	public final String
		DEP_PATH = "src/test/resource/test.dep",
		SPK_PATH = "src/test/resource/test.spk";
	
	public Set<String> target_tags = DSUtils.createSet("PERSON");
	
	@Test
	public void testChunker(){
		Scene scene = getScene(); List<Chunk> chunks;
		EnglighReferentChunker chunker = new EnglighReferentChunker(target_tags);
		
		for(Utterance u : scene){
			for(StatementNode[] tree : u.getStatementTrees()){
				chunks = chunker.getChunks(tree);
				
				for(Chunk chunk : chunks){
					System.out.println(chunk.getChunkTag());
					for(NLPNode node : chunk) System.out.println(node);
					System.out.println();
				}
			}
		}
	}
	
	public Scene getScene(){
		SceneTSVReader reader = new SceneTSVReader();
		reader.initFieldIndices(3, 4, 5, 10, 6, 7, 8, 9, 0, 1, 2, 11);
		return reader.fromTSV(IOUtils.createFileInputStream(DEP_PATH), IOUtils.createFileInputStream(SPK_PATH));
	}
}
