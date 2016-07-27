/*
 * Copyright [1999-2015] Wellcome Trust Sanger Institute and the EMBL-European Bioinformatics Institute
 * Copyright [2016] EMBL-European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.ensembl.healthcheck.testcase.generic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.ensembl.healthcheck.DatabaseRegistry;
import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.DatabaseType;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.Species;
import org.ensembl.healthcheck.Team;
import org.ensembl.healthcheck.testcase.SingleDatabaseTestCase;
import org.ensembl.healthcheck.util.DBUtils;

/**
 * Check that all species that should have projected xrefs do in fact have them.
 */
public class ProjectedXrefs extends SingleDatabaseTestCase {

	/**
	 * Creates a new instance of ProjectedXrefs.
	 */
	public ProjectedXrefs() {

		setDescription("Check that all species that should have projected xrefs do in fact have them.");
		setTeamResponsible(Team.CORE);

	}

	public void types() {

                removeAppliesToType(DatabaseType.OTHERFEATURES);
                removeAppliesToType(DatabaseType.ESTGENE);
                removeAppliesToType(DatabaseType.EST);
                removeAppliesToType(DatabaseType.CDNA);
                removeAppliesToType(DatabaseType.VEGA);
                removeAppliesToType(DatabaseType.RNASEQ);
	}

	/**
	 * Run the test.
	 * 
	 * @param dbre
	 *          The database to use.
	 * @return true if the test passed.
	 * 
	 */
	public boolean run(DatabaseRegistryEntry dbre) {

		boolean result = true;
	        Connection con = dbre.getConnection();
                Species species = dbre.getSpecies();

                if (species.equals(Species.HOMO_SAPIENS) || species.equals(Species.CAENORHABDITIS_ELEGANS) || species.equals(Species.DROSOPHILA_MELANOGASTER) || species.equals(Species.SACCHAROMYCES_CEREVISIAE) || species.equals(Species.CIONA_INTESTINALIS) || species.equals(Species.CIONA_SAVIGNYI)) {
                        return result;
                }

                // check display xrefs

	        int rows = DBUtils.getRowCount(con, "SELECT COUNT(*) FROM gene g, xref x WHERE g.display_xref_id=x.xref_id AND x.info_type='PROJECTION'");

	        if (rows == 0) {
		        ReportManager.problem(this, con, "No genes in " + species + " have projected display_xrefs");
		        result = false;
		} else {
			ReportManager.correct(this, con, rows + " genes in " + species + " have projected display_xrefs");
		}

		// check GO terms

		rows = DBUtils.getRowCount(con, "SELECT COUNT(*) FROM xref x, external_db e WHERE e.external_db_id=x.external_db_id AND e.db_name='GO' AND x.info_type='PROJECTION'");

		if (rows == 0) {
			ReportManager.problem(this, con, "No projected GO terms in " + species);
			result = false;
		} else {
			ReportManager.correct(this, con, rows + " projected GO terms in " + species);
		}
		return result;

	} // run

} // ProjectedXrefs
