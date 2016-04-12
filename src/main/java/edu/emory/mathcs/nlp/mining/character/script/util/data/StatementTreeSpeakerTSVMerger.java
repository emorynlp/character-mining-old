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
package edu.emory.mathcs.nlp.mining.character.script.util.data;

import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 30, 2016
 */
public class StatementTreeSpeakerTSVMerger {
	private final String SPEAKER_KEY = "speaker";
	
	public Scene merge(Scene scene){
		for(Utterance utterance : scene){
			for(StatementNode[] nodes : utterance.getStatementTrees()){
				if(utterance.getSpeaker() != null)
					nodes[1].putFeat(SPEAKER_KEY, utterance.getSpeaker());
			}
		}
		return scene;
	}
}
