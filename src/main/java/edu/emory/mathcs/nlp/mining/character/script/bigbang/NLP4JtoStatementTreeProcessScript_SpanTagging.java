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

import edu.emory.mathcs.nlp.mining.character.script.util.NLP4JtoStatementTreeProcessor;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 10, 2016
 */
public class NLP4JtoStatementTreeProcessScript_SpanTagging {
	public static final String
		IN_DIR  = "/Users/HenryChen/Desktop/BigBang/season1/original", 			IN_TREE_EXT  = ".dep", 		IN_SPEAKER_EXT = ".spk",
		OUT_DIR = "/Users/HenryChen/Desktop/BigBang/season1/span_tagged",		OUT_EXT  = ".dep";
	
	public static void main(String[] args) throws Exception{
		NLP4JtoStatementTreeProcessor processor = new NLP4JtoStatementTreeProcessor();
		processor.process(IN_DIR, IN_TREE_EXT, IN_SPEAKER_EXT, OUT_DIR, OUT_EXT);
	}
}
