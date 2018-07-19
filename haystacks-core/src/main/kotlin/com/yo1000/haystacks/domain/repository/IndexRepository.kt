package com.yo1000.haystacks.domain.repository

import com.yo1000.haystacks.domain.entity.Index
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName

/**
 * @author yo1000
 */
interface IndexRepository {
    fun findByTableName(name: TablePhysicalName): List<Index>
}
