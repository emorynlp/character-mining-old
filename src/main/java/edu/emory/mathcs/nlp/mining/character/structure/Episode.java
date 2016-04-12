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
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 8, 2016
 */
public class Episode implements Serializable, Comparable<Episode>, Iterable<Scene>{
	private static final long serialVersionUID = 8771622582938869922L;
	
	private int episode_id;
	private TreeMap<Integer, Scene> scenes;
	
	public Episode(int episode_id) {
		this.episode_id = episode_id;
		scenes = new TreeMap<>();
	}
	
	public int getID(){
		return episode_id;
	}
	
	public int size(){
		return scenes.size();
	}
	
	public int getFirstSceneId(){
		return scenes.firstKey();
	}
	
	public int getLastSceneId(){
		return scenes.lastKey();
	}
	
	public Scene getScene(int scene_id){
		return scenes.getOrDefault(scene_id, null);
	}
	
	public Collection<Scene> getScenes(){
		return scenes.values();
	}
	
	public int setID(int episode_id){
		return this.episode_id = episode_id;
	}
	
	public Scene addScene(Scene scene){
		return addScene(scene.getID(), scene);
	}
	
	public Scene addScene(int scene_id, Scene scene){
		scenes.put(scene.setID(scene_id), scene);
		return scene;
	}

	@Override
	public int compareTo(Episode o) {
		return getID() - o.getID();
	}

	@Override
	public Iterator<Scene> iterator() {
		return scenes.values().iterator();
	}

}
