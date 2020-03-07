#ifndef PARSE_HH_
#define  PARSE_HH_

#include "expr.hh"

#include <string>

/** Parser for prefix arith expressions involving integers, +, -, *, / */
ExprPtr parse(std::string str);


#endif //ifndef PARSE_HH_
