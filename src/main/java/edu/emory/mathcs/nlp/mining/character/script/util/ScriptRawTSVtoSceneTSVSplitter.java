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
package edu.emory.mathcs.nlp.mining.character.script.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.mining.character.util.DataLoaderUtil;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Feb 12, 2016
 */
public class ScriptRawTSVtoSceneTSVSplitter {
	public static final String SCENE_DELIM = "##";
	
	public static void splitScene(String in_path, String in_ext, String out_path, String out_ext) throws IOException{
		List<String> l_filePaths = FileUtils.getFileList(in_path, in_ext, false);
		if(out_path.charAt(out_path.length()-1) != '/') out_path += "/";
		
		String line, outPath; int[] metadata; int sceneId;
		BufferedReader reader; PrintWriter writer = null;
		
		for(String filePath : l_filePaths){
			reader = IOUtils.createBufferedReader(filePath); sceneId = 1; 
			metadata = DataLoaderUtil.getEpisodeMetaDate(filePath);
			
			while( (line = reader.readLine()) != null){
				if(line.startsWith(SCENE_DELIM)){
					if(writer != null) 	writer.close();
					
					outPath = String.format("%s%02d%02d%02d%s", out_path, metadata[0], metadata[1], sceneId++, out_ext); 					
					writer = new PrintWriter(IOUtils.createBufferedPrintStream(outPath));
				}
				else 	writer.println(line);
			}
			if(writer != null) 
				writer.close();
			reader.close();
		}
	}
}
