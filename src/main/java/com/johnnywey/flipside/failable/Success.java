package com.johnnywey.flipside.failable;

import com.johnnywey.flipside.marker.Worked;
import com.johnnywey.flipside.marker.DidItWork;

/**
 * Something succeeded.
 */
public class Success<T> implements Failable<T> {
    private final T result;

    public Success(final T result) {
        this.result = result;
    }

    @Override
    public T get() {
        return result;
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
}
