package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.LogicalName
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName

/**
 *
 * @author yo1000
 */
class TableNamedness(
        tablePhysicalName: TablePhysicalName,
        logicalName: LogicalName
) : Namedness(
        tablePhysicalName,
        logicalName
)
