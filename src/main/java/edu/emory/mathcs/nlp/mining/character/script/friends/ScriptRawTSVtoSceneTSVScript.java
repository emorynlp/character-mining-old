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

import java.io.IOException;

import edu.emory.mathcs.nlp.mining.character.script.util.data.ScriptRawTSVtoSceneTSVSplitter;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 9, 2016
 */
public class ScriptRawTSVtoSceneTSVScript {
	public static final String
		IN_DIR  = "/Users/HenryChen/Desktop/TV_Show_Tanscripts/Friends/raw/season2", 			IN_EXT   = ".tsv",
		OUT_DIR = "/Users/HenryChen/Desktop/TV_Show_Tanscripts/Friends/raw/season2/scenes",		OUT_EXT  = ".tsv";
	
	public static void main(String[] args) throws IOException{
		ScriptRawTSVtoSceneTSVSplitter.splitScene(IN_DIR, IN_EXT, OUT_DIR, OUT_EXT);
	}
}
