#pragma once
// - $$ --------------- $$

using namespace rapl {
	void init();
	void dealloc();
	double DRAM();
	double CORE();
	double PKG();
	double GPU();
}
// - $$ --------------- $$
