package com.yo1000.haystacks.core.service

import com.yo1000.haystacks.core.entity.TableNames
import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.core.valueobject.LogicalName
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito

class TableServiceTests {
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = [
        "'t1, t2' ; 'TableOne, TableTwo' ; '3, 11' ; '123, 456' ; '1, 0' ; '0, 1'",
        "'t1, t2' ; 'TableA,   TableB'   ; '5, 7'  ; '78,  90'  ; '0, 0' ; '0, 0'"
    ])
    fun `Given a TableService when invoke getTableOutlines then return list of TableOutline`(
            physicalNamesString: String,
            logicalNamesString: String,
            columnCountsString: String,
            rowCountsString: String,
            childrenCountsString: String,
            parentCountsString: String
    ) {
        // Given
        fun String.splitByComma(): List<String> = this.split(Regex("[ ]*,[ ]*"))
        val physicalNames = physicalNamesString.splitByComma()
        val logicalNames = logicalNamesString.splitByComma()
        val columnCounts = columnCountsString.splitByComma().map { it.toInt() }
        val rowCounts = rowCountsString.splitByComma().map { it.toLong() }
        val childrenCounts = childrenCountsString.splitByComma().map { it.toInt() }
        val parentCounts = parentCountsString.splitByComma().map { it.toInt() }

        val namePairs = physicalNames.mapIndexed { i, s -> TableNames(TablePhysicalName(s), LogicalName(logicalNames[i])) }
        val columnCountMap = physicalNames.mapIndexed { i, s -> TablePhysicalName(s) to columnCounts[i] }.toMap()
        val rowCountMap = physicalNames.mapIndexed { i, s -> TablePhysicalName(s) to rowCounts[i] }.toMap()
        val childrenCountMap = physicalNames.mapIndexed { i, s -> TablePhysicalName(s) to childrenCounts[i] }.toMap()
        val parentCountMap = physicalNames.mapIndexed { i, s -> TablePhysicalName(s) to parentCounts[i] }.toMap()

        val tableRepository = Mockito.mock(TableRepository::class.java)
        Mockito.doReturn(namePairs).`when`(tableRepository).findTableNamesAll()
        Mockito.doReturn(columnCountMap).`when`(tableRepository).findColumnCountMap()
        Mockito.doReturn(rowCountMap).`when`(tableRepository).findRowCountMap()
        Mockito.doReturn(childrenCountMap).`when`(tableRepository).findReferencedCountFromChildrenMap()
        Mockito.doReturn(parentCountMap).`when`(tableRepository).findReferencingCountToParentMap()

        val indexRepository = Mockito.mock(IndexRepository::class.java)
        val tableService = TableService(tableRepository, indexRepository)

        // When
        val actual = tableService.getTableOutlines()

        // Then
        actual.forEachIndexed { i, outline ->
            val name = namePairs[i].physicalName
            Assertions.assertEquals(namePairs[i], outline.names)
            Assertions.assertEquals(columnCountMap[name], outline.columnCount)
            Assertions.assertEquals(rowCountMap[name], outline.rowCount)
            Assertions.assertEquals(childrenCountMap[name], outline.referencedCountFromChildren)
            Assertions.assertEquals(parentCountMap[name], outline.referencingCountToParent)
        }
    }
}
