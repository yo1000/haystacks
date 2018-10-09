package com.yo1000.haystacks.core.service

import com.yo1000.haystacks.core.entity.FoundNames
import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.entity.Table
import com.yo1000.haystacks.core.entity.TableNames
import com.yo1000.haystacks.core.repository.IndexRepository
import com.yo1000.haystacks.core.repository.NoteRepository
import com.yo1000.haystacks.core.repository.TableRepository
import com.yo1000.haystacks.core.valueobject.FullyQualifiedName
import com.yo1000.haystacks.core.valueobject.LogicalName
import com.yo1000.haystacks.core.valueobject.Statement
import com.yo1000.haystacks.core.valueobject.TablePhysicalName
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito
import org.mockito.internal.verification.Times

class TableServiceTests {
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = [
        "'t1, t2' ; 'TableOne, TableTwo' ; 's1.t1, s1.t2' ; '3, 11' ; '123, 456' ; '1, 0' ; '0, 1'",
        "'t1, t2' ; 'TableA,   TableB'   ; 'sA.t1, sA.t2' ; '5, 7'  ; '78,  90'  ; '0, 0' ; '0, 0'"
    ])
    fun `Given a TableService when invoke getTableOutlines then should return list of TableOutline`(
            physicalNamesString: String,
            logicalNamesString: String,
            fullyQualifiedName: String,
            columnCountsString: String,
            rowCountsString: String,
            childrenCountsString: String,
            parentCountsString: String
    ) {
        // Given
        fun String.splitByComma(): List<String> = this.split(Regex("[ ]*,[ ]*"))
        val physicalNames = physicalNamesString.splitByComma()
        val logicalNames = logicalNamesString.splitByComma()
        val fullyQualifiedNames = fullyQualifiedName.splitByComma()
        val columnCounts = columnCountsString.splitByComma().map { it.toInt() }
        val rowCounts = rowCountsString.splitByComma().map { it.toLong() }
        val childrenCounts = childrenCountsString.splitByComma().map { it.toInt() }
        val parentCounts = parentCountsString.splitByComma().map { it.toInt() }

        val namePairs = physicalNames.mapIndexed { i, s -> TableNames(
                TablePhysicalName(s), LogicalName(logicalNames[i]), FullyQualifiedName(fullyQualifiedNames[i])) }
        val columnCountMap = physicalNames.mapIndexed { i, s -> TablePhysicalName(s) to columnCounts[i] }.toMap()
        val rowCountMap = physicalNames.mapIndexed { i, s -> TablePhysicalName(s) to rowCounts[i] }.toMap()
        val childrenCountMap = physicalNames.mapIndexed { i, s -> TablePhysicalName(s) to childrenCounts[i] }.toMap()
        val parentCountMap = physicalNames.mapIndexed { i, s -> TablePhysicalName(s) to parentCounts[i] }.toMap()

        val tableRepositoryMock = Mockito.mock(TableRepository::class.java)
        val indexRepositoryMock = Mockito.mock(IndexRepository::class.java)
        val noteRepositoryMock = Mockito.mock(NoteRepository::class.java)
        val tableService = TableService(
                tableRepositoryMock.also {
                    Mockito.doReturn(namePairs).`when`(it).findTableNamesAll()
                    Mockito.doReturn(columnCountMap).`when`(it).findColumnCountMap()
                    Mockito.doReturn(rowCountMap).`when`(it).findRowCountMap()
                    Mockito.doReturn(childrenCountMap).`when`(it).findReferencedCountFromChildrenMap()
                    Mockito.doReturn(parentCountMap).`when`(it).findReferencingCountToParentMap()
                },
                indexRepositoryMock,
                noteRepositoryMock
        )

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
        Mockito.verify(tableRepositoryMock, Times(1)).findColumnCountMap()
        Mockito.verify(tableRepositoryMock, Times(1)).findRowCountMap()
        Mockito.verify(tableRepositoryMock, Times(1)).findReferencedCountFromChildrenMap()
        Mockito.verify(tableRepositoryMock, Times(1)).findReferencingCountToParentMap()
        Mockito.verify(tableRepositoryMock, Times(1)).findTableNamesAll()
    }

    @Test
    fun `Given a TableService when invoke getTable by TablePhysicalName then should return Table object`() {
        // Given
        val table = Mockito.mock(Table::class.java)
        val tableRepositoryMock = Mockito.mock(TableRepository::class.java)
        val indexRepositoryMock = Mockito.mock(IndexRepository::class.java)
        val noteRepositoryMock = Mockito.mock(NoteRepository::class.java)
        val tableService = TableService(
                tableRepositoryMock.also {
                    Mockito.doReturn(table).`when`(it).findTable(any(TablePhysicalName::class.java))
                },
                indexRepositoryMock,
                noteRepositoryMock
        )

        // When
        val actual = tableService.getTable(TablePhysicalName("any"))

        // Then
        Assertions.assertSame(table, actual)
        Mockito.verify(tableRepositoryMock, Times(1))
                .findTable(any(TablePhysicalName::class.java))
    }

    @Test
    fun `Given a TableService when invoke getIndexes by TablePhysicalName then should return Index list`() {
        // Given
        val indexes = listOf(Mockito.mock(Index::class.java), Mockito.mock(Index::class.java))
        val tableRepositoryMock = Mockito.mock(TableRepository::class.java)
        val indexRepositoryMock = Mockito.mock(IndexRepository::class.java)
        val noteRepositoryMock = Mockito.mock(NoteRepository::class.java)
        val tableService = TableService(
                tableRepositoryMock,
                indexRepositoryMock.also {
                    Mockito.doReturn(indexes).`when`(it).findByTableName(any(TablePhysicalName::class.java))
                },
                noteRepositoryMock
        )

        // When
        val actual = tableService.getIndexes(TablePhysicalName("any"))

        // Then
        Assertions.assertSame(indexes, actual)
        Mockito.verify(indexRepositoryMock, Times(1))
                .findByTableName(any(TablePhysicalName::class.java))
    }

    @Test
    fun `Given a TableService when invoke getStatement by TablePhysicalName then should return Statement object`() {
        // Given
        val statement = Mockito.mock(Statement::class.java)
        val tableRepositoryMock = Mockito.mock(TableRepository::class.java)
        val indexRepositoryMock = Mockito.mock(IndexRepository::class.java)
        val noteRepositoryMock = Mockito.mock(NoteRepository::class.java)
        val tableService = TableService(
                tableRepositoryMock.also {
                    Mockito.doReturn(statement).`when`(it).findStatementByName(any(TablePhysicalName::class.java))
                },
                indexRepositoryMock,
                noteRepositoryMock
        )

        // When
        val actual = tableService.getStatement(TablePhysicalName("any"))

        // Then
        Assertions.assertSame(statement, actual)
        Mockito.verify(tableRepositoryMock, Times(1))
                .findStatementByName(any(TablePhysicalName::class.java))
    }

    @Test
    fun `Given a TableService when invoke find by query strings then should return FoundNames list`() {
        // Given
        val foundNamesList = listOf(Mockito.mock(FoundNames::class.java), Mockito.mock(FoundNames::class.java))
        val tableRepositoryMock = Mockito.mock(TableRepository::class.java)
        val indexRepositoryMock = Mockito.mock(IndexRepository::class.java)
        val noteRepositoryMock = Mockito.mock(NoteRepository::class.java)
        val tableService = TableService(
                tableRepositoryMock.also {
                    Mockito.doReturn(foundNamesList).`when`(it).findNames(Mockito.anyString(), Mockito.anyString())
                },
                indexRepositoryMock,
                noteRepositoryMock
        )

        // When
        val actual = tableService.find("any", "some")

        // Then
        Assertions.assertSame(foundNamesList, actual)
        Mockito.verify(tableRepositoryMock, Times(1))
                .findNames(Mockito.anyString(), Mockito.anyString())
    }

    private fun <T> any(clazz: Class<T>): T {
        return Mockito.any(clazz)
    }
}
