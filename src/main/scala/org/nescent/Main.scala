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
import java.util.UUID

object Main extends App {

  BasicConfigurator.configure()
  Logger.getRootLogger().setLevel(Level.DEBUG)

  val part_of = ObjectProperty("http://purl.obolibrary.org/obo/BFO_0000050")
  val class1 = Class("http://purl.obolibrary.org/obo/UBERON_0002398")
  val class1Query = Class(s"http://example.org/${UUID.randomUUID.toString}")
  val class1Axiom = class1Query EquivalentTo (part_of some class1)
  val class2 = Class("http://purl.obolibrary.org/obo/UBERON_0000955")
  val class2Query = Class(s"http://example.org/${UUID.randomUUID.toString}")
  val class2Axiom = class2Query EquivalentTo (part_of some class2)

  val factory = OWLManager.getOWLDataFactory
  val manager = OWLManager.createOWLOntologyManager
  val ontology = manager.loadOntologyFromOntologyDocument(new File("/data/phenoscape-kb/tbox.owl"))
  val reasoner = new ElkReasonerFactory().createReasoner(ontology)
  reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY)
  println("Class1")
  manager.addAxiom(ontology, class1Axiom)
  reasoner.flush()
  reasoner.getSubClasses(class1Query, false).getFlattened.foreach(println)
  manager.removeAxiom(ontology, class1Axiom)
  println("Class2")
  manager.addAxiom(ontology, class2Axiom)
  reasoner.flush()
  reasoner.getSubClasses(class2Query, false).getFlattened.foreach(println)
  manager.removeAxiom(ontology, class2Axiom)

}