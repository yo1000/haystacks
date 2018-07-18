package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName

/**
 * @author yo1000
 */
data class Relation(
        val tablePhysicalName: TablePhysicalName,
        val columnPhysicalName: ColumnPhysicalName
)
