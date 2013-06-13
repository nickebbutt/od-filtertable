
  Feature: Suffix Tree Map

    Background:
      Given I create a suffix tree
    
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
      
    Scenario: Search items by suffix
      When I add a value VAL1 under key ALABEL
      And I add a value VAL2 under key BLABEL
      And I add a value VAL3 under key CLABE
      Then a search for BEL gives the set VAL1, VAL2

    Scenario: Search items by substring
      When I add a value VAL1 under key ALABEL
      And I add a value VAL2 under key BLABEL
      And I add a value VAL3 under key NADA
      Then a search for LAB gives the set VAL1, VAL2
      
    Scenario: Search finds all substrings
      When I add a value VAL1 under key ABA
      And I add a value VAL2 under key AAA
      And I add a value VAL3 under key AA
      And I add a value VAL4 under key BA
      Then the radix tree contains keys A, AA, AAA, ABA, BA
      And a search for A gives the set VAL1, VAL2, VAL3, VAL4
      And a search for AA gives the set VAL2, VAL3
      And a search for BA gives the set VAL1, VAL4

    Scenario: I can remove a value
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABCD
      And the number of nodes is 10
      And I remove the value under key ABC
      Then the removed value was VAL1
      Then a search for BC returns VAL2
      And the number of nodes is 4

    Scenario: Add and remove item with a substring key
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key AB
      And a search for A gives the set VAL2, VAL1
      And I remove the value under key AB
      Then a search for A returns VAL1  
  
    Scenario: Removing a value which does not exist has no effect
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABCD
      And I remove the value under key A
      Then the removed value was null
      Then a search for B gives the set VAL1, VAL2
  
    Scenario: When I remove last value empty terminal node is removed
      When I add a value VAL1 under key ABC
      And I remove the value under key ABC
      Then the number of nodes is 0

    Scenario: Removing all values compresses node structure
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABD
      Then the number of nodes is 8
      And I remove the value under key ABC
      And I remove the value under key ABD
      Then the number of nodes is 0
  
    Scenario: Removing all values compresses node structure complex
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABD
      And I add a value VAL3 under key AB
      And the number of nodes is 10
      And I remove the value under key ABC
      And the number of nodes is 7
      And a search for A gives the set VAL3, VAL2 
      And I remove the value under key ABD
      And the number of nodes is 2
      And a search for A returns VAL3
      And I remove the value under key AB
      Then the number of nodes is 0
  
    Scenario: Getting a limited number of results
      When I add a value VAL1 under key ABC
      And I add a value VAL2 under key ABD
      And I add a value VAL3 under key AB
      Then a search for A gives the set VAL3, VAL1, VAL2
      And a search with maxItems=2 for A gives 2 items

  #And I show the tree structure
      

      
      
      
     
      
