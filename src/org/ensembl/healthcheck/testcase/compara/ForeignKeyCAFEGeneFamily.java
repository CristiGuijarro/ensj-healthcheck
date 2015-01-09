/*
 * Copyright [1999-2015] Wellcome Trust Sanger Institute and the EMBL-European Bioinformatics Institute
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

/**
 * An EnsEMBL Healthcheck test case that looks for broken foreign-key
 * relationships.
 */

public class ForeignKeyCAFEGeneFamily extends SingleDatabaseTestCase {

    /**
     * Create an ForeignKeyMemberId that applies to a specific set of databases.
     */
    public ForeignKeyCAFEGeneFamily() {

        addToGroup("compara_homology");
        setDescription("Check for broken foreign-key relationships in ensembl_compara databases.");
        setTeamResponsible(Team.COMPARA);

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

        if (tableHasRows(con, "CAFE_gene_family")) {

	    result &= checkForOrphans(con, "CAFE_species_gene", "cafe_gene_family_id", "CAFE_gene_family", "cafe_gene_family_id");

            result &= checkForOrphans(con, "CAFE_gene_family", "gene_tree_root_id", "gene_tree_root", "root_id");

	    result &= checkForOrphans(con, "CAFE_gene_family", "lca_id", "species_tree_node", "node_id");
	    result &= checkForOrphans(con, "CAFE_gene_family", "root_id", "species_tree_root", "root_id");

        } else {
            ReportManager.correct(this, con, "NO ENTRIES in CAFE_gene_family table, so nothing to test IGNORED");
        }

        return result;

    }

} // ForeignKeyCAFEGeneFamily
