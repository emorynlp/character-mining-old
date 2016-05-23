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
package edu.emory.mathcs.nlp.mining.character.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 8, 2016
 */
public class Scene implements Serializable, Iterable<Utterance>, Comparable<Scene>{
	private static final long serialVersionUID = 8522836696220388753L;
	
	private int scene_id;
	private List<Utterance> utterances;
	
	public Scene(int scene_id){
		this.scene_id = scene_id;
		utterances = new ArrayList<>();
	}
	
	public int getID(){
		return scene_id;
	}
	
	public int size(){
		return utterances.size();
	}
	
	public Utterance getUtterance(int utterance_id){
		if(utterance_id >= 0 && utterance_id < utterances.size())
			return utterances.get(utterance_id);
		return null;
	}
	
	public List<Utterance> getUtterances(){
		return utterances;
	}

	public int setID(int scene_id){
		return this.scene_id = scene_id;
	}
	
	public void addUtterance(Utterance utterance){
		utterances.add(utterance);
	}
	
	@Override
	public int compareTo(Scene o) {
		return getID() - o.getID();
	}

	@Override
	public Iterator<Utterance> iterator() {
		return new Iterator<Utterance>() {
			int i = 0, size = utterances.size();
			
			@Override
			public Utterance next() {
				return utterances.get(i++);
			}
			
			@Override
			public boolean hasNext() {
				return i < size;
			}
		};
	}

}
