package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.IndexPhysicalName
import com.yo1000.haystacks.domain.valueobject.LogicalName

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
