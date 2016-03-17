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
package edu.emory.mathcs.nlp.mining.character.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 9, 2016
 */
public class SceneTSVReaderTest {
	public final String 
		IN_DIR = "/Users/HenryChen/Desktop/Friends/season1",
		SPK_EXT = ".spk", DEP_EXT = ".dep";
	
	@Test
	public void testTSV() {
		List<Pair<InputStream, InputStream>> files = getFilePair();
		SceneTSVReader reader = getReader();
		
		Scene scene;
		for(Pair<InputStream, InputStream> file : files){
			scene = reader.fromTSV(file.o1, file.o2);
			System.out.println(scene.size() + " utterance loaded.");
		}
	}
	
	public SceneTSVReader getReader(){
		SceneTSVReader reader = new SceneTSVReader();
		reader.initFieldIndices(3, 4, 5, 10, 6, 7, 8, 9, 0, 1, 2, 11);
		return reader;
	}
	
	public List<Pair<InputStream, InputStream>> getFilePair(){
		List<Pair<InputStream, InputStream>> list = new ArrayList<>();
		
		List<String> 
			spk = FileUtils.getFileList(IN_DIR, SPK_EXT),
			dep = FileUtils.getFileList(IN_DIR, DEP_EXT);
		
		if(spk.size() != dep.size())
			throw new IllegalArgumentException("Mismatch of file count (.dep, .spk).");
		
		for(int i = 0; i < spk.size(); i++){
			list.add(new Pair<InputStream, InputStream>(
					IOUtils.createFileInputStream(dep.get(i)),
					IOUtils.createFileInputStream(spk.get(i))));				
		}
		return list;
	}
}
