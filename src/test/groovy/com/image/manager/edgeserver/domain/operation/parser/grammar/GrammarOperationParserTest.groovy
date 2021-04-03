package com.image.manager.edgeserver.domain.operation.parser.grammar

import com.image.manager.edgeserver.domain.operation.CropOperation
import com.image.manager.edgeserver.domain.operation.OperationFactory
import com.image.manager.edgeserver.domain.operation.ScaleOperation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils

class GrammarOperationParserTest {

    private GrammarOperationParser parser;

    @BeforeEach
    void setUp() {
        def operationFactory = new OperationFactory([new CropOperation.Factory(), new ScaleOperation.Factory()])
        this.parser = new GrammarOperationParser(operationFactory)
    }

    @Test
    void 'Should parse scale operation'() {
        // given:
        def query = 'op=scale&w=100&h=200'

        // when:
        def operations = parser.fromQuery(query)

        // then:
        assert operations
        assert operations.size() == 1

        def operation = operations[0]
        assert ReflectionTestUtils.getField(operation, 'w') == 100
        assert ReflectionTestUtils.getField(operation, 'h') == 200
    }

    @Test
    void 'Should parse crop operation'() {
        // given:
        def query = 'op=crop&x=1&y=2&w=10&h=20'

        // when:
        def operations = parser.fromQuery(query)

        // then:
        assert operations
        assert operations.size() == 1

        def operation = operations[0]
        assert ReflectionTestUtils.getField(operation, 'x') == 1
        assert ReflectionTestUtils.getField(operation, 'y') == 2
        assert ReflectionTestUtils.getField(operation, 'w') == 10
        assert ReflectionTestUtils.getField(operation, 'h') == 20
    }

    @Test
    void 'Should parse multiple operations'() {
        // given:
        def query = 'op=scale&w=1&h=1&op=crop&x=1&y=2&w=2&h=2&op=scale&w=3&h=3'

        // when:
        def operations = parser.fromQuery(query)

        // then:
        assert operations
        assert operations.size() == 3

        // assert first operation in query:
        assert operations.find { it -> ReflectionTestUtils.getField(it, 'w') == 1}

        // assert second operation in query:
        assert operations.find { it -> ReflectionTestUtils.getField(it, 'w') == 2}

        // assert last operation in query:
        assert operations.find { it -> ReflectionTestUtils.getField(it, 'w') == 3}
    }

}
