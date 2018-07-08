package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.IndexPhysicalName
import com.yo1000.haystacks.domain.valueobject.LogicalName

/**
 *
 * @author yo1000
 */
class IndexNamednes(
        indexPhysicalName: IndexPhysicalName,
        logicalName: LogicalName
) : Namedness(
        indexPhysicalName,
        logicalName
)
