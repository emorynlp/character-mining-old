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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.nlp.common.util.DSUtils;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 8, 2016
 */
public class Utterance implements Serializable{
	private static final long serialVersionUID = -7666651294661709214L;
	
	/* Static functions =============================================================== */
	public static final Set<Character>
		ignore_character_start 	= DSUtils.toHashSet('(', '['),
		ignore_character_end 	= DSUtils.toHashSet(')', ']');
	
	public static String trimActionNote(String line){
		StringBuilder sb = new StringBuilder();
		int para_depth = 0; String statement;
		
		for(char c : line.toCharArray()){
			switch(c){
				case '(': case '[': case '{':		para_depth++; break;
				case ')': case ']': case '}':		para_depth--; break;
				default:							if(para_depth <= 0)	sb.append(c);
			}
		}
		statement = sb.toString().trim();
		return (statement.length() == 0)? null : statement;
	}
	
	/* Class body ===================================================================== */
	private String speaker, utterance_raw, statment_raw;
	private List<StatementNode[]> statement_trees;
	
	
	public Utterance(String speaker){
		init(speaker, null, null, null);
	}
	
	public Utterance(String speaker, String utterance){
		init(speaker, utterance, trimActionNote(utterance), null);
	}
	
	public Utterance(String speaker, String utterance, String statement){
		init(speaker, utterance, statement, null);
	}
	
	public Utterance(String speaker, String utterance, String statement, List<StatementNode[]> statement_trees){
		init(speaker, utterance, statement, statement_trees);
	}
	
	private void init(String speaker, String utterance, String statement, List<StatementNode[]> statement_trees){
		this. speaker = speaker; 
		utterance_raw = utterance;
		statment_raw = statement;
		
		if(statement_trees == null) statement_trees = new ArrayList<>();
		else						statement_trees = new ArrayList<>(statement_trees);
	}
	
	public String getSpeaker(){
		return speaker;
	}
	
	public String getUtterance(){
		return utterance_raw;
	}
	
	public String getStatement(){
		return statment_raw;
	}
	
	public List<StatementNode[]> getStatementTrees(){
		return statement_trees;
	}
	
	public void setSpeaker(String speaker){
		this.speaker = speaker;
	}
	
	public void setUtterance(String utterance){
		this.utterance_raw = utterance;
	}
	
	public void setStatement(String statement){
		this.statment_raw = statement;
	}
	
	public void setStatmentTress(List<StatementNode[]> statement_trees){
		this.statement_trees = statement_trees;
	}
	
	public void addStatementTree(StatementNode[] tree){
		statement_trees.add(tree);
	}
}
