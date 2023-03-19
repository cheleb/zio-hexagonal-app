package currency.core

class CurrencyPairSpec extends munit.FunSuite {
  test("hello") {

    val currencyPair = CurrencyPair.create("EUR", "USD", 1.2)

    assertEquals(currencyPair.isRight, true)
  }
}
