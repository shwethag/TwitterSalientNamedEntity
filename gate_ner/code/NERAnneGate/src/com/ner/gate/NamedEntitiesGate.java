package com.ner.gate;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NamedEntitiesGate {
	private String datasetDoc;
	private Corpus corpus;
	private CorpusController annieController;

	public NamedEntitiesGate(String docloc) throws GateException, IOException {
		datasetDoc = docloc;
		System.setProperty("gate.home", "/home/shwetha/GATE_Developer_8.0");
		Gate.init();
		File pluginsHome = Gate.getPluginsHome();
		File anniePlugin = new File(pluginsHome, "ANNIE");
		File annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
		annieController = (CorpusController) PersistenceManager
				.loadObjectFromFile(annieGapp);

	}

	public void execute() throws ResourceInstantiationException,
			MalformedURLException, ExecutionException {
		corpus = Factory.newCorpus("StandAloneAnnie corpus");
		FeatureMap params = Factory.newFeatureMap();
		params.put("sourceUrl", new File(datasetDoc).toURL());
		params.put("preserveOriginalContent", new Boolean(true));
		params.put("collectRepositioningInfo", new Boolean(true));
		Document doc = (Document) Factory.createResource(
				"gate.corpora.DocumentImpl", params);
		corpus.add(doc);
		annieController.setCorpus(corpus);
		annieController.execute();
	}

	private void print() {
		Iterator<Document> iter = corpus.iterator();
		System.out.println("Print enter---");
		while(iter.hasNext()) {
		      Document doc = (Document) iter.next();
		      AnnotationSet defaultAnnotSet = doc.getAnnotations();
		      Set<String> annotTypesRequired = new HashSet<String>();
		      annotTypesRequired.add("Person");
		      annotTypesRequired.add("Location");
		      annotTypesRequired.add("Organization");
		      annotTypesRequired.add("Unknown");
		      Set<Annotation> peopleAndPlaces =
		        new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));
		      
		     
		      String xmlDocument = doc.toXml(peopleAndPlaces, false);
		      parseXML(xmlDocument);
		}
	}
	
	private void parseXML(String xmlDoc){
		String[] lines = xmlDoc.split("\n");
		for(String line : lines){
			System.out.println(line);
		}
	}

	public static void main(String[] args) {
		try {
			NamedEntitiesGate neg = new NamedEntitiesGate(args[0]);
			neg.execute();
			neg.print();
			
		} catch (GateException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
