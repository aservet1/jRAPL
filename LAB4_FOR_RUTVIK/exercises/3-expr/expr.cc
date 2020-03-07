#include "expr.hh"

#include <string>
#include <sstream>

std::string
IntExpr::toString() const
{
  std::stringstream s;
  s << value;
  return s.str();
}

int
IntExpr::eval() const
{
  return value;
}

static std::string
binaryExprToString(std::string op, ExprPtr left, ExprPtr right)
{
  std::stringstream s;
  s << "(" << *left << ") " << op << " (" << *right << ")";
  return s.str();
}

int
AddExpr::eval() const
{
  return (*left).eval() + (*right).eval();
}

std::string
AddExpr::toString() const
{
  return binaryExprToString("+", left, right);
}

int
SubExpr::eval() const
{
  return (*left).eval() - (*right).eval();
}

std::string
SubExpr::toString() const
{
  return binaryExprToString("-", left, right);
}

int
MulExpr::eval() const
{
  return (*left).eval() * (*right).eval();
}

std::string
MulExpr::toString() const
{
  return binaryExprToString("*", left, right);
}

int
DivExpr::eval() const
{
  return (*left).eval() / (*right).eval();
}

std::string
DivExpr::toString() const
{
  return binaryExprToString("/", left, right);
}
