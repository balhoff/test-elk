// scala 2.13.3
// ammonite 2.3.8

import $ivy.`net.sourceforge.owlapi:owlapi-distribution:4.5.17`
import $ivy.`org.semanticweb.elk:elk-owlapi:0.4.3`
import $ivy.`com.outr::scribe-slf4j:2.7.12`

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.elk.owlapi.ElkReasonerFactory
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLClassExpression
import org.semanticweb.owlapi.reasoner.OWLReasoner
import org.semanticweb.owlapi.reasoner.InferenceType
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import org.apache.log4j.Level

@main
def main(ontFile: os.Path) = {
    BasicConfigurator.configure()
    Logger.getRootLogger().setLevel(Level.DEBUG)
    val factory = OWLManager.getOWLDataFactory()
    val PartOf = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050"))
    val AnatomicalEntity = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/UBERON_0001062"))
    val SkeletalSystem = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/UBERON_0001434"))
    val ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontFile.toIO)
    scribe.info("Done loading")
    val reasoner = new ElkReasonerFactory().createReasoner(ontology)
    reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY)
    scribe.info("Done classifying")
    query(factory.getOWLObjectSomeValuesFrom(PartOf, SkeletalSystem), reasoner)
    query(factory.getOWLObjectSomeValuesFrom(PartOf, AnatomicalEntity), reasoner)
}

def query(cls: OWLClassExpression, reasoner: OWLReasoner): Unit = {
    val start = System.currentTimeMillis()
    val size = reasoner.getSubClasses(cls, false).getFlattened().size()
    val stop = System.currentTimeMillis()
    scribe.info(s"Query time: ${stop - start}ms")
    scribe.info(size.toString())
}
