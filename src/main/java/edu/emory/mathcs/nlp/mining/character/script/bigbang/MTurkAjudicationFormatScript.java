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
package edu.emory.mathcs.nlp.mining.character.script.bigbang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.script.mturk.adjuidaction.MTurkAnnoataionToTreeMerger;
import edu.emory.mathcs.nlp.mining.character.script.mturk.adjuidaction.MergedTreetoMTurkAdjudicationCSVFormatter;
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
public class MTurkAjudicationFormatScript {
	public static final int
		NUM_TURKER 		= 2,
		NUM_QUESTION 	= 50,
		OPTION_COUNT 	= 2,
		EXTRA_CHARACTER_COUNT 	= 15,
		SN_OFFSET				= 2;
	
	public static final Set<String>
		IGN_SPEAKERS = DSUtils.createSet("All", "Both", null),
		MAIN_CHARACTER = DSUtils.createSet("Sheldon", "Leonard", "Penny", "Howard", "Raj");
	
	public static final String
		RESULT_CSV = "/Users/HenryChen/Desktop/CharacterDetection/MTurk_out/bigbang/BigBang_s1_b1_mturk_result.csv",
		I_DIR  = "/Users/HenryChen/Desktop/BigBang/season1/span_tagged",
		O_DIR  = "/Users/HenryChen/Desktop/BigBang/season1/annotated",
		T_EXT  = ".dep", S_EXT  = ".spk",
		O_CSV = "/Users/HenryChen/Desktop/CharacterDetection/MTurk_out/bigbang/BigBang_s1_adjudication.csv";
	
	public static void main(String[] args) throws Exception{
		MTurkResultParser parser = new MTurkResultParser(NUM_QUESTION);
		List<MTurkResultEntry> entries = parser.parseMTurkResult(IOUtils.createFileInputStream(RESULT_CSV));
		
		MTurkAnnoataionExtractor extractor = new MTurkAnnoataionExtractor(NUM_QUESTION);
		Map<String, List<Pair<String, String>>> annotations = extractor.extractAnnotation(entries);
		
		SceneTSVReader reader = SceneTSVReader.DEFAULT();
		List<Season> seasons = DataLoaderUtil.loadFromFiles(I_DIR, T_EXT, S_EXT, reader);
		
		MTurkAnnoataionToTreeMerger merger = new MTurkAnnoataionToTreeMerger();
		merger.merge(seasons, annotations); 
		
		DataExportUtil.exportToFiles(seasons, O_DIR, T_EXT, S_EXT);
		
		Set<String> set = annotations.keySet().stream().map(m->m.substring(0, 6)).collect(Collectors.toSet());
		List<String> metadata_selected = new ArrayList<>(set); Collections.sort(metadata_selected);
		
		MergedTreetoMTurkAdjudicationCSVFormatter formatter = new MergedTreetoMTurkAdjudicationCSVFormatter(SN_OFFSET, NUM_QUESTION, OPTION_COUNT, EXTRA_CHARACTER_COUNT, IGN_SPEAKERS, MAIN_CHARACTER);
		formatter.format(seasons, metadata_selected, O_CSV);
	}
}
