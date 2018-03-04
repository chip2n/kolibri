import com.chip2n.kolibri.Store
import io.kotlintest.specs.StringSpec

data class TestState(val value: Int) {
    fun increment() = copy(value = value + 1)
}
object TestEvent

class StorageTest : StringSpec() {
    init {
        "observe initial state" {
            val initialState = TestState(value = 0)
            val store = Store<TestState, TestEvent>(
                    initial = initialState,
                    reducer = { state, _ -> state.increment() }
            )
            val observer = store.state.test()

            observer.awaitCount(1)
            observer.assertValue(TestState(value = 0))
        }
        "observe reduced state" {
            val initialState = TestState(value = 0)
            val store = Store<TestState, TestEvent>(
                    initial = initialState,
                    reducer = { state, _ -> state.increment() }
            )

            val observer = store.state.test()
            store.send(TestEvent)

            observer.awaitCount(2)
            observer.assertValues(
                    TestState(value = 0),
                    TestState(value = 1)
            )
        }
    }
}
