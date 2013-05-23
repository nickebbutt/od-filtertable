
  Feature: Suffix Tree
    
    Scenario: Add items top level
      Given I create a suffix tree
      When I add a value VAL1 under key A
      And I add a value VAL2 under key B
      Then a search for A returns VAL1
      And a search for B returns VAL2
      
    Background:
      Given I create a suffix tree
    
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
      Then the suffix tree contains keys AA, AAA, ABA, BA
      And the number of nodes is 7
      And a search for A returns VAL3, VAL2, VAL1
      And a search for AA returns VAL3, VAL2
      And a search for AB returns VAL1
      And a search for B returns VAL4
      And a search for BA returns VAL4
      
    Scenario: Adding items out of alphabetical order
      When I add a value VAL1 under key AC
      When I add a value VAL2 under key AB
      When I add a value VAL3 under key AA
      Then the suffix tree contains keys AA, AB, AC
      And the number of nodes is 5
      And a search for A returns VAL3, VAL2, VAL1

    @REMOVE
    Scenario: I can remove a value
      When I add a value VAL1 under key ABC
      When I add a value VAL2 under key ABC
      And I remove a value VAL1 under key ABC
      Then a search for ABC returns VAL2 
  

  #And I show the tree structure
      

      
      
      
     
      
