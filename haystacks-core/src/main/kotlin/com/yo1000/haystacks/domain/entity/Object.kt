package com.yo1000.haystacks.domain.entity

import com.yo1000.haystacks.domain.valueobject.Comment
import com.yo1000.haystacks.domain.valueobject.ObjectName


/**
 *
 * @author yo1000
 */
open class Object(
        val name: ObjectName,
        val comment: Comment
)
