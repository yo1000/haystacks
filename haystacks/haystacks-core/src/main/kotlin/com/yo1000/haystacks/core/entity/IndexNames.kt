package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.IndexPhysicalName
import com.yo1000.haystacks.core.valueobject.LogicalName

/**
 *
 * @author yo1000
 */
class IndexNames(
        physicalName: IndexPhysicalName,
        logicalName: LogicalName
) : Names(
        physicalName,
        logicalName
)
