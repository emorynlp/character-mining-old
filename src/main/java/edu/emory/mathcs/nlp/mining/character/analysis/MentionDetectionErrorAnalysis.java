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
package edu.emory.mathcs.nlp.mining.character.analysis;

import java.util.List;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.chunk.EnglighReferentChunker;
import edu.emory.mathcs.nlp.mining.character.chunk.EnglishNamedEntityChunker;
import edu.emory.mathcs.nlp.mining.character.chunk.EnglishNounChunker;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader_Single;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 8, 2016
 */
public class MentionDetectionErrorAnalysis {
	public static final String[] season_paths = {
		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/BigBang/season1/original",
		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/Friends/season1/original",
		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/Friends/season2/original"
	};
	public static final String EXT = ".dep";
	
	public static void main(String[] args){
		EnglishNounChunker nn_chunker = new EnglishNounChunker();
		EnglishNamedEntityChunker ne_chunker = new EnglishNamedEntityChunker(DSUtils.createSet("PERSON"));
		EnglighReferentChunker mention_chunker = new EnglighReferentChunker(DSUtils.createSet("PERSON"));
		
		char bilou; StatementNode node;
		int i, c_nn, c_ne, c_mention, c_annotation, c_word, c_sentence, c_scene;
		List<String> file_paths; Scene scene;
		SceneTSVReader_Single reader = SceneTSVReader_Single.DEFAULT();
		
		for(String season_path : season_paths){
			c_word = c_sentence = c_scene = 0;
			c_nn = c_ne = c_mention = c_annotation = 0;
			file_paths = FileUtils.getFileList(season_path, EXT);
			
			for(String file_path : file_paths){
				scene = reader.readScene(IOUtils.createFileInputStream(file_path));
				c_scene++;
				
				for(Utterance utterance : scene){
					for(StatementNode[] nodes : utterance.getStatementTrees()){
						c_sentence++;
						c_nn += nn_chunker.getChunks(nodes).size();
						c_ne += ne_chunker.getChunks(nodes).size();
						c_mention += mention_chunker.getChunks(nodes).size();
						
						for(i = 1; i < nodes.length; i++){
							node = nodes[i]; c_word++;
							bilou = node.getReferantLabel().charAt(0);
							if(bilou == 'B' || bilou == 'U') c_annotation++;
						}
					}
				}
			}
			
			System.out.println("Result for " + season_path);
			System.out.println(String.format("NN: %d, NE: %d, MEN: %d, ANN: %d | SCN: %d, SENT: %d, WD: %d", 
				c_nn, c_ne, c_mention, c_annotation, c_scene, c_sentence, c_word));
		}
	}
}
