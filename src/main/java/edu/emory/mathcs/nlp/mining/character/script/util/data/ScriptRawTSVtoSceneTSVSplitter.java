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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.common.util.StringUtils;
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
		
		String line, outPath; int[] metadata; int i, sceneId;
		BufferedReader reader; PrintWriter writer = null;
		String[] fields;
		
		for(String filePath : l_filePaths){
			reader = IOUtils.createBufferedReader(filePath); sceneId = 1; 
			metadata = DataLoaderUtil.getEpisodeMetaDate(filePath);
			
			while( (line = reader.readLine()) != null){
				if(line.startsWith(SCENE_DELIM)){
					if(writer != null) 	writer.close();
					
					outPath = String.format("%s%02d%02d%02d%s", out_path, metadata[0], metadata[1], sceneId++, out_ext); 					
					writer = new PrintWriter(IOUtils.createBufferedPrintStream(outPath));
				}
				else {
					fields = Splitter.splitTabs(line);
					fields[0] = formatSpekaer(fields[0]);
					
					for(i = 1; i < fields.length; i++)
						if(fields[i].isEmpty()) fields[i] = StringConst.UNDERSCORE;
					
					switch(fields.length){
					case 0: writer.println("_\t_\t_"); 											break;
					case 1: writer.println(String.format("%s\t_\t_", fields[0])); 				break;
					case 2: writer.println(String.format("%s\t%s\t_", fields[0], fields[1]));	break;
					default: writer.println(Joiner.join(fields, StringConst.TAB));
					}
				}
			}
			if(writer != null) 
				writer.close();
			reader.close();
		}
	}
	
	public static String formatSpekaer(String field){
		String[] words = Splitter.splitSpace(field);
		for(int i = 0; i < words.length; i++){
			if(words[i].length() == 1)
				words[i] = StringUtils.toUpperCase(words[i]);
			else if(words[i].length() > 1)
				words[i] = Character.toUpperCase(words[i].charAt(0)) + StringUtils.toLowerCase(words[i].substring(1));
		}
		return Joiner.join(words, StringConst.SPACE);
	}
}
