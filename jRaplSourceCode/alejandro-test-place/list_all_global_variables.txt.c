/*
	Remember to make the variable names more descriptive too!!
*/

<<----------arch_spec.c--------->>
(*)uint32_t eax, ebx, ecx, edx;	//made them local to get_cpu_model (only place where I found them used)
(*)uint32_t cpu_model;			//get_cpu_model( ) just called every function that is needed

(*)int read_time = 0;		//removed b/c never used
(*)uint64_t max_pkg = 0;	//removed (for now) b/c only used in commented out sections
(*)uint64_t num_core_thread = 0;// made a get( ) function
(*)uint64_t num_pkg_thread = 0;	// made a get( ) function
(*)uint64_t num_pkg_core = 0; 	// made a get( ) function
(*)uint64_t num_pkg = 0; 		// is already returned from getSocketNum, so can be get( ) from there. made it a static global for CPUScaler.c bc it's so entrenched
(*)int core = 0;	//was only used in CPUScaler.c/ProfileInit, so just making it local to that function
(*)int coreNum = 0; //it's already returned from core_num(  ) function

(*)cpuid_info_t cpu_info;	//never used so removed
<-------arch_spec.h------>
/*( )extern int core;

( )extern int read_time;
( )extern uint64_t max_pkg;
( )extern uint64_t num_core_thread; //number of physical threads per core
( )extern uint64_t num_pkg_thread; //number of physical threads per package 
( )extern uint64_t num_pkg_core;  //number of cores per package
( )extern uint64_t num_pkg;      //number of packages for current machine

( )extern int coreNum;*/
<<------------------------------>>

<<----------CPUScaler.c--------->>
(*)rapl_msr_unit rapl_unit;	// mage a get( ) function
(*)rapl_msr_parameter *parameters; //static'd
/*global variable*/
(*)int *fd;		//static'd
<-------CPUScaler.h------>
no global variables
<<------------------------------>>

<<-------------msr.c------------>>
(*)double WRAPAROUND_VALUE; //gone, made get_wraparound_value( ) return on its own
(*)double F_arr[4] = {1.0, 1.25, 1.50, 1.75}; //made static const
//factor of F for time_window_limit. It represents these four value. //was at one point {1.1, 1.2, 1.3, 1.4}
<---------msr.h---------->
/*extern char *ener_info;
extern rapl_msr_unit rapl_unit;
extern int *fd;
extern rapl_msr_parameter *parameters;*/
<<------------------------------>>
