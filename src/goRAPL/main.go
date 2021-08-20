package main

//#include "cdefs.h"
import "C"

//import "unsafe"
import "fmt"
import "time"

func main() {
	socket := C.int(1);
	before := C.energyStatCheckPerSocket(socket);
	time.Sleep(2 * time.Second);
	after  := C.energyStatCheckPerSocket(socket);
	diff := C.energy_stats_subtract(after, before);
	fmt.Println(diff);
}
