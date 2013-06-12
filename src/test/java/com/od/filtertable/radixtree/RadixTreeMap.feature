
  Feature: Radix Tree Map

    Background:
      Given I create a radix tree
    
    Scenario: Add items top level
      When I add a value VAL1 under key A
      And I add a value VAL2 under key B
      Then a search for A returns VAL1
      And a search for B returns VAL2
    
    Scenario: Add items with long labels
      When I add a value VAL1 under key ALABEL
      And I add a value VAL2 under key BLABEL
      Then a search for ALABEL returns VAL1
      And a search for BLABEL returns VAL2
      
    Scenario: Search items by prefix
      When I add a value VAL1 under key ALABEL
      And I add a value VAL2 under key BLABEL
      Then a search for A returns VAL1
      And a search for B returns VAL2
      
    Scenario: Search returns results in alphabetical order
      When I add a value VAL1 under key AB
      And I add a value VAL2 under key AA
      Then a search for A returns VAL2, VAL1

    Scenario: Search finds all substrings
      When I add a value VAL1 under key ABA
      And I add a value VAL2 under key AAA
      And I add a value VAL3 under key AA
      And I add a value VAL4 under key BA
      Then the radix tree contains keys AA, AAA, ABA, BA
      And the number of nodes is 6
      And a search for A returns VAL3, VAL2, VAL1
      And a search for AA returns VAL3, VAL2
      And a search for AB returns VAL1
      And a search for B returns VAL4
      And a search for BA returns VAL4
      
    Scenario: Adding items out of alphabetical order
      When I add a value VAL1 under key AC
      And I add a value VAL2 under key AB
      And I add a value VAL3 under key AA
      Then the radix tree contains keys AA, AB, AC
      And the number of nodes is 4
      And a search for A returns VAL3, VAL2, VAL1

    Scenario: I can remove a value
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABCD
      And I remove the value under key ABC
      Then the removed value was VAL1
      Then a search for ABC returns VAL2
      And the number of nodes is 1

    Scenario: Add and remove an item with a substring key
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key AB
      And a search for A returns VAL2, VAL1
      And I remove the value under key AB
      Then a search for A returns VAL1  

    Scenario: Removing a value which does not exist has no effect
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABCD
      And I remove the value under key A
      Then the removed value was null
      Then a search for ABC returns VAL1, VAL2
      And the number of nodes is 3
  
    Scenario: When I remove last value empty terminal node is removed
      When I add a value VAL1 under key ABC
      And I remove the value under key ABC
      And the number of nodes is 0

    Scenario: Removing all values compresses node structure
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABD
      Then the number of nodes is 3
      And I remove the value under key ABC
      And I remove the value under key ABD
      Then the number of nodes is 0
  
    Scenario: Removing all values compresses node structure complex
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABD
      And I add a value VAL3 under key AB
      And the number of nodes is 4
      And I remove the value under key ABC
      And the number of nodes is 3
      And a search for A returns VAL3, VAL2 
      And I remove the value under key ABD
      And the number of nodes is 1
      And a search for A returns VAL3
      And I remove the value under key AB
      Then the number of nodes is 0
  
    Scenario: Getting a limited number of results
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABD
      And I add a value VAL3 under key AB
      Then a search for A returns VAL3, VAL1, VAL2
      And a search with maxItems=2 for A returns VAL3, VAL1

  #And I show the tree structure
      

      
      
      
     
      
