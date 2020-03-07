#ifndef TO_STRING_HH_
#define TO_STRING_HH_

#include <iostream>
#include <string>

//Inheriting from this abstract class and defining a toString
//method allows a class to use << on an ostream.
struct ToString {
  virtual std::string toString() const = 0;
};

std::ostream& operator<<(std::ostream& out, const ToString& obj);
  
#endif //ifndef TO_STRING_HH_
