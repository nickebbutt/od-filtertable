Configurations: Simple, Dynamic

Uses: Standard Index

  Feature: Standard Index Features
    
    Scenario: Simple matching
      Given I create an index
      When I add value A under key MYKEY
      And I add value B under key MYKEY
      Then a prefix search for MYKEY returns A, B


    Scenario: Prefix matching
      Given I create an index
      When I add value A under key MYKEY
      And I add value B under key MYKE
      Then a prefix search for MYKEY returns A
      And a prefix search for MYKE returns A, B
      And a prefix search for M returns A, B
       