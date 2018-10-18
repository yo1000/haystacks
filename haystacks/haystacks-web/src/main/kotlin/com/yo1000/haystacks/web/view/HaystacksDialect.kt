package com.yo1000.haystacks.web.view

import com.yo1000.haystacks.web.resource.Table
import org.thymeleaf.context.IExpressionContext
import org.thymeleaf.dialect.AbstractProcessorDialect
import org.thymeleaf.dialect.IExpressionObjectDialect
import org.thymeleaf.expression.IExpressionObjectFactory
import org.thymeleaf.processor.IProcessor
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor
import org.thymeleaf.templatemode.TemplateMode

class HaystacksDialect : AbstractProcessorDialect(
        "Haystacks dialect",
        "hay",
        StandardDialect.PROCESSOR_PRECEDENCE
), IExpressionObjectDialect {
    companion object {
        const val TABLES_EXPRESSION = "tables"
        const val SEARCH_EXPRESSION = "search"
    }

    override fun getProcessors(p0: String?): MutableSet<IProcessor> = mutableSetOf(
            StandardXmlNsTagProcessor(TemplateMode.HTML, prefix)
    )

    override fun getExpressionObjectFactory(): IExpressionObjectFactory = object : IExpressionObjectFactory {
        override fun buildObject(context: IExpressionContext?, expressionObjectName: String?): Any? =
                when (expressionObjectName) {
                    TABLES_EXPRESSION -> Tables()
                    SEARCH_EXPRESSION -> Search()
                    else -> null
                }

        override fun isCacheable(expressionObjectName: String?): Boolean = false

        override fun getAllExpressionObjectNames(): MutableSet<String> =
                mutableSetOf(TABLES_EXPRESSION, SEARCH_EXPRESSION)
    }
}

class Tables {
    fun isPrimaryKey(column: Table.Column, indexes: List<Table.Index>): Boolean =
            indexes.find { it.columns.any { it.column == column.name } }?.type == "PRIMARY"

    fun isForeignKey(column: Table.Column): Boolean = column.parent != null
}

class Search {
    fun containsAnyQuery(s: String, queries: List<String>): Boolean = queries.any { s.contains(it) }
}