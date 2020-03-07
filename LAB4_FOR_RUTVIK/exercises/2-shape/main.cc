#include "point.hh"
#include "shapes.hh"
#include "tostring.hh"

#include <iostream>

static const Circle circles[] = {
  Circle(1, Point()),
  Circle(2, Point(1, 1)),
};
static const Rectangle rectangles[] = {
  Rectangle(Point(1, 1), Point(4, 5)),
  Rectangle(Point(0, 0), Point(5, 4)),
};

static const Shape* shapes[] = {
  &circles[0], &rectangles[0], &circles[1], &rectangles[1],
};
constexpr auto nShapes = sizeof(shapes)/sizeof(shapes[0]);


static void
outSizes(std::ostream& out)
{
  out << "sizeof(double) = " << sizeof(double) << std::endl;
  out << "sizeof(Point) = " << sizeof(Point) << std::endl << std::endl;

  out << "sizeof(ToString) = " << sizeof(ToString) << std::endl;
  out << "sizeof(Shape) = " << sizeof(Shape) << std::endl;
  out << "sizeof(Circle) = " << sizeof(Circle) << std::endl;
  out << "sizeof(Rectangle) = " << sizeof(Rectangle) << std::endl;
}

static void
outShapes(std::ostream& out)
{
  for (auto i = 0u; i < nShapes; i++) {

    //pointed to Shape could be either a Circle or Rectangle.
    auto shapeP = shapes[i];

    //if shapeP points to a Circle, then call Circle::perimeter();
    //if shapeP points to a Rectangle, then call Rectangle::perimeter();
    //choosing the code at runtime is referred to as *runtime polymorphism*.
    auto perim = shapeP->perimeter();
    auto area = shapeP->area();

    out << *shapeP << std::endl
        << "\tperimeter:\t" << perim << std::endl
        << "\tarea:\t\t" << area << std::endl;
  }
}

int main() {
  std::ostream& out = std::cout;
  outShapes(out); out << std::endl;
  outSizes(out); out << std::endl;
}
