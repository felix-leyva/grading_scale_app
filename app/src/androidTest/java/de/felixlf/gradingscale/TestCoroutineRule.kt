package de.felixlf.gradingscale

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

//https://blog.mindorks.com/unit-testing-viewmodel-with-kotlin-coroutines-and-livedata
@ExperimentalCoroutinesApi
class TestCoroutineRule2 : TestRule {

  val testCoroutineDispatcher = TestCoroutineDispatcher()

  val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

  override fun apply(base: Statement, description: Description?) = object : Statement() {
    @Throws(Throwable::class)
    override fun evaluate() {
      Dispatchers.setMain(testCoroutineDispatcher)

      base.evaluate()

      Dispatchers.resetMain()
      testCoroutineScope.cleanupTestCoroutines()
    }
  }

  fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
    testCoroutineScope.runBlockingTest { block() }


}



@ExperimentalCoroutinesApi
class MainCoroutineRule(
  val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher(),
  val testScope :TestCoroutineScope = TestCoroutineScope(testDispatcher)
) : TestWatcher() {

  override fun starting(description: Description?) {
    super.starting(description)
    Dispatchers.setMain(testDispatcher)
  }

  override fun finished(description: Description?) {
    super.finished(description)
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
  }

  @ExperimentalCoroutinesApi
  fun runBlockingTest(block: suspend () -> Unit) =
    this.testDispatcher.runBlockingTest {
      block()
    }

}