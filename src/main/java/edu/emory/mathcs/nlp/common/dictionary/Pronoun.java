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

import java.util.HashSet;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 30, 2016
 */
public class Pronoun {
	private Pronoun() { }
	
	public static final Set<String>
		FIRST_PERSON_SINGULAR 	= DSUtils.createSet("i", "im", "me", "my", "mine", "myself"),
		SECOND_PERSON_SINGULAR	= DSUtils.createSet("you", "your", "yours", "yourselves"),
		THIRD_PERSON_SINGULAR	= DSUtils.createSet("he", "his", "him", "himself", "she", "her", "hers", "herself"),
		
		FIRST_PERSON_PLURAL 	= DSUtils.createSet("we", "us", "our", "ourselves"),
		SECOND_PERSON_PLURAL 	= DSUtils.createSet("you", "your", "yours", "yourselves"),
		THIRD_PERSON_PLURAL		= DSUtils.createSet("they", "them", "their", "themselves", "theirs"),
		
		NONPERSON				= DSUtils.createSet("it", "its", "itself");
	
	public static Set<String> getPersonalPronouns(){
		Set<String> set = new HashSet<>();
		set.addAll(FIRST_PERSON_SINGULAR);		set.addAll(FIRST_PERSON_PLURAL);
		set.addAll(SECOND_PERSON_SINGULAR);		set.addAll(SECOND_PERSON_PLURAL);
		set.addAll(THIRD_PERSON_SINGULAR);		set.addAll(THIRD_PERSON_PLURAL);
		return set;
	}
	
}
