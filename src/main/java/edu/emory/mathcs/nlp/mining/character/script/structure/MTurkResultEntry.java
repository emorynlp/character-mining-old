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
package edu.emory.mathcs.nlp.mining.character.script.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Feb 3, 2016
 */
public class MTurkResultEntry {
	private Map<String, String> m_fields;
	
	public MTurkResultEntry(){
		m_fields = new HashMap<>();
	}
	
	public String getField(String key){
		return m_fields.get(key);
	}
	
	public String[] getFieldNames(){
		Set<String> set = m_fields.keySet();
		return  set.toArray(new String[set.size()]);
	}
	
	public void addField(String key, String value){
		m_fields.put(key, value);
	}
	
	public void addFields(String[] keys, String[] values){
		int len = Math.min(keys.length, values.length);
		
		for(int i = 0; i < len; i++)
			m_fields.put(keys[i], values[i]);
	}
}
