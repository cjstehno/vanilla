package com.stehno.vanilla.jdbc.sql.transform

import groovy.transform.Immutable
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

/**
 * Created by cjstehno on 1/14/16.
 */
@GroovyASTTransformation(phase = CANONICALIZATION) @TypeChecked
class InjectSqlUpdateTransform extends AbstractASTTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode annotationNode = nodes[0] as AnnotationNode
        AnnotatedNode targetNode = nodes[1] as AnnotatedNode

        UpdateSqlModel model = new UpdateSqlModel(
            (annotationNode.getMember('value') as ConstantExpression).value as String,
            (annotationNode.getMember('source') as ConstantExpression).value as String,
            (annotationNode.getMember('extractor') as ClassExpression).type
        )

        // sql.executeUpdate(sql(), params())
    }
}

@Immutable
class UpdateSqlModel {
    String sql
    String source
    ClassNode extractor
}
