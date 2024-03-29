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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Splitter;
import edu.emory.mathcs.nlp.component.template.node.FeatMap;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.mining.character.structure.Scene;
import edu.emory.mathcs.nlp.mining.character.structure.StatementNode;
import edu.emory.mathcs.nlp.mining.character.structure.Utterance;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Mar 8, 2016
 */
public class SceneTSVReader{
	public static String BLANK = StringConst.UNDERSCORE;
	public static String FEAT_KEY = "speaker";
	
	public static SceneTSVReader DEFAULT(){
		SceneTSVReader reader = new SceneTSVReader();
		reader.initFieldIndices(3, 4, 5, 10, 6, 7, 8, 9, 0, 1, 11);
		return reader;
	}
	
	private BufferedReader reader;
	
	public int form   		= -1;
	public int lemma  		= -1;
	public int pos    		= -1;
	public int nament 		= -1;
	public int feats  		= -1;
	public int dhead  		= -1;
	public int deprel 		= -1;
	public int sheads 		= -1;
	public int utterance_id	= -1;
	public int sentence_id	= -1;
	public int referant		= -1;
	
	public void initFieldIndices(
		int form, int lemma, int pos, int nament, int feats, int dhead, int deprel, 
		int sheads, int utteranceId, int sentenceId, int referant){
		this.form = form; 		this.lemma = lemma;
		this.pos = pos;			this.nament = nament;
		this.feats = feats;		this.dhead = dhead;
		this.deprel = deprel;	this.sheads = sheads;
		
		this.utterance_id 	= utteranceId;
		this.sentence_id 	= sentenceId;
		this.referant 		= referant;
	}
	
	public Scene readScene(InputStream in){
		return readScene(-1, in);
	}
	
	public Scene readScene(int scene_id, InputStream in){
		TreeMap<Integer, Utterance> utterances = new TreeMap<>();
		
		try {
			open(in);
			String line; List<String[]> list = new ArrayList<>();
			
			while( (line = reader.readLine()) != null){
				line = line.trim();
				
				if (line.isEmpty()){
					if (list.isEmpty()) continue;
					
					toUtterance(utterances, list);
					list.clear();
				}
				else list.add(Splitter.splitTabs(line));
			}
			close();
		} catch (IOException e) { e.printStackTrace(); }
		
		Scene scene = new Scene(scene_id);
		for(Utterance utterance : utterances.values())
			scene.addUtterance(utterance);
		return scene;
	}
		
	private void open(InputStream in){
		reader = IOUtils.createBufferedReader(in);		
	}
	
	private void close(){
		try{
			if (reader != null) reader.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	private void toUtterance(Map<Integer, Utterance> m_utterances, List<String[]> l_fields) throws IOException{
		int uid = Integer.parseInt(l_fields.get(0)[utterance_id]);
		StatementNode[] nodes = toNodeList(l_fields);
		Utterance utterance = m_utterances.getOrDefault(uid, new Utterance(uid, nodes[1].getFeat(FEAT_KEY)));
		utterance.addStatementTree(nodes);
		m_utterances.putIfAbsent(uid, utterance);
	}
	
	private StatementNode[] toNodeList(List<String[]> list){
		int i, size = list.size();
		StatementNode[] nodes = (StatementNode[])Array.newInstance(StatementNode.class, size+1);
		nodes[0] = new StatementNode().toRoot();
		
		for (i = 1; i <= size; i++)
			nodes[i] = create(i, list.get(i-1));
		
		if (dhead >= 0){
			for (i=1; i<=size; i++)
				initDependencyHead(i, list.get(i-1), nodes);
			
			if (sheads >= 0){
				for (i=1; i<=size; i++)
					initSemanticHeads(i, list.get(i-1)[sheads], nodes);
			}
		}
		return nodes;
	}
	
	private StatementNode create(int id, String[] values){
		String  f = getValue(values, form  , false);
		String  l = getValue(values, lemma , false);
		String  p = getValue(values, pos   , true);
		String  n = getValue(values, nament, true);
		FeatMap t = new FeatMap(getValue(values, feats, true));
		String	r = getValue(values, referant, true);
		
		return new StatementNode(new NLPNode(id, f, l, p, n, t, null, null), r);
	}
	
	private String getValue(String[] values, int index, boolean tag){
		if (index < 0 || values.length <= index) return null;
		String s = values[index];
		return tag && BLANK.equals(s) ? null : s; 
	}
	
	private void initDependencyHead(int id, String[] values, StatementNode[] nodes){
		if (BLANK.equals(values[dhead])) return;
		int headID = Integer.parseInt(values[dhead]);
		nodes[id].setDependencyHead(nodes[headID], values[deprel]);
	}
	
	private void initSemanticHeads(int id, String value, StatementNode[] nodes){
		if (BLANK.equals(value)) return;
		NLPNode node = nodes[id];
		int headID;
		String[] t;
		
		for (String arg : Splitter.splitSemiColons(value)){
			t = Splitter.splitColons(arg);
			headID = Integer.parseInt(t[0]);
			node.addSemanticHead(nodes[headID], t[1]);
		}			
	}
}
