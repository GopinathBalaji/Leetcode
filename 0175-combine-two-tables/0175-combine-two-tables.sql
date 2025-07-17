# Use LEFT JOIN to include all records from Person, even if there's no corresponding address.
SELECT firstName, lastName, city, state 
FROM Person
LEFT JOIN Address ON Person.personId = Address.personId;