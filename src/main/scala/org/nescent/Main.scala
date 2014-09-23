package org.nescent

import java.io.File

import scala.collection.JavaConversions._

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.phenoscape.scowl.OWL._
import org.semanticweb.elk.owlapi.ElkReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.reasoner.InferenceType

object Main extends App {

  BasicConfigurator.configure()
  Logger.getRootLogger().setLevel(Level.DEBUG)

  val part_of = ObjectProperty("http://purl.obolibrary.org/obo/BFO_0000050")
  val class1 = Class("http://purl.obolibrary.org/obo/UBERON_0002398")
  val class2 = Class("http://purl.obolibrary.org/obo/UBERON_0000955")

  val factory = OWLManager.getOWLDataFactory
  val manager = OWLManager.createOWLOntologyManager
  val ontology = manager.loadOntologyFromOntologyDocument(new File("/data/phenoscape-kb/tbox.owl"))
  val reasoner = new ElkReasonerFactory().createReasoner(ontology)
  reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY)
  println("Class1")
  reasoner.getSubClasses(part_of some class1, false).getFlattened.foreach(println)
  println("Class2")
  reasoner.getSubClasses(part_of some class2, false).getFlattened.foreach(println)

}