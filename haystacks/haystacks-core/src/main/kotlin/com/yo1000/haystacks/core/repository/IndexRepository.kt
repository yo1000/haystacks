package com.yo1000.haystacks.core.repository

import com.yo1000.haystacks.core.entity.Index
import com.yo1000.haystacks.core.valueobject.TablePhysicalName

/**
 * @author yo1000
 */
interface IndexRepository {
    fun findByTableName(name: TablePhysicalName): List<Index>
}
