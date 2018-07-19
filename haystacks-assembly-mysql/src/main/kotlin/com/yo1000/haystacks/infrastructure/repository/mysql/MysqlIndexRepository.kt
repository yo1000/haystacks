package com.yo1000.haystacks.infrastructure.repository.mysql

import com.yo1000.haystacks.domain.entity.Index
import com.yo1000.haystacks.domain.repository.IndexRepository
import com.yo1000.haystacks.domain.valueobject.TablePhysicalName
import org.springframework.stereotype.Repository

/**
 * @author yo1000
 */
@Repository
class MysqlIndexRepository : IndexRepository {
    override fun findByTableName(name: TablePhysicalName): List<Index> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}