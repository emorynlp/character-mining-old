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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.constant.StringConst;
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
public class TokenizationExport {
	public static final String[] season_paths = {
//		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/BigBang/season1_singleFile",
		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/Friends/season1_singleFile"
//		"/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/Friends/season2_singleFile"
	};
	public static final String EXT = ".dep";
	
	public static void main(String[] args) throws IOException{
		SceneTSVReader_Single reader = SceneTSVReader_Single.DEFAULT();
		
		int i; Scene scene; List<String> file_paths;
		String out_path; PrintWriter writer; StringJoiner joiner;
		for(String season_path : season_paths){
			file_paths = FileUtils.getFileList(season_path, EXT);
			for(String file_path : file_paths){
				scene = reader.readScene(IOUtils.createFileInputStream(file_path));
				out_path = FileUtils.replaceExtension(file_path, "tknz");
				writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_path));
				
				for(Utterance utterance : scene){
					for(StatementNode[] nodes : utterance.getStatementTrees()){
						joiner = new StringJoiner(StringConst.SPACE);
						for(i = 1; i < nodes.length; i++)  joiner.add(nodes[i].getWordForm());
						writer.println(joiner.toString());
					}
				}
				writer.close();
			}
		}
	}
}
