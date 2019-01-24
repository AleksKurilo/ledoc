package dk.ledocsystem.service.impl.utils;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PredicateBuilderAndParser {

    public Predicate toPredicate(Pair<? extends Path, String> pair) {
        Path<?> path = pair.getLeft();
        StringExpression targetValue = null;
        if (path instanceof EnumPath) {
            targetValue = ((EnumPath) path).stringValue();
        } else if (path instanceof NumberPath) {
            targetValue = ((NumberPath) path).stringValue();
        } else if (path instanceof StringPath) {
            targetValue = ((StringPath) path);
        }
        return Expressions.anyOf(
                targetValue.containsIgnoreCase(pair.getRight())
        );
    }
}
