package com.johnnywey.flipside.failable;

import com.johnnywey.flipside.marker.DidNotWork;
import com.johnnywey.flipside.marker.DidItWork;
import com.sun.org.apache.bcel.internal.generic.FADD;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Something failed.
 */
public final class Failed<T> implements Failable<T> {

    private final Fail reason;
    private final String detail;

    public Failed(final Fail reason, final String detail) {
        this.reason = reason;
        this.detail = detail;
    }

    public static <T> Failed<T> of(final Fail reason, final String detail) {
        return new Failed<>(reason, detail);
    }

    @Override
    public T get() {
        throw new FailableException(this);
    }

    @Override
    public Boolean isSuccess() {
        return false;
    }

    @Override
    public Fail getReason() {
        return reason;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return reason + ": " + detail;
    }

    @Override
    public DidItWork toDidItWork() {
        return new DidNotWork(this.reason, this.detail);
    }

    @Override
    public Failable<T> filter(Predicate<? super T> predicate) {
        return this;
    }

    @Override
    public <U> Failable<U> map(Function<? super T, ? extends U> mapper) {
        throw new FailableException(this);
    }

    @Override
    public <U> Failable<U> flatMap(Function<? super T, Failable<U>> mapper) {
        throw new FailableException(this);
    }

    @Override
    public T orElse(T other) {
        throw new FailableException(this);
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        throw new FailableException(this);
    }
}
