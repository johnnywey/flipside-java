package com.johnnywey.flipside

import com.johnnywey.flipside.failable.Fail
import com.johnnywey.flipside.failable.FailableException
import com.johnnywey.flipside.failable.Failed
import com.johnnywey.flipside.failable.Success
import spock.lang.Specification

class FailedSpec extends Specification {

    def "test failed"() {
        setup:
        def errMsg = "This is a test failed"
        def unit = Failed.of(Fail.BAD_REQUEST, errMsg)

        expect:
        !unit.isSuccess()
        unit.detail.matches(errMsg)
        unit.reason.equals(Fail.BAD_REQUEST)
        unit.toString().equals(Failed.of(Fail.BAD_REQUEST, errMsg).toString())
        unit.reason.httpResponseCode.equals(Fail.BAD_REQUEST.httpResponseCode)

        unit.toDidItWork().detail.matches(errMsg)
        unit.toDidItWork().reason.equals(Fail.BAD_REQUEST)
    }

    def "test exception"() {
        setup:
        def errMsg = "This is a test failed"
        def unit = Failed.of(Fail.BAD_REQUEST, errMsg)

        when:
        unit.get()

        then:
        thrown(FailableException)
    }

}
