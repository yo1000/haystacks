package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.LogicalName
import com.yo1000.haystacks.core.valueobject.TablePhysicalName

/**
 *
 * @author yo1000
 */
class TableNames(
        physicalName: TablePhysicalName,
        logicalName: LogicalName
) : Names(
        physicalName,
        logicalName
)
