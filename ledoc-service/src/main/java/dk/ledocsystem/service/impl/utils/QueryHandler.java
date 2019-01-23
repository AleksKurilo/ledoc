package dk.ledocsystem.service.impl.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueryHandler {
    public JPAQuery sortPageableQuery(JPAQuery query, Pageable pageable, EntityPathBase<?> qEntity) {
        List<Sort.Order> sorts = pageable.getSort().get().collect(Collectors.toList());

        List<OrderSpecifier> sortParams = new LinkedList<>();
        if (sorts.size() > 0) {
            sorts.forEach(order -> {
                sortParams.add(new OrderSpecifier(Order.valueOf(order.getDirection().name()), Expressions.stringPath(qEntity, order.getProperty())));
            });

            query.orderBy(sortParams.toArray(new OrderSpecifier[sortParams.size()]));
        }

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        return query;
    }
}
