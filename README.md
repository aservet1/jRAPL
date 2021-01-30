# jRAPL
If you stumbled upon this repo, feel free to use whatever you want out of it, but the documentation is definitely not guaranteed to be accurate. Project still 
under development. The energy reading utilities from Java work but you kind of have to figure out what to use and how it works :) Feel free to email me and ask,
I'd be more than happy to give you a run-down of what's usable and what's still under development.

Repo for Alejandro and Rutvik to collaborate on the jRAPL project (version 2.0)

Based on jRAPL files originally obtained from Kenan's original jRAPL project: https://github.com/kliu20/jRAPL. jRAPL is used a couple of times here and
there if you search it in GitHub. This repo is for Alejandro to formalize and expand the jRAPL energy programming interface.

See Kenan's README (above link) for general information about jRAPL, although it's quite different from the current implementation details. However, there
is still generally useful knowledge about jRAPL in it. I will have an extensive README once the development details are ironed out.

## How to build
Execute the `./build.sh` shell script. It compiles the native library, copies it into the maven project and uses maven to build up `jRAPL-{version}.jar` file
found in the `target` directory. You can then include the jar like any other, either in your IDE or on the command line with `-cp` when you build and run other projects that use it. Some classes have sample `main()`drivers that you can execute, for example `sudo java -cp jRAPL-1.0.jar jRAPL.SyncEnergyMonitor`.

#### Contact
Any questions, feel free to email. Alejandro Servetto {aservet1@binghamton.edu}
