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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.collection.tuple.IntIntPair;
import edu.emory.mathcs.nlp.common.collection.tuple.Pair;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.decode.DecodeConfig;
import edu.emory.mathcs.nlp.decode.NLPDecoder;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 8, 2016
 */
public class ReleaseDataNLP4JParseScript {
	public static final String
		IN_DIR = "/Users/HenryChen/Desktop/Friends_Release",
		IN_EXT = ".dep",
		OUT_EXT = "nlp4j";
	
	public static void main(String[] args) throws IOException{
		InputStream in_config = IOUtils.createFileInputStream("src/main/configuration/config-decode-en.xml");
		NLPDecoder decoder = new NLPDecoder(new DecodeConfig(new BufferedInputStream(in_config)));
				
		NLPNode[] nodes; Pair<IntIntPair, String> line;
		int i, u_id, s_id; String sentence;
		
		for(String file_path : FileUtils.getFileList(IN_DIR, IN_EXT)){
			String out_path = FileUtils.replaceExtension(file_path, OUT_EXT);
			BufferedReader reader = IOUtils.createBufferedReader(file_path);
			PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_path));
			
			while( (line = getNextSentence(reader)) != null){
				u_id = line.o1.i1; 
				s_id = line.o1.i2;
				sentence = line.o2;
				
				nodes = decoder.decode(sentence);
				for(i = 1; i < nodes.length; i++)
					writer.println(String.format("%d\t%d\t%s", u_id, s_id, nodes[i]));
				writer.println();
			}
			writer.close();
		}
	}
	
	public static Pair<IntIntPair, String> getNextSentence(BufferedReader reader) throws IOException{
		int u_id = -1, s_id = -1; String line; String[] fields;
		StringJoiner joiner = new StringJoiner(StringConst.SPACE);
		
		while( (line = reader.readLine()) != null){
			if(line.equals(StringConst.EMPTY)) break;
			fields = Splitter.splitTabs(line);
			
			if(u_id < 0 && s_id < 0){
				u_id = Integer.parseInt(fields[0]);
				s_id = Integer.parseInt(fields[1]);
			}
			joiner.add(fields[3]);
		}
		
		return (joiner.length() == 0)? null : new Pair<>(new IntIntPair(u_id, s_id), joiner.toString());
	}
}
