package com.johnnywey.flipside.matcher;

import com.johnnywey.flipside.ClientResponse;
import com.johnnywey.flipside.box.Box;
import com.johnnywey.flipside.failable.Fail;
import com.johnnywey.flipside.failable.Failable;
import com.johnnywey.flipside.failable.Failed;
import com.johnnywey.flipside.marker.DidItWork;

import java.util.function.Function;

public class Matcher {
    private final static String SUCCESS_PATTERN = "2/d{2}"; // only supports HTTP 2xx for now ...
    private final Object value;
    private Boolean awaitingMatch = true;

    public Matcher(Object valueIn) {
        value = valueIn;
    }

    /**
     * Match an incoming object on a type of match closure.
     * <p>
     * For example:
     * <p>
     * {@code match "test" on {matches "test", {println "Test matches!"}}}
     *
     * @param incoming The incoming object to match
     */
    public static Matcher match(Object incoming) {
        return new Matcher(incoming);
    }

    /**
     * Class Matcher.
     * <p>
     * Allows matches like:
     * {@code match "test" on {matches String, {println "Test matches!"}}}
     *
     * @param klazz    The class to match against
     * @param callback The closure to execute if the match is successful
     */
    public Matcher matches(Class klazz, Function callback) {
        if (awaitingMatch && this.value.getClass() == klazz) {
            call(callback);
        }
        return this;
    }

    /**
     * Object value Matcher.
     * <p>
     * Allows matches like:
     * {@code match 10 on {matches 10, {println "It matches!"}}}
     *
     * @param val     The object to match against
     * @param closure The closure to execute if the match is successful
     */
    public Matcher matches(Object val, Function closure) {
        if (awaitingMatch && val.equals(value)) {
            call(closure);
        }
        return this;
    }

    /**
     * Success closure. Will be executed when:
     * <ul>
     * <li>The result is a {@link com.johnnywey.flipside.failable.Failable} and is successful</li>
     * <li>The result is a {@link com.johnnywey.flipside.marker.DidItWork} and is successful</li>
     * <li>The result is an implementation of {@link com.johnnywey.flipside.ClientResponse} and the status code was an HTTP success</li>
     * </ul>
     *
     * @param callback The closure to execute if successful
     */
    public Matcher success(Function callback) {
        if (awaitingMatch && value instanceof Failable && ((Failable) value).isSuccess()) {
            call(callback, ((Failable) value).get());
        } else if (awaitingMatch && value instanceof DidItWork && ((DidItWork) value).isSuccess()) {
            call(callback);
        } else if (awaitingMatch && value instanceof ClientResponse && ((ClientResponse) value).getStatusCode().toString().matches(SUCCESS_PATTERN)) {
            call(callback, value);
        }
        return this;
    }

    /**
     * Failure closure. Will be executed when:
     * <ul>
     * <li>The result is a {@link Failable} and is not successful</li>
     * <li>The result is a {@link DidItWork} and is not successful</li>
     * <li>The result is an implementation of {@link ClientResponse} and the status code was not an HTTP success</li>
     * </ul>
     *
     * @param callback The closure to execute if successful
     */
    public Matcher failure(Function callback) {
        if (awaitingMatch && value instanceof Failable && !((Failable) value).isSuccess()) {
            call(callback);
        } else if (awaitingMatch && value instanceof DidItWork && !((DidItWork) value).isSuccess()) {
            call(callback);
        } else if (awaitingMatch && value instanceof ClientResponse && !SUCCESS_PATTERN.matches(((ClientResponse) value).getStatusCode().toString())) {
            // create a Fail out of the value
            ClientResponse clientResponse = (ClientResponse) value;
            Fail fail = Fail.fromHttpResponseCode(clientResponse.getStatusCode());
            if (fail == null) {
                fail = Fail.UNKNOWN;
            }
            call(callback, new Failed(fail, clientResponse.getStatusText()));
        }
        return this;
    }

    /**
     * Box closure. Will be executed when an incoming {@link com.johnnywey.flipside.box.Box} is full and the value will be passed in.
     *
     * @param callback The closure to execute if full
     */
    public Matcher some(Function callback) {
        if (awaitingMatch && value instanceof Box && !((Box) value).isEmpty()) {
            call(callback, ((Box) value).get());
        }

        return this;
    }

    /**
     * Box closure. Will be executed when an incoming {@link Box} is empty.
     *
     * @param callback The function to execute if empty
     */
    public Matcher none(Function callback) {
        if (awaitingMatch && value instanceof Box && ((Box) value).isEmpty()) {
            call(callback);
        }
        return this;
    }

    private void call(Function callback) {
        call(callback, value);
    }

    private Object call(Function callback, Object incoming) {
        awaitingMatch = false;
        return callback.apply(incoming);
    }
}