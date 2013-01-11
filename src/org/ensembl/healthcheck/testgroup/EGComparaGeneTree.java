package org.ensembl.healthcheck.testgroup;

import org.ensembl.healthcheck.GroupOfTests;
import org.ensembl.healthcheck.testcase.compara.CheckFlatProteinTrees;
import org.ensembl.healthcheck.testcase.compara.CheckHomology;
import org.ensembl.healthcheck.testcase.compara.CheckSequenceTable;
import org.ensembl.healthcheck.testcase.compara.ForeignKeyHomologyId;
import org.ensembl.healthcheck.testcase.compara.ForeignKeyMethodLinkId;
import org.ensembl.healthcheck.testcase.compara.ForeignKeySequenceId;
import org.ensembl.healthcheck.testcase.compara.ForeignKeyTaxonId;
import org.ensembl.healthcheck.testcase.eg_compara.CheckEmptyLeavesTrees;
import org.ensembl.healthcheck.testcase.eg_compara.EGForeignKeyHomologyMemberId;
import org.ensembl.healthcheck.testcase.eg_compara.EGForeignKeyGeneTreeMemberId;
import org.ensembl.healthcheck.testcase.eg_compara.EGGeneTreeForeignKeyMethodLinkSpeciesSetId;

public class EGComparaGeneTree extends GroupOfTests {

	public EGComparaGeneTree() {
		addTest(CheckFlatProteinTrees.class, 
				CheckEmptyLeavesTrees.class,
				CheckHomology.class, 
				CheckSequenceTable.class,

				EGForeignKeyHomologyMemberId.class,
				// Commented out until
				// http://www.ebi.ac.uk/panda/jira/browse/EG-1242
				// is resolved.
				//
				//EGForeignKeyGeneTreeMemberId.class,
				EGGeneTreeForeignKeyMethodLinkSpeciesSetId.class,

				ForeignKeyHomologyId.class, 
				ForeignKeyMethodLinkId.class,
				ForeignKeySequenceId.class, 
				ForeignKeyTaxonId.class);
	}
}
