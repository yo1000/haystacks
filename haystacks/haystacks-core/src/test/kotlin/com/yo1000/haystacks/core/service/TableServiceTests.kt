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
        "'t1=TableOne, t2=TableTwo'; 't1=3, t2=11'; 't1=123, t2=456'; 't1=1, t2=0'; 't1=0, t2=1'",
        "'t1=TableA,   t2=TableB'  ; 't1=5, t2=7' ; 't1=78,  t2=90' ; 't1=0, t2=0'; 't1=0, t2=0'"
    ])
    fun `Given a TableService when invoke getTableOutlines then return list of TableOutline`(
            namePairsString: String,
            columnCountMapString: String,
            rowCountMapString: String,
            childrenCountMapString: String,
            parentCountMapString: String
    ) {
        // Given
        val namePairs = namePairsString.split(Regex("[ ]*,[ ]*")).map {
            it.split("=").let {
                TableNames(
                        physicalName = TablePhysicalName(it[0]),
                        logicalName = LogicalName(it[1])
                )
            }
        }
        val columnCountMap = columnCountMapString.split(Regex("[ ]*,[ ]*")).map {
            it.split("=").let {
                TablePhysicalName(it[0]) to it[1].toInt()
            }
        }.toMap()
        val rowCountMap = rowCountMapString.split(Regex("[ ]*,[ ]*")).map {
            it.split("=").let {
                TablePhysicalName(it[0]) to it[1].toLong()
            }
        }.toMap()
        val childrenCountMap = childrenCountMapString.split(Regex("[ ]*,[ ]*")).map {
            it.split("=").let {
                TablePhysicalName(it[0]) to it[1].toInt()
            }
        }.toMap()
        val parentCountMap = parentCountMapString.split(Regex("[ ]*,[ ]*")).map {
            it.split("=").let {
                TablePhysicalName(it[0]) to it[1].toInt()
            }
        }.toMap()

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
