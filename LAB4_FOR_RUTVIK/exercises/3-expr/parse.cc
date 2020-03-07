#include "parse.hh"

#include <sstream>
#include <string>
#include <cstdlib>
#include <cctype>


static ExprPtr
parse(std::stringstream& s, std::string str)
{
  if (s.eof()) {
    std::cerr << "unexpected end-of-input \"" << str << "\"" << std::endl;
    std::exit(1);
  }
  char c;
  s >> c;
  if (std::isdigit(c)) {
    s.unget();
    int val;
    s >> val;
    return IntExpr::make(val);
  }
  else {
    ExprPtr left = parse(s, str);
    ExprPtr right = parse(s, str);
    switch (c) {
    case '+':
      return AddExpr::make(left, right);
    case '-':
      return SubExpr::make(left, right);
    case '*':
      return MulExpr::make(left, right);
    case '/':
      return DivExpr::make(left, right);
    default:
      std::cerr << "unexpected char '" << c << "' in input \""
		<< str << "\"" << std::endl;
      std::exit(1);
    }
  }
}

ExprPtr
parse(std::string str) {
  std::stringstream s(str);
  return parse(s, str);
}

