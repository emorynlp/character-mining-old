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
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.common.collection.tuple.IntIntPair;
import edu.emory.mathcs.nlp.common.collection.tuple.Triple;
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
public class ClearnNLPtoNLP4JParseScript {
	public static final String
		IN_DIR = "/Users/HenryChen/Desktop/CharacterDetection/MTurk_out/release/augmented/final",
		IN_EXT = ".dep",
		OUT_EXT = "nlp4j";
	
	public static void main(String[] args) throws IOException{
		InputStream in_config = IOUtils.createFileInputStream("src/main/configuration/config-decode-en.xml");
		NLPDecoder decoder = new NLPDecoder(new DecodeConfig(new BufferedInputStream(in_config)));
				
		Triple<IntIntPair, List<String>, List<String>> triple;
		int i, u_id, s_id; List<String> annotations; NLPNode[] nodes; 
		
		for(String file_path : FileUtils.getFileList(IN_DIR, IN_EXT)){
			String out_path = FileUtils.replaceExtension(file_path, OUT_EXT);
			BufferedReader reader = IOUtils.createBufferedReader(file_path);
			PrintWriter writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_path));
			
			while( (triple = getNextSentence(reader)) != null){
				u_id = triple.o1.i1; 
				s_id = triple.o1.i2;
				annotations = triple.o3;
				
				nodes = decode(decoder, triple.o2);
				for(i = 1; i < nodes.length; i++)
					writer.println(String.format("%d\t%d\t%s\t%s", u_id, s_id, nodes[i], annotations.get(i-1)));
				writer.println();
			}
			writer.close();
		}
	}
	
	public static NLPNode[] decode(NLPDecoder decoder, List<String> tokens){
		NLPNode[] nodes = new NLPNode[tokens.size()+1];
		nodes[0] = new NLPNode().toRoot();
		
		for(int i = 1; i <= tokens.size(); i++)
			nodes[i] = new NLPNode(i, tokens.get(i-1));
		decoder.decode(nodes);
		
		return nodes;
	}
	
	public static Triple<IntIntPair, List<String>, List<String>> getNextSentence(BufferedReader reader) throws IOException{
		int u_id = -1, s_id = -1; String line; String[] fields;
		List<String> tokens = new ArrayList<>(), annotations = new ArrayList<>();
		
		while( (line = reader.readLine()) != null){
			if(line.equals(StringConst.EMPTY)) break;
			fields = Splitter.splitTabs(line);
			
			if(u_id < 0 && s_id < 0){
				u_id = Integer.parseInt(fields[0]);
				s_id = Integer.parseInt(fields[1]);
			}
			
			tokens.add(fields[3]);
			annotations.add(fields[12]);
		}
		
		return (tokens.isEmpty())? null : new Triple<>(new IntIntPair(u_id, s_id), tokens, annotations);
	}
}
