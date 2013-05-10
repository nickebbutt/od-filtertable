
  Feature: Suffix Tree
    
    Scenario: Add items top level
      Given I create a suffix tree
      When I add a value VAL1 under key A
      And I add a value VAL2 under key B
      Then a search for A returns VAL1
      And a search for B returns VAL2
      
    Scenario: Add items with long labels
      Given I create a suffix tree
      When I add a value VAL1 under key ALABEL
      And I add a value VAL2 under key BLABEL
      Then a search for ALABEL returns VAL1
      And a search for BLABEL returns VAL2
      
    Scenario: Search items by prefix
      Given I create a suffix tree
      When I add a value VAL1 under key ALABEL
      And I add a value VAL2 under key BLABEL
      Then a search for A returns VAL1
      And a search for B returns VAL2
      
    Scenario: Search returns results in alphabetical order
      Given I create a suffix tree
      When I add a value VAL1 under key AB
      And I add a value VAL2 under key AA
      Then a search for A returns VAL2, VAL1
      
     
      
