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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.chunk.Chunk;
import edu.emory.mathcs.nlp.mining.character.chunk.EnglighReferentChunker;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader_Single;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 8, 2016
 */
public class MentionDetectionAnalysis {
	public static final String[] season_paths = {
		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/BigBang/season1/temp",
		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/Friends/season1/temp",
		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/Friends/season2/temp"
	};
	public static final String EXT = ".dep";
	public static final Set<String> NON_PER_PRP = new HashSet<>(Arrays.asList("it", "its", "itself"));
	
	public static void main(String[] args){
		EnglighReferentChunker mention_chunker = new EnglighReferentChunker(DSUtils.createSet("PERSON"));
		
		List<String> file_paths; Scene scene;
		StatementNode node; int c_error; char bilou; 
		SceneTSVReader_Single reader = SceneTSVReader_Single.DEFAULT();
		
		for(String season_path : season_paths){
			c_error = 0; System.out.println(season_path);
			file_paths = FileUtils.getFileList(season_path, EXT);
			
			for(String file_path : file_paths){
				scene = reader.readScene(IOUtils.createFileInputStream(file_path));
				
				for(Utterance utterance : scene){
					for(StatementNode[] nodes : utterance.getStatementTrees()){
						for(Chunk chunk : mention_chunker.getChunks(nodes)){
							node = (StatementNode)chunk.getHeadNode();
							bilou = node.getReferantLabel().charAt(0);
							if(!NON_PER_PRP.contains(node.getWordFormSimplifiedLowercase()) && bilou == 'O')
								c_error++;
						}
					}
				}
			}
			System.out.println("Error: " + c_error);
		}
	}
}
