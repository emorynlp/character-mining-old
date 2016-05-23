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
public class Season implements Serializable, Comparable<Season>, Iterable<Episode>{
	private static final long serialVersionUID = 7067132737364878689L;
	
	private int season_id;
	private TreeMap<Integer, Episode> episodes;
	
	public Season(int season_id) {
		this.season_id = season_id;
		episodes = new TreeMap<>();
	}
	
	public int getID(){
		return season_id;
	}
	
	public int size(){
		return episodes.size();
	}
	
	public Episode getEpisode(int episode_id){
		return episodes.getOrDefault(episode_id, null);
	}
	
	public Collection<Episode> getEpisodes(){
		return episodes.values();
	}
	
	public int setID(int season_id){
		return this.season_id = season_id;
	}
	
	public Episode addEpisode(Episode episode){
		return addEpisode(episode.getID(), episode);
	}
	
	public Episode addEpisode(int episode_id, Episode episode){
		episodes.put(episode.setID(episode_id), episode);
		return episode;
	}

	@Override
	public int compareTo(Season o) {
		return getID() - o.getID();
	}

	@Override
	public Iterator<Episode> iterator() {
		return episodes.values().iterator();
	}

}
