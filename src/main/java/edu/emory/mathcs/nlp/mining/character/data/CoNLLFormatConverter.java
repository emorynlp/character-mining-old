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

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.nlp.common.util.CharTokenizer;
import edu.emory.mathcs.nlp.common.util.DSUtils;
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
public class CoNLLFormatConverter {
	public static final String 
		DEP_DIR = "/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/Friends/season1_singleFile",
		IN_SPK  = "/Users/HenryChen/Google Drive/Character Mining/TV_Show_Tanscripts/Friends/season1.speakers",
		DEP_EXT = ".dep",
		OUT_EXT = "conll";
	
	public static void main(String[] args){
		SceneTSVReader_Single reader = SceneTSVReader_Single.DEFAULT();
		Map<String, String> m_speakers = constructSpeakerMap(IOUtils.createFileInputStream(IN_SPK));
		List<String> dep_files = FileUtils.getFileList(DEP_DIR, DEP_EXT);
		
		
		StatementNode node; PrintWriter writer;
		String out_path, doc_name, entry, ne_label, coref_label, speaker; 
		Scene scene; int i, j, size = dep_files.size();
		for(i = 0; i < size; i++){
			scene = reader.readScene(IOUtils.createFileInputStream(dep_files.get(0)));
			
			out_path = FileUtils.replaceExtension(dep_files.get(0), OUT_EXT);
			doc_name = out_path.substring(out_path.lastIndexOf('/')+1, out_path.lastIndexOf('.'));
			writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_path));
			
			for(Utterance utterance : scene){
				speaker = (utterance.getSpeaker() != null)? utterance.getSpeaker() : "__";
				for(StatementNode[] nodes : utterance.getStatementTrees()){
					for(j = 1; j < nodes.length; j++){
						node = nodes[j];
						
						switch(node.getNamedEntityTag().charAt(0)){
						case 'B':	ne_label = String.format("(%s*", node.getNamedEntityTag().substring(2)); break;
						case 'U':	ne_label = String.format("(%s)", node.getNamedEntityTag().substring(2)); break;
						case 'L':	ne_label = "*)"; break;
						default: ne_label = "*"; 
						}
						switch(node.getReferantLabel().charAt(0)) {
						case 'B':	coref_label = String.format( "(%s", m_speakers.get(node.getReferantLabel().substring(2))); break;
						case 'L':	coref_label = String.format( "%s)", m_speakers.get(node.getReferantLabel().substring(2))); break;
						case 'U':	coref_label = String.format("(%s)", m_speakers.get(node.getReferantLabel().substring(2))); break;
						default: coref_label = "-";
						}
						
						entry = String.format("%s% 10s% 10s %10s %10s %10s %11s%11s%11s%11s%11s%11s", 
							doc_name, Integer.toString(i), Integer.toString(j-1), node.getWordForm(), node.getPartOfSpeechTag(), 
							"CTREE", node.getLemma(), "-", "-", speaker, ne_label, coref_label);
						writer.println(entry);
					}
				}
				writer.println();
			}
			break;
		}
	}
	
	public static Map<String, String> constructSpeakerMap(InputStream in){
		return DSUtils.createStringHashMap(in, new CharTokenizer('\t'));
	}
}
