#ifndef SHAPES_HH_
#define SHAPES_HH_

#include "point.hh"
#include "tostring.hh"

#define _USE_MATH_DEFINES 1
#include <cmath>
#include <string>
#include <sstream>

/* A Shape will implement a toString() as well as a perimeter()
 * method.  Since both methods are pure virtual, Shape is an
 * abstract class and cannot be instantiated.
 */
class Shape : public ToString {
public:
  virtual double perimeter() const = 0;
  virtual double area() const = 0;
};

/* A Circle is a concrete Shape */
struct Circle : public Shape {
  const double radius;
  const Point origin;

  /** constructor: default will build unit circle */
  Circle(double radius=1, const Point& point=Point())
    : radius(radius), origin(point)
  {}

  /** return perimeter of this circle */
  double perimeter() const { return 2 * M_PI * radius; }

  /** return area of circle */
  double area() const { return M_PI * radius * radius; }

  /** return string representation of this circle */
  std::string toString() const {
    std::stringstream s;
    s << "Circle[" << origin << ", " << radius << "]";
    return s.str();
  }

};

/* A Rectangle is another concrete Shape */
struct Rectangle : public Shape {
  const Point topLeft, bottomRight;

  /** constructor: no default value for parameters */
  Rectangle(Point p1, Point p2) : topLeft(p1), bottomRight(p2) {}

  /** return perimeter of this rectangle */
  double perimeter() const {
    double width = bottomRight.x - topLeft.x;
    if (width < 0) width = -width;
    double height = bottomRight.y - topLeft.y;
    if (height < 0) height = -height;
    return 2 * (width + height);
  }

  /** return area of rectangle */
  double area() const {
    double width = bottomRight.x - topLeft.x;
    if (width < 0) width = -width;
    double height = bottomRight.y - topLeft.y;
    if (height < 0) height = -height;
    return width * height;
  }

  /** return string representation of this rectangle */
  std::string toString() const {
    std::stringstream s;
    s << "Rectangle[" << topLeft.toString() << ", "
      << bottomRight.toString() << "]";
    return s.str();
  }

};
#endif //ifndef SHAPES_HH_
