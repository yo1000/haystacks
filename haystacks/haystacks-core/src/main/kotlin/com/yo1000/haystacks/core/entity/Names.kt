package com.yo1000.haystacks.core.entity

import com.yo1000.haystacks.core.valueobject.LogicalName
import com.yo1000.haystacks.core.valueobject.PhysicalName


/**
 *
 * @author yo1000
 */
open class Names(
        val physicalName: PhysicalName,
        val logicalName: LogicalName
)
