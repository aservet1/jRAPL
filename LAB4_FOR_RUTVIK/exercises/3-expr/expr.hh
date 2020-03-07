#ifndef EXPR_HH_
#define EXPR_HH_

#include "tostring.hh"

#include <memory>
#include <string>


class Expr : public ToString {
public:
  virtual int eval() const = 0;
};

//ExprPtr is an alias for a smart shared ptr to an Expr.
//using is like a typedef
using ExprPtr = std::shared_ptr<Expr>;

/** An IntExpr only contains an integer value */
class IntExpr : public Expr {
private:
  enum class Private { TAG };
public:
  const int value;

  /** Factory function */
  static ExprPtr make(int val) {
    return std::make_shared<IntExpr>(val, Private::TAG);
  }

  int eval() const;

  std::string toString() const;

  //use Private to ensure this constructor cannot be called from outside
  //this class even though it is public
  IntExpr(int val, Private x) : value(val) { }

}; //IntExpr

//KISS results in repetitive code for binary expressions.
//Not using alternatives like a template class


class AddExpr : public Expr {
private:
  enum class Private { TAG };
public:
  const ExprPtr left;
  const ExprPtr right;

  /** Factory function */
  static ExprPtr make(ExprPtr left, ExprPtr right) {
    return std::make_shared<AddExpr>(left, right, Private::TAG);
  }

  int eval() const;

  std::string toString() const;

  //use Private to ensure this constructor cannot be called from outside
  //this class even though it is public
  AddExpr(ExprPtr& left, ExprPtr& right, Private x) :
    left(left), right(right) {
  }
}; //AddExpr


class SubExpr : public Expr {
private:
  enum class Private { TAG };
public:
  const ExprPtr left;
  const ExprPtr right;

  /** Factory function */
  static ExprPtr make(ExprPtr left, ExprPtr right) {
    return std::make_shared<SubExpr>(left, right, Private::TAG);
  }

  int eval() const;

  std::string toString() const;

  //use Private to ensure this constructor cannot be called from outside
  //this class even though it is public
  SubExpr(ExprPtr& left, ExprPtr& right, Private x) :
    left(left), right(right) {
  }
}; //SubExpr


class MulExpr : public Expr {
private:
  enum class Private { TAG };
public:
  const ExprPtr left;
  const ExprPtr right;

  /** Factory function */
  static ExprPtr make(ExprPtr left, ExprPtr right) {
    return std::make_shared<MulExpr>(left, right, Private::TAG);
  }

  int eval() const;

  std::string toString() const;

  //use Private to ensure this constructor cannot be called from outside
  //this class even though it is public
  MulExpr(ExprPtr& left, ExprPtr& right, Private x) :
    left(left), right(right) {
  }
}; //MulExpr

class DivExpr : public Expr {
private:
  enum class Private { TAG };
public:
  const ExprPtr left;
  const ExprPtr right;

  /** Factory function */
  static ExprPtr make(ExprPtr left, ExprPtr right) {
    return std::make_shared<DivExpr>(left, right, Private::TAG);
  }

  int eval() const;

  std::string toString() const;

  //use Private to ensure this constructor cannot be called from outside
  //this class even though it is public
  DivExpr(ExprPtr& left, ExprPtr& right, Private x) :
    left(left), right(right) {
  }
}; //DivExpr




#endif //ifndef EXPR_HH
