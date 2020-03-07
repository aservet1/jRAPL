#include "line.hh"

#include <cmath>
#include <string>
#include <sstream>

double Line::length() const {
  return p1.distance(p2);
}

std::string Line::toString() const
{
  return p1.toString() + " -- " + p2.toString();
}
