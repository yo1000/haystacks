package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.domain.valueobject.LogicalName

/**
 *
 * @author yo1000
 */
class ColumnNames(
        physicalName: ColumnPhysicalName,
        logicalName: LogicalName
) : Names(
        physicalName,
        logicalName
)
