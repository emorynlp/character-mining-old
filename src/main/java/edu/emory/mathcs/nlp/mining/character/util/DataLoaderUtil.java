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
package edu.emory.mathcs.nlp.mining.character.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.reader.SceneTSVReader;
import edu.emory.mathcs.nlp.mining.character.structure.Episode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.Season;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 9, 2016
 */
public class DataLoaderUtil {
	private DataLoaderUtil(){ }
	
	public static int[] getEpisodeMetaDate(String filename){
		int idx = filename.lastIndexOf('/');
		String fileName = (idx > 0)? filename.substring(idx+1) : filename;
		
		if(fileName.length() < 4)
			throw new IllegalArgumentException("Invalid length for file name.");
		
		int[] metadata = new int[2];
		metadata[0] = Integer.parseInt(fileName.substring(0,2));
		metadata[1] = Integer.parseInt(fileName.substring(2,4));
		return metadata;
	}
	
	public static int[] getSceneMetaDate(String filename){
		int idx = filename.lastIndexOf('/');
		String fileName = (idx > 0)? filename.substring(idx+1) : filename;
		
		if(fileName.length() < 6)
			throw new IllegalArgumentException("Invalid length for file name.");
		
		int[] metadata = new int[3];
		metadata[0] = Integer.parseInt(fileName.substring(0,2));
		metadata[1] = Integer.parseInt(fileName.substring(2,4));
		metadata[2] = Integer.parseInt(fileName.substring(4,6));
		return metadata;
	}
	
	public static List<Season> loadFromFiles(String in_path, String in_tree_ext, String in_speaker_ext, SceneTSVReader reader) throws Exception{
		List<String> tree_file_paths = FileUtils.getFileList(in_path, in_tree_ext),
			speaker_file_paths = FileUtils.getFileList(in_path, in_speaker_ext);
		if(tree_file_paths.size() != speaker_file_paths.size())
			throw new Exception("Mismath of tree and speaker files.");
		
		String base; int[] metadata;
		
		// Construct an index map for speaker files
		Map<String, String> m_speaker_file_paths = new HashMap<>();
		for(String speaker_file_path : speaker_file_paths){
			metadata = DataLoaderUtil.getSceneMetaDate(speaker_file_path);
			base = String.format("%02d%02d%02d", metadata[0], metadata[1], metadata[2]);
			m_speaker_file_paths.put(base, speaker_file_path);
		}
			
		String speaker_file_path;
		InputStream tree_in, speaker_in;
		Season season; Episode episode; Scene scene;
		TreeMap<Integer, Season> m_seasons = new TreeMap<>();
		
		for(String tree_file_path : tree_file_paths){
			metadata = DataLoaderUtil.getSceneMetaDate(tree_file_path);
			base = String.format("%02d%02d%02d", metadata[0], metadata[1], metadata[2]);
			speaker_file_path = m_speaker_file_paths.get(base);
			
			season = m_seasons.getOrDefault(metadata[0], new Season(metadata[0]));
			if(season.size() == 0) m_seasons.put(metadata[0], season);
			
			episode = season.getEpisode(metadata[1]);
			if(episode == null) episode = season.addEpisode(new Episode(metadata[1]));
			
			if(speaker_file_path != null){
				tree_in = IOUtils.createFileInputStream(tree_file_path);
				speaker_in = IOUtils.createFileInputStream(speaker_file_path);
				
				scene = reader.fromTSV(tree_in, speaker_in);
				scene.setID(metadata[2]); episode.addScene(scene);
			}
		}
		return new ArrayList<>(m_seasons.values());
	}
}
