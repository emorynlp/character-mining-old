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
package edu.emory.mathcs.nlp.common.dictionary;

import java.io.InputStream;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 30, 2016
 */
public class PersonalCommonNoun {
	private Set<String> nouns;
	
	public PersonalCommonNoun(){
		String path = Dictionary.CHARACTER_ROOT + "PersonNouns.txt";
		init(IOUtils.getInputStreamsFromResource(path));
	}
	
	public PersonalCommonNoun(InputStream in_stream){
		init(in_stream);
	}
	
	public void init(InputStream in_stream){
		nouns = DSUtils.createStringHashSet(in_stream, true, true);
	}
	
	public boolean isPersonalCommonNoun(String word){
		return nouns.contains(word) ||
			nouns.contains(StringUtils.toLowerCaseSimplifiedForm(word));
	}
}
