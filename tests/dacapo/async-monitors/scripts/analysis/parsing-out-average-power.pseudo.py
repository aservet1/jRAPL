
This will eventually get added to the first, per-iteration analysis script

provided:
	diffed energy (joules)
	diffed timestamps (usecs)
	these are parallel arrays

power_values = []
elapsed_time = 0
for joules, usecs in zip(diffed_energy, diffed_timestamps):
	if approx_modulo(elapsed_time, 1 second):
		power = joulespersec / counted_second
		joulespersec = 0
		counted_second = 0
