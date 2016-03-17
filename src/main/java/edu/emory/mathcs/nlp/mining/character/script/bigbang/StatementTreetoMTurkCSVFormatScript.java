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

import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.script.mturk.annotation.StatementTreetoMTurkCSVFormatter;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 16, 2016
 */
public class StatementTreetoMTurkCSVFormatScript {
	public static final Set<String>
		IGN_WORDS = DSUtils.createSet("i", "me", "my", "mine", "myself", "it", "its", "itself", "we", "us", "our", "ourselves", "ours", "they", "them", "their", "themselves", "theirs"),
		IGN_SPEAKERS = DSUtils.createSet("All", "Both", null),
		MAIN_CHARACTER = DSUtils.createSet("Sheldon", "Leonard", "Penny", "Howard", "Raj");
	
	public static final int 
		SN_OFFSET			= 2,
		Q_LB 				= 8, 
		Q_UB 				= 50,
		EXTRA_CHARACTER_NUM = 20-MAIN_CHARACTER.size();

	public static final String
		I_DIR  = "/Users/HenryChen/Desktop/BigBang/season1/span_tagged",
		T_EXT  = ".dep",
		S_EXT  = ".spk",
		O_PATH = "/Users/HenryChen/Desktop/BigBang/season1/mturk_bigbang_s1.csv";
	
	public static void main(String[] args) throws Exception{
		SceneTSVReader reader = SceneTSVReader.DEFAULT();
		StatementTreetoMTurkCSVFormatter formatter = 
			new StatementTreetoMTurkCSVFormatter(SN_OFFSET, Q_LB, Q_UB, EXTRA_CHARACTER_NUM, IGN_WORDS, IGN_SPEAKERS, MAIN_CHARACTER);
		formatter.formatScenes(reader, I_DIR, T_EXT, S_EXT, O_PATH);
	}
}
