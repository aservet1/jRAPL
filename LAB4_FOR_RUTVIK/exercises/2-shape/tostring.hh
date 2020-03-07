#ifndef TO_STRING_HH_
#define TO_STRING_HH_

#include <iostream>
#include <string>

/*
This specification file sets up a class `ToString` which should
contain a method called `toString()`.  This method is declared
virtual, which means that this method can be overridden by any classes
which inherit from `ToString`.  The `= 0` means that the `ToString`
class does not provide any implementation for the `toString()` method;
this makes `toString()` a *pure virtual method*.  Since there is no
implementation for `toString()` it is impossible to create an instance
of `ToString` and it is referred to as an *abstract class*.
*/

//Inheriting from this abstract class and defining a toString
//method allows a class to use << on an ostream.
struct ToString {
  virtual std::string toString() const = 0;
};

/*
This declaration provides the raison d'etre for the `ToString` class.
The declaration overloads the `<<` operator to allow output of
`ToString` objects.  The `tostring.cc` implementation file provides a
trivial implementation.
*/
std::ostream& operator<<(std::ostream& out, const ToString& obj);
  
#endif //ifndef TO_STRING_HH_
