import math
import statistics

''' https://math.stackexchange.com/questions/1547141/aggregating-standard-deviation-to-a-summary-point?fbclid=IwAR3GpT8cNoNbMHntA1dKhWKHGXvBj2W-t7NQU29qoqtsg37uZKZgkeDM-aE <-- formulas for aggr_mean and aggr_stdev '''
def aggr_mean(sample_sizes, averages):
    assert len(sample_sizes) == len(averages)
    return sum([ (sample_sizes[i]*averages[i]) for i in range(len(sample_sizes)) ]) / sum(sample_sizes)

# def aggr_stdev(sample_sizes, stdevs):
#     assert len(sample_sizes) == len(stdevs)
#     return math.sqrt(sum([ (sample_sizes[i]*(stdevs[i]**2)) for i in range(len(sample_sizes)) ]) / sum (sample_sizes))

''' https://en.wikipedia.org/wiki/Propagation_of_uncertainty '''
def propagate_uncertainty_through_average(sample_sizes, stdevs):
    assert len(sample_sizes) == len(stdevs); N = len(stdevs)
    # weighted_variances = [ (stdevs[i]**2) * (sample_sizes[i]**2) for i in range(N) ]
    variances = [ s**2 for s in stdevs ] 
    average_variance = sum(variances) / N**2
    return math.sqrt(average_variance)


def aggregate_memory_stats(memory_data):
    mem_stats = {}
    sample_sizes = [dat['num_samples'] for dat in memory_data]
    mem_stats['avg']   = aggr_mean (sample_sizes , [dat['avg'] for dat in memory_data])
    mem_stats['stdev'] = propagate_uncertainty_through_average(sample_sizes, [dat['stdev'] for dat in memory_data])
    mem_stats['num_samples'] = statistics.mean([ dat['num_samples'] for dat in memory_data ]) #TODO we want {avg:..., stdev:...}. don't we?

    ##.#.## -- Do not delete these! We will probably end up not includling these metrics, but we might!! Do not delete them unless they are confirmed useless!
    ##.#.## mem_stats['global_min'] = min( [ dat['min'] for dat in memory_data] )
    ##.#.## mem_stats['global_max'] = max( [ dat['max'] for dat in memory_data] )
    ##.#.## mem_stats['min'] = statistics.mean( [ dat['min'] for dat in memory_data ] )
    ##.#.## mem_stats['max'] = statistics.mean( [ dat['max'] for dat in memory_data ] )
    ##.#.## mem_stats['median_median'] = statistics.median( [ dat['median'] for dat in memory_data ] )
    ##.#.## mem_stats['median'] = statistics.mean( [ dat['median'] for dat in memory_data ] )

    return mem_stats

"""
  Assumes 'data' is an array of structurally identical JSON object, where
  all of the leaf objects have keys ['avg','num_samples','stdev'] with number values.
  Recursively descends and then aggregates each of these three fields with the appropriate
  aggregation function.
"""
def general_aggregate(data):
	res = {}
	if sorted(list(data[0].keys())) != sorted(['avg','num_samples','stdev']):
		for k in data[0].keys():
			res[k] = general_aggregate( [ d[k] for d in data ] )
	else: # at the leaves
		sample_sizes = [ d['num_samples'] for d in data ]
		res['avg'] = aggr_mean(sample_sizes,  [d['avg'] for d in data ])
		res['stdev'] = propagate_uncertainty_through_average( sample_sizes, [ d['stdev'] for d in data ] )
		res['num_samples'] = statistics.mean( [ d['num_samples'] for d in data ] )

	return res

def percent_diff(a,b):
    return (a-b)/((a+b)/2) * 100

''' https://en.wikipedia.org/wiki/Propagation_of_uncertainty '''
def percent_diff_propagate_uncertainty(sa,sb,a,b):
    subtract = a-b
    subtract_sd = math.sqrt((sa**2)+(sb**2))
    average = (a/2) + (b/2)
    average_sd = math.sqrt( (sa**2 / 2**2) + (sb**2 / 2**2) )
    return math.sqrt( ((subtract/average)**2) * ( (subtract_sd/subtract)**2 + (average_sd/average)**2 ) * 100 )