#ifndef POINT_HH_
#define POINT_HH_

#include <string>

#include "tostring.hh"

//similar to Point from previous exercise, but this definition extends
//ToString.
struct Point : public ToString {
  const double x, y;
  Point(double x=0, double y=0) : x(x), y(y) { }
  double distance(const Point& other) const;
  std::string toString() const;
};



#endif //ifndef POINT_HH_
