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
package edu.emory.mathcs.nlp.mining.character.data;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader_Single;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Apr 11, 2016
 */
public class SpeakerMapGenerator {
	public static final String 
		IN_DIR = "/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/BigBang/season1_singleFile",
		IN_DEP = ".dep",
		OUT_SPK = "/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/BigBang/season1.speakers";
	
	public static void main(String[] args){
		SceneTSVReader_Single reader = SceneTSVReader_Single.DEFAULT();
		
		Set<String> speakers = new HashSet<>(); Scene scene;
		for(String file_path : FileUtils.getFileList(IN_DIR, IN_DEP)){
			scene = reader.readScene(IOUtils.createFileInputStream(file_path));
			
			for(Utterance utterance : scene){
				if(utterance.getSpeaker() != null) speakers.add(utterance.getSpeaker());
				for(StatementNode[] nodes : utterance.getStatementTrees()){
					for(StatementNode node : nodes){
						if(node.getReferantLabel().charAt(0) != 'O')
							speakers.add(node.getReferantLabel().substring(2));
					}
				}
			}
		}
		PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(OUT_SPK));
		int i = 1; for(String speaker : speakers) writer.println(speaker + "\t" + i++);
		writer.close();
	}
}
