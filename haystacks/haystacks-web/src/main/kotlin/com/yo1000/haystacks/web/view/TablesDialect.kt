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

class TablesDialect : AbstractProcessorDialect(
        "Haystacks tables dialect",
        "hay",
        StandardDialect.PROCESSOR_PRECEDENCE
), IExpressionObjectDialect {
    companion object {
        const val EXPRESSION_NAME = "tables"
    }

    override fun getProcessors(p0: String?): MutableSet<IProcessor> = mutableSetOf(
            StandardXmlNsTagProcessor(TemplateMode.HTML, prefix)
    )

    override fun getExpressionObjectFactory(): IExpressionObjectFactory = object : IExpressionObjectFactory {
        override fun buildObject(context: IExpressionContext?, expressionObjectName: String?): Any? =
                expressionObjectName.takeIf { it == EXPRESSION_NAME }?.let { Tables() }

        override fun isCacheable(expressionObjectName: String?): Boolean = false

        override fun getAllExpressionObjectNames(): MutableSet<String> =
                mutableSetOf(EXPRESSION_NAME)

    }
}

class Tables {
    fun isPrimaryKey(column: Table.Column, indexes: List<Table.Index>): Boolean =
            indexes.find { it.columns.any { it.column == column.name } }?.type == "PRIMARY"

    fun isForeignKey(column: Table.Column): Boolean = column.parent != null
}
