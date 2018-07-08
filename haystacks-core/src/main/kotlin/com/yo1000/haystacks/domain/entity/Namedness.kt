package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.LogicalName
import com.yo1000.haystacks.domain.valueobject.PhysicalName


/**
 *
 * @author yo1000
 */
open class Namedness(
        val physicalName: PhysicalName,
        val logicalName: LogicalName
)
