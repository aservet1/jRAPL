#include "point.hh"

#include <cmath>
#include <string>
#include <sstream>

double Point::distance(const Point& other) const {
  const auto dx = x - other.x;
  const auto dy = y - other.y;
  return sqrt(dx*dx + dy*dy);
}

std::string Point::toString() const {
  std::stringstream s;
  s << "(" << x << ", " << y << ")";
  return s.str();
}
