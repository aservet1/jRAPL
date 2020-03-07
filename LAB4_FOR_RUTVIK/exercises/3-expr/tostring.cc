#include "tostring.hh"

#include <iostream>
#include <string>


std::ostream&
operator<<(std::ostream& out, const ToString& obj)
{
  return out << obj.toString();
}
