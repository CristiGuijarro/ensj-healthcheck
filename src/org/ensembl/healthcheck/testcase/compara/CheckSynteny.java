/*
 * Copyright [1999-2016] Wellcome Trust Sanger Institute and the EMBL-European Bioinformatics Institute
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


package org.ensembl.healthcheck.testcase.compara;

import java.sql.Connection;

import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.Team;
import org.ensembl.healthcheck.testcase.SingleDatabaseTestCase;
import org.ensembl.healthcheck.util.DBUtils;

/**
 * An EnsEMBL Healthcheck test case that looks for broken foreign-key
 * relationships.
 */

public class CheckSynteny extends SingleDatabaseTestCase {

	/**
	 * Create an CheckSynteny that applies to a specific set of databases.
	 */
	public CheckSynteny() {
		setDescription("Check for missing syntenies in the compara database.");
		setTeamResponsible(Team.COMPARA);
	}

	/**
	 * Run the test.
	 * 
	 * @param dbre
	 *            The database to use.
	 * @return true if the test passed.
	 * 
	 */
	public boolean run(DatabaseRegistryEntry dbre) {

		Connection con = dbre.getConnection();

		if (!tableHasRows(con, "synteny_region")) {
			ReportManager.problem(this, con, "NO ENTRIES in the synteny_region table");
			return false;
		} else if (!tableHasRows(con, "dnafrag_region")) {
			ReportManager.problem(this, con, "NO ENTRIES in the dnafrag_region table");
			return false;
		} else if (!tableHasRows(con, "dnafrag")) {
			ReportManager.problem(this, con, "NO ENTRIES in the dnafrag table");
			return false;
		} else {
			boolean result = true;
			result &= checkForSingles(con, "dnafrag_region", "synteny_region_id");
			return result;
		}
	}

} // CheckHomology
