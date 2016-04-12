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
package edu.emory.mathcs.nlp.mining.character.script.util.nlp4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.decode.DecodeConfig;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.emory.mathcs.nlp.tokenization.Tokenizer;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 9, 2016
 */
public class ScriptSceneTSVtoNLP4JParser {
	public static final String REGEX_PARATHESE = "\\(.*\\)"; 
	
	public static void toNLP4J(String in_path, String in_ext, String out_path, String out_tree_ext, String out_speaker_ext) throws IOException{
		if(out_path.charAt(out_path.length()-1) != '/') out_path += "/";
		
		InputStream in_config = IOUtils.createFileInputStream("src/main/configuration/config-decode-en.xml");
		NLPDecoder decoder = new NLPDecoder(new DecodeConfig(new BufferedInputStream(in_config)));
		Tokenizer tokenizer = new EnglishTokenizer();
		
		BufferedReader reader; String out_filePath;
		PrintWriter tree_writer, speaker_writer;
		
		for(String in_filePath : FileUtils.getFileList(in_path, in_ext)){
			out_filePath = out_path + FileUtils.getBaseName(in_filePath);
			out_filePath = out_filePath.substring(0, out_filePath.lastIndexOf('.'));
			
			reader = IOUtils.createBufferedReader(in_filePath);
			tree_writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_filePath + out_tree_ext));
			speaker_writer = new PrintWriter(IOUtils.createBufferedPrintStream(out_filePath + out_speaker_ext));
			
			process(tokenizer, decoder, reader, tree_writer, speaker_writer);
		}
	}
	
	private static void process(Tokenizer tokenizer, NLPDecoder decoder, BufferedReader reader, PrintWriter tree_writer, PrintWriter speaker_writer) throws IOException{
		String line, spk, statement; String[] fields;
		int i, uid = 0, sid = 0; List<NLPNode[]> trees;
		
		while( (line = reader.readLine()) != null){
			if(line.equals(StringConst.EMPTY)) continue;
			fields = Splitter.splitTabs(line);
			
			statement = fields[2];
			spk = fields[0].replaceAll(REGEX_PARATHESE, StringConst.EMPTY); 
			trees = tokenizer.segmentize(statement);
			
			for(NLPNode[] tree : trees){				
				decoder.decode(tree);
				for(i = 1; i < tree.length; i++)
					tree_writer.println(String.format("%d\t%d\t%s", uid, sid, tree[i].toString()));
				tree_writer.println();
				speaker_writer.println(String.format("%d\t%d\t%s", uid, sid++, spk));
			}
			uid++;
		}
		
		reader.close(); tree_writer.close(); speaker_writer.close();
	}
}
