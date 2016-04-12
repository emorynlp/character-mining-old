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
package edu.emory.mathcs.nlp.mining.character.script.data;

import java.io.File;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.script.util.data.StatementTreeSpeakerTSVMerger;
import edu.emory.mathcs.nlp.mining.character.structure.Episode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.Season;
import edu.emory.mathcs.nlp.mining.character.util.DataExportUtil;
import edu.emory.mathcs.nlp.mining.character.util.DataLoaderUtil;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 30, 2016
 */
public class StatementTreeSpeakerTSVMergeScript {
	public static final String
		I_DIR  = "/Users/HenryChen/Desktop/TV_Show_Tanscripts/Friends/season2/annotated",
		O_DIR  = "/Users/HenryChen/Desktop/TV_Show_Tanscripts/Friends/season2_singleFile",
		T_EXT  = ".dep", S_EXT  = ".spk";
	
	public static void main(String[] args) throws Exception{
		SceneTSVReader reader = SceneTSVReader.DEFAULT();
		StatementTreeSpeakerTSVMerger merger = new StatementTreeSpeakerTSVMerger();
		List<Season> seasons = DataLoaderUtil.loadFromFiles(I_DIR, T_EXT, S_EXT, reader);
		
		for(Season season : seasons){
			for(Episode episode : season){
				for(Scene scene : episode)
					merger.merge(scene);
			}
		}

		DataExportUtil.exportToFiles(seasons, O_DIR, T_EXT, S_EXT);
		for(String file : FileUtils.getFileList(O_DIR, S_EXT))
			new File(file).delete();
	}
}
