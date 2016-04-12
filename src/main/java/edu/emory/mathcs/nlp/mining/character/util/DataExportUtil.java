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
package edu.emory.mathcs.nlp.mining.character.util;

import java.io.PrintWriter;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.structure.Episode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.Season;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 21, 2016
 */
public class DataExportUtil {
	public static void exportToFiles(List<Season> seasons, String out_dir, String out_tree_ext, String out_speaker_ext){
		int i, uid, sid, seasonId, episodeId, sceneId;
		String out_tree_path, out_speaker_path, speaker; 
		PrintWriter tree_writer, speaker_writer;
		
		if(out_dir.charAt(out_dir.length()-1) != CharConst.FW_SLASH)
			out_dir += CharConst.FW_SLASH;
		
		for(Season season : seasons){
			seasonId = season.getID();
			for(Episode episode : season){
				episodeId = episode.getID();
				for(Scene scene : episode){
					sceneId = scene.getID();
					uid = sid = 0;
					
					out_tree_path = String.format("%s%02d%02d%02d%s", out_dir, seasonId, episodeId, sceneId, out_tree_ext);
					out_speaker_path = String.format("%s%02d%02d%02d%s", out_dir, seasonId, episodeId, sceneId, out_speaker_ext);
					tree_writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_tree_path));
					speaker_writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_speaker_path));
					
					for(Utterance utterance : scene){
						speaker = utterance.getSpeaker();
						if(speaker == null) speaker = SceneTSVReader.BLANK;
						for(StatementNode[] tree : utterance.getStatementTrees()){
							for(i = 1; i < tree.length; i++)
								tree_writer.println(String.format("%d\t%d\t%s", uid, sid, tree[i]));
							speaker_writer.println(String.format("%d\t%d\t%s", uid, sid, speaker));
							tree_writer.println(); sid++;
						}
						uid++;
					}
					tree_writer.close(); speaker_writer.close();
				}
			}
		}
	}
}
