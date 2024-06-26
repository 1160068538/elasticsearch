/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */
package org.elasticsearch.xpack.esql.core.expression.function.scalar;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.xpack.esql.core.expression.Expression;
import org.elasticsearch.xpack.esql.core.expression.gen.processor.Processor;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.esql.core.util.PlanStreamInput;
import org.elasticsearch.xpack.esql.core.util.PlanStreamOutput;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;

public abstract class UnaryScalarFunction extends ScalarFunction {

    private final Expression field;

    protected UnaryScalarFunction(Source source, Expression field) {
        super(source, singletonList(field));
        this.field = field;
    }

    protected UnaryScalarFunction(StreamInput in) throws IOException {
        this(Source.readFrom((StreamInput & PlanStreamInput) in), ((PlanStreamInput) in).readExpression());
    }

    @Override
    public final void writeTo(StreamOutput out) throws IOException {
        source().writeTo(out);
        ((PlanStreamOutput) out).writeExpression(field);
    }

    @Override
    public final UnaryScalarFunction replaceChildren(List<Expression> newChildren) {
        return replaceChild(newChildren.get(0));
    }

    protected abstract UnaryScalarFunction replaceChild(Expression newChild);

    public Expression field() {
        return field;
    }

    protected abstract Processor makeProcessor();

    @Override
    public boolean foldable() {
        return field.foldable();
    }

    @Override
    public Object fold() {
        return makeProcessor().process(field().fold());
    }
}
