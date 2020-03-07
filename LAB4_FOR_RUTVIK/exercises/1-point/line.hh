#ifndef LINE_HH_
#define LINE_HH_

#include <string>
#include "point.hh"

struct Line {
  //for the purpose of this lab, these fields are public but const.
  //Having them public makes it hard to change the reprentation
  //from these cartesian coordinates to polar coordinates.
  const Point p1, p2;

  /** constructor */
  Line(Point p1, Point p2) : p1(p1), p2(p2) { }

  /** return length of this line */
  //the trailing const means that this function will not change
  //this line
  double length() const;

  /** return a string representation of this line */
  std::string toString() const;
};



#endif //ifndef LINE_HH_
