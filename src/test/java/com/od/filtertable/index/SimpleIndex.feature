
  Feature: Simple Index
    
    Scenario: Index Ordering
      Given I create a simple index
      When I add value E under key AB
      And I add value B under key AA
      And I add value R under key AD
      And I add value A under key AC
      And a prefix search for A returns in order B,E,A,R


    Scenario: Index Ordering Shallower Results First
      Given I create a simple index
      When I add value A under key AA
      And I add value B under key AAA
      And I add value C under key AAC
      And I add value D under key AAD
      And a prefix search for A returns in order A,B,C,D