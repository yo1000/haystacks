package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.ColumnPhysicalName
import com.yo1000.haystacks.core.valueobject.LogicalName

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
