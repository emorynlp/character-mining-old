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
package edu.emory.mathcs.nlp.bin;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.component.dep.DEPParser;
import edu.emory.mathcs.nlp.component.ner.NERTagger;
import edu.emory.mathcs.nlp.component.pos.POSTagger;
import edu.emory.mathcs.nlp.component.srl.SRLParser;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.train.OnlineTrainer;
import edu.emory.mathcs.nlp.component.template.util.NLPMode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPTrain
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	protected String configuration_file;
	@Option(name="-m", usage="output model file (optional)", required=false, metaVar="<filename>")
	protected String model_file = null;
	@Option(name="-p", usage="previously trained model file (optional)", required=false, metaVar="<filename>")
	protected String previous_model_file = null;
	@Option(name="-t", usage="training path (required)", required=true, metaVar="<filepath>")
	protected String train_path;
	@Option(name="-d", usage="development path (optional)", required=false, metaVar="<filepath>")
	protected String develop_path;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<string>")
	protected String train_ext = "*";
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<string>")
	protected String develop_ext = "*";
	@Option(name="-mode", usage="mode (required: pos|ner|dep|srl|sent)", required=true, metaVar="<string>")
	protected String mode = null;
	
	public void train(String[] args)
	{
		BinUtils.initArgs(args, this);
		List<String> trainFiles   = FileUtils.getFileList(train_path  , train_ext);
		List<String> developFiles = (develop_path != null) ? FileUtils.getFileList(develop_path, develop_ext) : null;
		OnlineTrainer<?, ?> trainer  = createOnlineTrainer();
		
		Collections.sort(trainFiles);
		if (developFiles != null) Collections.sort(developFiles);
		trainer.train(NLPMode.valueOf(mode), trainFiles, developFiles, configuration_file, model_file, previous_model_file);
	}
	
	public <S extends NLPState<N>, N extends NLPNode>OnlineTrainer<S, N> createOnlineTrainer()
	{
		return new OnlineTrainer<S, N>()
		{
			@Override
			public OnlineComponent<S, N> createComponent(NLPMode mode, InputStream config)
			{
				return createOnlineComponent(mode, config);
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	public <S extends NLPState<N>, N extends NLPNode>OnlineComponent<S, N> createOnlineComponent(NLPMode mode, InputStream config)
	{
		switch (mode)
		{
		case pos: return (OnlineComponent<S, N>)new POSTagger(config);
		case ner: return (OnlineComponent<S, N>)new NERTagger(config);
		case dep: return (OnlineComponent<S, N>)new DEPParser(config);
		case srl: return (OnlineComponent<S, N>)new SRLParser(config);
		default : throw new IllegalArgumentException("Unsupported mode: "+mode);
		}
	}
	
	static public void main(String[] args)
	{
		new NLPTrain().train(args);
	}
}
