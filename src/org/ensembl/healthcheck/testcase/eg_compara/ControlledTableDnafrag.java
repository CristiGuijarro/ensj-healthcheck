package org.ensembl.healthcheck.testcase.eg_compara;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ensembl.healthcheck.DatabaseRegistryEntry;
import org.ensembl.healthcheck.ReportManager;
import org.ensembl.healthcheck.util.SqlTemplate;

public class ControlledTableDnafrag extends AbstractControlledRows {

	protected String getControlledTableName() {
		return "dnafrag";	
	}

	/**
	 * DatabaseRegistryEntry of the master database.
	 */
	protected DatabaseRegistryEntry getMasterDatabase() {
		return getComparaMasterDatabase();
	}

	@Override
	protected boolean runTest(DatabaseRegistryEntry dbre) {

		String controlledTableToTest = getControlledTableName();
		
		DatabaseRegistryEntry masterDbRe = getMasterDatabase();
		Connection testDbConn = dbre.getConnection();
		
		if (masterDbRe==null) {
			ReportManager.problem(
				this, 
				testDbConn, 
				"Can't get connection to master database! Perhaps it has not been "
				+"configured?"
			);
			return false;
		}
		
		boolean passed = checkAllRowsInTableIfInDnaCompara(controlledTableToTest, getControlledTableName(), dbre, masterDbRe);
		
		//ReportManager.problem(this, testDbConn, "Not implemented yet!");		
		
		return passed;
	}
	
	protected boolean checkAllRowsInTable(
			final String controlledTableToTest,
			final String masterTable,
			DatabaseRegistryEntry testDbre,
			DatabaseRegistryEntry masterDbRe
		) {
		return checkAllRowsInTableIfInDnaCompara(controlledTableToTest, masterTable, testDbre, masterDbRe);
	}
		
	protected boolean allDnaFragForSpeciesInComparaMaster(
			final String controlledTableToTest,
			final String masterTable,
			DatabaseRegistryEntry testDbre,
			DatabaseRegistryEntry masterDbRe,
			String speciesName
) {
		//checkRangeOfRowsInTable
		
		
		final Logger logger = getLogger();
		
		final Connection testDbConn = testDbre.getConnection();
		final Connection masterconn = masterDbRe.getConnection();

		final SqlTemplate sqlTemplateTestDb        = getSqlTemplate(testDbConn);  

		int genomeDbId = sqlTemplateTestDb.queryForDefaultObject(
				"select genome_db_id from genome_db where name='" + speciesName + "'",
				Integer.class
			);
		
		String whereClause = " where genome_db_id =  " + genomeDbId + " ";

		int rowCount = sqlTemplateTestDb.queryForDefaultObject(
			"select count(*) from " + controlledTableToTest + whereClause,
			Integer.class
		);
		
		logger.info("Number of rows in table: " + rowCount + "with genome_db_id="+ genomeDbId + "("+speciesName+")");
		
		final List<String> testTableColumns = getColumnsOfTable(testDbConn, controlledTableToTest);		
		final List<String> masterColumns    = getColumnsOfTable(masterconn, masterTable);		
		
		boolean masterHasAllNecessaryColumns = columnsAreSubset(
				testDbConn,
				masterconn,
				controlledTableToTest
		);		
		
		logger.log(Level.INFO, "Checking if columns are compatible");
		
		if (!masterHasAllNecessaryColumns) {
			
			testTableColumns.removeAll(masterColumns);			
			ReportManager.problem(
				this, 
				testDbConn, 
				"The following columns are not present in the master database: "
				+ testTableColumns + "\n"
				+ "The schemas are not compatible.\n"
			);
			return false;
		} else {
			logger.log(Level.INFO, "Columns are ok.");
		}
		
		int limit = batchSize;
		boolean allRowsInMaster = true;
		
		for(int currentOffset = 0; currentOffset<rowCount && !numReportedRowsExceedsMaximum(); currentOffset+=limit) {
			
			logger.info("Checking rows " + currentOffset + " out of " + rowCount + " for species " + speciesName);
			
			allRowsInMaster &= checkRangeOfRowsInTable(
				controlledTableToTest,
				masterTable,
				testDbre,
				masterDbRe,
				whereClause,
				limit,
				currentOffset
			);			
		}
		return allRowsInMaster;
	}
	
	protected boolean checkAllRowsInTableIfInDnaCompara(
			final String controlledTableToTest,
			final String masterTable,
			DatabaseRegistryEntry testDbre,
			DatabaseRegistryEntry masterDbRe
		) {
		
		final Connection testDbConn = testDbre.getConnection();		
		final SqlTemplate sqlTemplateTestDb = getSqlTemplate(testDbConn);
		
		List<String> speciesNameInComparaDb = sqlTemplateTestDb.queryForDefaultObjectList( 
			"select distinct genome_db.name from dnafrag join genome_db using (genome_db_id) order by genome_db.name;",
			String.class
		);
		
		boolean allSpeciesPass = true;

		for (String currentSpeciesNameInComparaDb : speciesNameInComparaDb) {
			
			boolean currentSpeciesPasses = true;
			
			if (speciesConfiguredForDnaCompara(currentSpeciesNameInComparaDb)) {
				
				currentSpeciesPasses = allDnaFragForSpeciesInComparaMaster(
					controlledTableToTest,
					masterTable,
					testDbre,
					masterDbRe,
					currentSpeciesNameInComparaDb
				);
				allSpeciesPass = allSpeciesPass && currentSpeciesPasses;
				
			} else {
				logger.info("Skipping " + currentSpeciesNameInComparaDb + ", because it is not configured for a dna compara method.");
			}
			
			if (!currentSpeciesPasses) {
				ReportManager.problem(this, testDbConn, "Species " + currentSpeciesNameInComparaDb + " has failed.");
			}
			
		}
		return allSpeciesPass;
	}
}
