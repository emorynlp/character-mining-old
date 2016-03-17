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
package edu.emory.mathcs.nlp.mining.character.script.util;

import java.io.PrintWriter;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;
import edu.emory.mathcs.nlp.mining.character.util.DataLoaderUtil;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 10, 2016
 */
public class NLP4JtoStatementTreeProcessor {
	private SceneTSVReader reader;
	private StatementTreeReferantSpanTagger tagger;
	
	public NLP4JtoStatementTreeProcessor(){
		reader = new SceneTSVReader();
		reader.initFieldIndices(3, 4, 5, 10, 6, 7, 8, 9, 0, 1, 2, -1);
		tagger = new StatementTreeReferantSpanTagger();
	}
	
	public void process(String in_path, String in_tree_ext, String in_speaker_ext, String out_path, String out_ext) throws Exception{
		List<String> l_tree_paths 	 = FileUtils.getFileList(in_path, in_tree_ext, false);
		List<String> l_speaker_paths = FileUtils.getFileList(in_path, in_speaker_ext, false);
		if(l_speaker_paths.size() != l_tree_paths.size())
			throw new Exception(String.format("Mismatch of input file count (%s, %s)", in_tree_ext, in_speaker_ext));
		if(out_path.charAt(out_path.length()-1) != '/') out_path += "/";
		
		int i, j, sentenceId;
		Scene scene; String outPath; int[] metadata; PrintWriter writer;
		for(i = 0; i < l_tree_paths.size(); i++){
			scene = reader.fromTSV(
				IOUtils.createFileInputStream(l_tree_paths.get(i)), 
				IOUtils.createFileInputStream(l_speaker_paths.get(i)));
			metadata = DataLoaderUtil.getSceneMetaDate(FileUtils.getBaseName(l_tree_paths.get(i)));
			
			outPath = String.format("%s%02d%02d%02d%s", out_path, metadata[0], metadata[1], metadata[2], out_ext);
			writer = new PrintWriter(IOUtils.createBufferedPrintStream(outPath));
			
			sentenceId = 0;
			for(Utterance utterance : scene)
				for(StatementNode[] nodes : utterance.getStatementTrees()){
					tagger.markSpan(nodes);
					
					for(j = 1; j < nodes.length; j++)
						writer.println(String.format("%d\t%d\t%s", utterance.getID(), sentenceId, nodes[j].toString()));
					writer.println(); sentenceId++;
				}
			writer.close();
		}
		
	}
}
