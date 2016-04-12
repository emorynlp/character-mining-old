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
package edu.emory.mathcs.nlp.mining.character.eval;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 	Henry(Yu-Hsin) Chen ({@code yu-hsin.chen@emory.edu})
 * @version	1.0
 * @since 	Feb 8, 2016
 */
public class CohenKappaEvaluator {
	
	private int[][] l_decisions;
	private int c_agree, c_disagree;
	
	private int c_parties, c_options;
	private Map<String, Integer> m_options;
	
	public CohenKappaEvaluator(int c_parties, String[] options){
		m_options = new HashMap<>();
		reset(c_parties, options);
	}
	
	public int getPartyCount(){
		return c_parties;
	}
	
	public int getOptionCount(){
		return c_options;
	}
	
	public void reset(int c_parties, String[] options){
		c_agree = c_disagree = 0;
		this.c_parties = c_parties;
		this.c_options = options.length;
		l_decisions = new int[c_parties][c_options];
		
		m_options.clear();
		for(int i = 0; i < c_options; i++)
			m_options.put(options[i], i);
	}
	
	public void clear(){
		c_agree = c_disagree = 0;
		for(int i = 0; i < c_parties; i++)
			Arrays.fill(l_decisions[i], 0);
	}
	
	public void addAgreement(){
		addAgreement(1);
	}
	
	public void addDisagreement(){
		addDisagreement(1);
	}
	
	public void addAgreement(int count){
		c_agree += count;
	}
	
	public void addDisagreement(int count){
		c_disagree += count;
	}
	
	public void addEntry(int party_idx, String option){
		if(!m_options.containsKey(option))
			throw new IllegalArgumentException("Invalid option string.");
		else
			l_decisions[party_idx][m_options.get(option)]++;
	}
	
	public void addEntries(int party_idx, String[] options){
		for(String option : options) addEntry(party_idx, option);
	}

	public double getAgreementScore(){
		return (double) c_agree / (c_agree + c_disagree);
	}
	
	public double getAgreementRatio(){
		return (double) c_agree / c_disagree;
	}
	
	public double getCohenKappaCoefficient(){
		double po = getAgreementScore(), pe = 0, prob;
		
		int i, j, sum;
		double[][] m_prob = new double[c_parties][c_options];
		for(i = 0; i < c_parties; i++){
			sum = Arrays.stream(l_decisions[i]).sum();
			for(j = 0; j < c_options; j++)
				m_prob[i][j] = (double) l_decisions[i][j] / sum;
		}
		
		for(j = 0; j < c_options; j++){
			prob = 1;
			for(i = 0; i < c_parties; i++)
				prob *= m_prob[i][j];
			pe += prob;
		}		
		
		return (po - pe) / (1 - pe);
	}
}
