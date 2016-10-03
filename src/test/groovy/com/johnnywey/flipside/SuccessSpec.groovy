package com.johnnywey.flipside

import com.johnnywey.flipside.failable.Fail
import com.johnnywey.flipside.failable.Success
import spock.lang.Specification

class SuccessSpec extends Specification {

    def "test success"() {
        setup:
        def successMsg = "mySuccess"
        def unit = Success.of(successMsg)

        expect:
        unit.isSuccess()
        unit.reason.equals(Fail.SUCCESS)
        unit.detail.equals(Fail.SUCCESS.name())
        unit.get().equals(successMsg)
        unit.toDidItWork().isSuccess()
    }

    def "test success with consumer"() {
        setup:
        def successMsg = "mySuccess"
        def result = "not set"
        Success.of(successMsg).ifSuccess({ m -> result = successMsg })

        expect:
        result == successMsg
    }

    def "test success with mapping"() {
        setup:
        def successMsg = "mySuccess"
        Double result = Success.of(successMsg).map({s -> 23d}).orElse(24d);

        expect:
        result == 23d
    }
}
