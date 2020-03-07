#include "point.hh"

#include <cmath>
#include <string>
#include <sstream>

/** return distance of this point from other point */
double Point::distance(const Point& other) const {
  const auto dx = x - other.x;
  const auto dy = y - other.y;
  return sqrt(dx*dx + dy*dy);
}

std::string Point::toString() const {
  //declaring s to be a stringstream allows usage similar to std::cout
  std::stringstream s;
  s << "(" << x << ", " << y << ")"; //normal (x, y) format
  return s.str(); //return std::string underlying stringstream
}
