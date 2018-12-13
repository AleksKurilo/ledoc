package dk.ledocsystem.data.util;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.querydsl.binding.MultiValueBinding;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class LocalDateMultiValueBinding implements MultiValueBinding<Path<LocalDate>, LocalDate> {

    @Override
    public Optional<Predicate> bind(Path<LocalDate> path, Collection<? extends LocalDate> value) {
        if (value.size() > 2) {
            return Optional.empty();
        }

        Iterator<? extends LocalDate> iterator = value.iterator();
        if (value.size() == 1) {
            return Optional.of(Expressions.asDate(path).eq(iterator.next()));
        }

        LocalDate from = iterator.next();
        LocalDate to = iterator.next();
        return Optional.of(Expressions.asDate(path).between(from, to));
    }
}
