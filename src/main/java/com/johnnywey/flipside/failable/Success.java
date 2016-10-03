package com.johnnywey.flipside.failable;

import com.johnnywey.flipside.marker.Worked;
import com.johnnywey.flipside.marker.DidItWork;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Something succeeded.
 */
public class Success<T> implements Failable<T> {
    private final T result;
    private static final Success<?> EMPTY = new Success<>(null);

    public Success(final T result) {
        this.result = result;
    }

    @Override
    public T get() {
        return result;
    }

    public static <T> Success<T> of(final T result) {
        return new Success<>(result);
    }

    public static<T> Success<T> empty() {
        @SuppressWarnings("unchecked")
        Success<T> t = (Success<T>) EMPTY;
        return t;
    }

    @Override
    public Boolean isSuccess() {
        return true;
    }

    @Override
    public Fail getReason() {
        return Fail.SUCCESS;
    }

    @Override
    public String getDetail() {
        return Fail.SUCCESS.name();
    }

    @Override
    public DidItWork toDidItWork() {
        return new Worked();
    }

    @Override
    public Failable<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isSuccess()) {
            return this;
        } else {
            return predicate.test(get()) ? this : empty();
        }
    }

    @Override
    public <U> Failable<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isSuccess())
            return empty();
        else {
            return Success.of(mapper.apply(get()));
        }
    }

    @Override
    public <U> Failable<U> flatMap(Function<? super T, Failable<U>> mapper)  {
        Objects.requireNonNull(mapper);
        if (!isSuccess())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(get()));
        }
    }
}
