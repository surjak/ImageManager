package com.image.manager.edgeserver.common

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class NumberUtilsTest {

    @Test
    void 'Should return proper amount of digits'() {
        assertEquals(3, NumberUtils.getDigits(111))
        assertEquals(3, NumberUtils.getDigits(110))
        assertEquals(3, NumberUtils.getDigits(100))
        assertEquals(2, NumberUtils.getDigits(11))
        assertEquals(2, NumberUtils.getDigits(10))
        assertEquals(1, NumberUtils.getDigits(1))
        assertEquals(1, NumberUtils.getDigits(0))
    }
}