Feature: Read/Get Function Positive and Negative Test Cases

  @wip
  Scenario: verify getting right item with valid values
    Given User adds a product to cart
    When User places an order
    And Shipping Info entered
    Then status should be success
    And Get Token from CKO
    And Complete Order



