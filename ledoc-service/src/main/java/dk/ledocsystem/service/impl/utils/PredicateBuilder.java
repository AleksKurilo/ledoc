package dk.ledocsystem.service.impl.utils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PredicateBuilder {

    public Predicate toStringPredicate(Pair<StringPath, String> pair) {

        return ExpressionUtils.anyOf(
                pair.getLeft().containsIgnoreCase(pair.getRight())
        );
    }

    public Predicate toNumberPredicate(Pair<String, String> pair, Class source) {
        PathBuilder<T> pathBuilder = new PathBuilder(source, source.getSimpleName().toLowerCase());
        return ExpressionUtils.anyOf(
                pathBuilder.getNumber(pair.getLeft(), Long.class).stringValue().containsIgnoreCase(pair.getRight())
        );
    }
}
