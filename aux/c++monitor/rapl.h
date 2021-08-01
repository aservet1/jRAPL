#pragma once
// - $$ --------------- $$

namespace rapl {
	void Init();
	void Dealloc();
	double DRAM();
	double CORE();
	double PKG();
	double GPU();
}
// - $$ --------------- $$
