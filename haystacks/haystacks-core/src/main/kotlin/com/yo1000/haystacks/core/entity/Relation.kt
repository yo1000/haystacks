package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.core.valueobject.TablePhysicalName

/**
 * @author yo1000
 */
data class Relation(
        val tablePhysicalName: TablePhysicalName,
        val columnPhysicalName: ColumnPhysicalName
)
