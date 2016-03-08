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
package edu.emory.mathcs.nlp.mining.character.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.template.util.BILOU;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 8, 2016
 */
public class SpeakerAnnotationReforattor {
	// Reformat the indexing of speaker annotation to match nlp4j tree (Due to switch from ClearNLP -> NLP4J)
	public static final String
		IN_DIR 			= "/Users/HenryChen/Desktop/Friends_Release",
		SPKANNO_EXT		= ".ann",
		CLEARNLP_EXT 	= ".dep",
		NLP4J_EXT 		= ".nlp4j",
		OUT_EXT			= "ann.updated";
	
	public static int u_id, s_id;
		
	public static void main(String[] args) throws IOException{
		List<String>
			l_annotation_path 	= FileUtils.getFileList(IN_DIR, SPKANNO_EXT),
			l_clearnlp_path		= FileUtils.getFileList(IN_DIR, CLEARNLP_EXT),
			l_nlp4j_path		= FileUtils.getFileList(IN_DIR, NLP4J_EXT);
		
		if(l_annotation_path.size() != l_clearnlp_path.size() || l_clearnlp_path.size() != l_nlp4j_path.size())
			throw new IllegalArgumentException("Mismatch of file counts.");
		
		int i, c, x, size = l_annotation_path.size(); 
		PrintWriter writer; String out_path, wordform;
		BufferedReader annotation_reader, clearnlp_reader, nlp4j_reader;
		List<String> l_annotations, l_clearnlp_wordform, l_nlp4j_wordform;
		List<Integer> l_idx = new ArrayList<>();
		
		for(i = 0; i < size; i++){
			annotation_reader 	= IOUtils.createBufferedReader(l_annotation_path.get(i));
			clearnlp_reader 	= IOUtils.createBufferedReader(l_clearnlp_path.get(i));
			nlp4j_reader		= IOUtils.createBufferedReader(l_nlp4j_path.get(i));
			
			out_path = FileUtils.replaceExtension(l_annotation_path.get(i), OUT_EXT);
			writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_path));
			
			while(  (l_annotations = getNextAnnotations(annotation_reader)) != null &&
					(l_clearnlp_wordform = getNextWordForms(clearnlp_reader)) != null &&
					(l_nlp4j_wordform = getNextWordForms(nlp4j_reader)) != null){
				
				if(l_clearnlp_wordform.size() != l_nlp4j_wordform.size()){
					l_idx.clear();
					for(c = 0; c < l_annotations.size(); c++)
						if(!l_annotations.get(c).equals(BILOU.O.toString()))
							l_idx.add(c);
					
					x = 0;
					for(int idx : l_idx){
						wordform = l_clearnlp_wordform.get(idx);
						for(; x < l_nlp4j_wordform.size(); x++)
							if(l_nlp4j_wordform.get(x).equals(wordform)) break;
							else writer.println(String.format("%d\t%d\t%d\t%s", u_id, s_id, x+1, BILOU.O.toString()));
						writer.println(String.format("%d\t%d\t%d\t%s", u_id, s_id, ++x, l_annotations.get(idx)));
					}
					for(; x < l_nlp4j_wordform.size(); x++)
						writer.println(String.format("%d\t%d\t%d\t%s", u_id, s_id, x+1, BILOU.O.toString()));
				}
				else{
					for(c = 0; c < l_annotations.size(); c++)
						writer.println(String.format("%d\t%d\t%d\t%s", u_id, s_id, c+1, l_annotations.get(c)));
				}
				writer.println();
			}
			writer.close();
		}
	}
	
	public static List<String> getNextWordForms(BufferedReader reader) throws IOException{
		return getNext(reader, 3);
	}
	
	public static List<String> getNextAnnotations(BufferedReader reader) throws IOException{
		return getNext(reader, 3);
	}
	
	private static List<String> getNext(BufferedReader reader, int idx) throws IOException{
		List<String> list = new ArrayList<>();
		
		String line; String[] fields;
		while( (line = reader.readLine()) != null){
			if(line.equals(StringConst.EMPTY)) break;
			fields = Splitter.splitTabs(line);
			list.add(fields[idx]);
			
			u_id = Integer.parseInt(fields[0]);
			s_id = Integer.parseInt(fields[1]);
		}
		
		return (list.isEmpty())? null : list;
	}
}
