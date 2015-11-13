/**
 * Copyright 2015, Emory University
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
package edu.emory.mathcs.nlp.component.zzz.feature;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.constant.CharConst;
import edu.emory.mathcs.nlp.common.constant.MetaConst;
import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.component.zzz.node.NLPNode;
import edu.emory.mathcs.nlp.component.zzz.node.Orthographic;
import edu.emory.mathcs.nlp.component.zzz.state.NLPState;
import edu.emory.mathcs.nlp.learning.vector.StringVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class FeatureTemplate<S extends NLPState> implements Serializable
{
	private static final long serialVersionUID = -6755594173767815098L;
	protected List<FeatureItem<?>[]> feature_list;
	protected List<FeatureItem<?>>   feature_set;

	public FeatureTemplate()
	{
		feature_list = new ArrayList<>();
		feature_set  = new ArrayList<>();
		init();
	}
	
	protected abstract void init();
	
//	============================== SERIALIZATION ==============================
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		feature_list = (List<FeatureItem<?>[]>)in.readObject();
		feature_set  = (List<FeatureItem<?>>)  in.readObject();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(feature_list);
		out.writeObject(feature_set);
	}

//	============================== INITIALIZATION ==============================

	public void add(FeatureItem<?>... items)
	{
		feature_list.add(items);
	}
	
	public void addSet(FeatureItem<?> items)
	{
		feature_set.add(items);
	}
	
	public int size()
	{
		return feature_list.size() + feature_set.size();
	}
	
//	============================== EXTRACTOR ==============================
	
	public StringVector extractFeatures(S state)
	{
		StringVector x = new StringVector();
		Collection<String> t;
		int i, type = 0;
		String f;
		
		for (i=0; i<feature_list.size(); i++,type++)
		{
			f = getFeature(state, feature_list.get(i));
			if (f != null) x.add(type, f);
		}
		
		for (i=0; i<feature_set.size(); i++,type++)
		{
			t = getFeatures(state, feature_set.get(i));
			if (t != null) for (String s : t) x.add(type, s);
		}
		
		return x;
	}
	
	/** Called by {@link #extractFeatures()}. */
	protected String getFeature(S state, FeatureItem<?>... items)
	{
		String f;
		
		if (items.length == 1)
			return getFeature(state, items[0]);
		else
		{
			StringJoiner join = new StringJoiner("_");
			
			for (FeatureItem<?> item : items)
			{
				f = getFeature(state, item);
				if (f == null) return null;
				join.add(f);
			}
			
			return join.toString();
		}
	}
	
	protected abstract String getFeature(S state, FeatureItem<?> item);
	protected abstract Collection<String> getFeatures(S state, FeatureItem<?> item);
	
	protected String getFeature(FeatureItem<?> item, NLPNode node)
	{
		String f = node.getValue(item.field);
		if (f != null) return f;
		
		switch (item.field)
		{
		case word_shape: return StringUtils.getShape(node.getSimplifiedWordForm(), (Integer)item.value);
		case uncapitalized_word_shape: return StringUtils.getShape(StringUtils.toLowerCase(node.getSimplifiedWordForm()), (Integer)item.value);
		case prefix: return getPrefix(node, (Integer)item.value);
		case suffix: return getSuffix(node, (Integer)item.value);
		case feats: return node.getFeat((String)item.value);
		case valency: return node.getValency((Direction)item.value);
		default: return null;
		}
	}
	
	protected Collection<String> getFeatures(S state, FeatureItem<?> item, NLPNode node)
	{
		switch (item.field)
		{
		case binary: return getBinaryFeatures(state, node);
		case orthographic: return getOrthographicFeatures(state, node, true);
		case orthographic_uncapitalized: return getOrthographicFeatures(state, node, false);
		case word_clusters: return node.getWordClusters();
		case named_entity_gazetteers: return node.getNamedEntityGazetteers();
		default: return null;
		}
	}
	
	/** The prefix cannot be the entire word (e.g., getPrefix("abc", 3) -> null). */
	protected String getPrefix(NLPNode node, int n)
	{
		String s = node.getSimplifiedWordForm();
		return (n < s.length()) ? StringUtils.toLowerCase(s.substring(0, n)) : null;
	}
	
	/** The suffix cannot be the entire word (e.g., getSuffix("abc", 3) -> null). */
	protected String getSuffix(NLPNode node, int n)
	{
		String s = node.getSimplifiedWordForm();
		return (n < s.length()) ? StringUtils.toLowerCase(s.substring(s.length()-n)) : null;
	}
	
	protected List<String> getBinaryFeatures(S state, NLPNode node)
	{
		List<String> values = new ArrayList<>();
		
		if (state.isFirst(node)) values.add("0");
		if (state.isLast (node)) values.add("1");
		
		return values.isEmpty() ? null : values;
	}
	
	protected List<String> getOrthographicFeatures(S state, NLPNode node, boolean caseSensitive)
	{
		List<String> list = new ArrayList<>();
		
		if (MetaConst.HYPERLINK.equals(node.getSimplifiedWordForm()))
			list.add(Orthographic.HYPERLINK);
		else
		{
			char[] cs = node.getSimplifiedWordForm().toCharArray();
			getOrthographicFeauturesAux(list, cs, state.isFirst(node), caseSensitive);
		}
		
		return list.isEmpty() ? null : list;
	}
	
	/** Called by {@link #getOrthographic(boolean)}. */
	protected void getOrthographicFeauturesAux(List<String> list, char[] cs, boolean isFirst, boolean caseSensitive)
	{
		boolean hasDigit  = false;
		boolean hasPeriod = false;
		boolean hasHyphen = false;
		boolean hasPunct  = false;
		boolean fstUpper  = false;
		boolean allDigit  = true;
		boolean allPunct  = true;
		boolean allUpper  = true;
		boolean allLower  = true;
		boolean noLower   = true;
		boolean allDigitOrPunct = true;
		int     countUpper = 0;
		
		boolean upper, lower, punct, digit;
		int i, size = cs.length;
		char c;
		
		for (i=0; i<size; i++)
		{
			c = cs[i];
			
			upper = CharUtils.isUpperCase(c);
			lower = CharUtils.isLowerCase(c);
			digit = CharUtils.isDigit(c);
			punct = CharUtils.isPunctuation(c);
			
			if (upper)
			{
				if (i == 0)	fstUpper = true;
				else		countUpper++;
			}
			else
				allUpper = false;
			
			if (lower)	noLower  = false;	
			else		allLower = false;
			
			if (digit)	hasDigit = true;
			else		allDigit = false;

			if (punct)
			{
				hasPunct = true;
				if (c == CharConst.PERIOD) hasPeriod = true;
				if (c == CharConst.HYPHEN) hasHyphen = true;
			}
			else
				allPunct = false;
			
			if (!digit && !punct)
				allDigitOrPunct = false;
		}
		
		if (allUpper)
			{if (caseSensitive) list.add(Orthographic.ALL_UPPER);}
		else if (allLower)
			{if (caseSensitive) list.add(Orthographic.ALL_LOWER);}
		else if (allDigit)
			list.add(Orthographic.ALL_DIGIT);
		else if (allPunct)
			list.add(Orthographic.ALL_PUNCT);
		else if (allDigitOrPunct)
			list.add(Orthographic.ALL_DIGIT_OR_PUNCT);
		else if (noLower)
			{if (caseSensitive) list.add(Orthographic.NO_LOWER);}
		
		if (caseSensitive && !allUpper)
		{
			if (fstUpper && !isFirst)
				list.add(Orthographic.FST_UPPER);
			if (countUpper == 1)
				list.add(Orthographic.UPPER_1);
			else if (countUpper > 1)
				list.add(Orthographic.UPPER_2);
		}
		
		if (!allDigit && hasDigit)
			list.add(Orthographic.HAS_DIGIT);
		
		if (hasPeriod)	list.add(Orthographic.HAS_PERIOD);
		if (hasHyphen)	list.add(Orthographic.HAS_HYPHEN);
		
		if (!allPunct && !hasPeriod && !hasHyphen && hasPunct)
			list.add(Orthographic.HAS_OTHER_PUNCT);
	}
}