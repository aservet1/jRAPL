#ifndef POINT_HH_
#define POINT_HH_

#include <string>

struct Point {
  //for the purpose of this lab, these fields are public but const.
  //Having them public makes it hard to change the reprentation
  //from these cartesian coordinates to polar coordinates.
  const double x, y;

  /** constructor */
  Point(double x=0, double y=0) : x(x), y(y) { }

  /** return distance of this point from other point */
  //the trailing const means that this function will not change
  //this point
  double distance(const Point& other) const;

  /** return a string representation of this point */
  std::string toString() const;
};



#endif //ifndef POINT_HH_
