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
package edu.emory.mathcs.nlp.mining.character.script.friends;

import java.util.List;
import java.util.Map;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.script.mturk.release.MTurkAdjudicationToTreeMerger;
import edu.emory.mathcs.nlp.mining.character.script.mturk.result.MTurkAnnoataionExtractor;
import edu.emory.mathcs.nlp.mining.character.script.mturk.result.MTurkResultParser;
import edu.emory.mathcs.nlp.mining.character.script.structure.MTurkResultEntry;
import edu.emory.mathcs.nlp.mining.character.structure.Season;
import edu.emory.mathcs.nlp.mining.character.util.DataExportUtil;
import edu.emory.mathcs.nlp.mining.character.util.DataLoaderUtil;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 20, 2016
 */
public class MTurkAjudicationToStatementTreeMergeScript {
	public static final int NUM_QUESTION 	= 50;
	
	public static final String
		RESULT_CSV = "/Users/HenryChen/Desktop/CharacterDetection/MTurk_out/friends/Friends_s2_adjudication_result.csv",
		I_DIR  = "/Users/HenryChen/Desktop/TV_Show_Tanscripts/Friends/season2/augmented",
		O_DIR  = "/Users/HenryChen/Desktop/TV_Show_Tanscripts/Friends/season2/augmented_new",
		T_EXT  = ".dep", S_EXT  = ".spk";
	
	public static void main(String[] args) throws Exception{
		MTurkResultParser parser = new MTurkResultParser(NUM_QUESTION);
		List<MTurkResultEntry> entries = parser.parseMTurkResult(IOUtils.createFileInputStream(RESULT_CSV));
		
		MTurkAnnoataionExtractor extractor = new MTurkAnnoataionExtractor(NUM_QUESTION);
		Map<String, List<Pair<String, String>>> annotations = extractor.extractAdjudication(entries);
		
		SceneTSVReader reader = SceneTSVReader.DEFAULT();
		List<Season> seasons = DataLoaderUtil.loadFromFiles(I_DIR, T_EXT, S_EXT, reader);
		
		MTurkAdjudicationToTreeMerger merger = new MTurkAdjudicationToTreeMerger();
		merger.merge(seasons, annotations); 
		
		DataExportUtil.exportToFiles(seasons, O_DIR, T_EXT, S_EXT);
	}
}
