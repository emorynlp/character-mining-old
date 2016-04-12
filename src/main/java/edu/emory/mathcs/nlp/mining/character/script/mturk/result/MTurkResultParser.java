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
package edu.emory.mathcs.nlp.mining.character.script.mturk.result;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.mining.character.script.structure.MTurkResultEntry;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Feb 3, 2016
 */
public class MTurkResultParser {
	private final Pattern CSV_delim = Pattern.compile("\",\"");
	private final String ASSGN_FIELDNAME = "AssignmentId", HTML_FIELDNAME = "Input.Scene_html";
	private final String ANS_FIELD_FORMAT = "Answer.Q%dAnswer";
	
	private int question_count;
	
	public MTurkResultParser(int question_count){
		this.question_count = question_count;
	}
	
	public List<MTurkResultEntry> parseMTurkResult(InputStream in){
		List<MTurkResultEntry> list = new ArrayList<>();
		
		try {
			BufferedReader reader = IOUtils.createBufferedReader(in);
			String line; String[] keys = null, fields; 
			
			// Read off field names
			if((line = reader.readLine()) != null){
				keys = Splitter.splitCommas(line);
				cleanCSVFormat(keys);
			}

			MTurkResultEntry entry;
			while( (line = reader.readLine()) != null){
				fields = Splitter.split(line, CSV_delim);
				cleanCSVFormat(fields);
				
				entry = new MTurkResultEntry();
				entry.addFields(keys, fields);

				list.add(entry);
			}
			
			reader.close();
		} catch (Exception e) { e.printStackTrace(); }
		
		return list;
	}
	
	public void exportMTurkResult(List<MTurkResultEntry> entries, String out_directory, String out_html_ext, String out_key_ext){
		if(out_directory.charAt(out_directory.length()-1) != CharConst.FW_SLASH)
			out_directory += StringConst.FW_SLASH;
		
		int i; PrintWriter writer; String outpath;
		for(MTurkResultEntry entry : entries){
			outpath = out_directory + entry.getField(ASSGN_FIELDNAME) + out_html_ext;
			writer = new PrintWriter(IOUtils.createBufferedPrintStream(outpath));
			
			writer.println(entry.getField(HTML_FIELDNAME));
			writer.close();
			
			outpath = out_directory + entry.getField(ASSGN_FIELDNAME) + out_key_ext;
			writer = new PrintWriter(IOUtils.createBufferedPrintStream(outpath));
			
			for(i = 1; i <= question_count; i++)
				writer.println(entry.getField(String.format(ANS_FIELD_FORMAT, i)));
			
			writer.close();
		}
	}
	
	private void cleanCSVFormat(String[] strings){
		int i, start, end; char[] cs;
		
		for(i = 0; i < strings.length; i++){
			cs = strings[i].toCharArray();
			
			for(start = 0; start < cs.length; start++)
				if(cs[start] != '"' && cs[start] != ' ') break;
			for(end = cs.length-1; end >= start; end--)
				if(cs[end] != '"' && cs[end] != ' ') break;

			strings[i] = strings[i].substring(start, end+1); 
		}
	}
}
