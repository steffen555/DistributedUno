import spawnlib
import os
import time

MAX_NUM_PLAYERS = 10


def get_loopback_bandwidth_usage():
	raw = os.popen("ifconfig lo").read()
	for line in raw.split("\n"):
		if "TX packets" in line:
			return int(line.split(" ")[13]) # bytes used


filename = "bandwidth_experiment_%d.txt" % (int(time.time()))
f = open(filename, "w")

for num_players in range(2, MAX_NUM_PLAYERS):
	print "start", num_players
	begin_usage = get_loopback_bandwidth_usage()

	spawnlib.run_with_n_players(num_players)

	end_usage = get_loopback_bandwidth_usage()
	print "end", num_players

	usage = end_usage - begin_usage

	f.write("%d players, %d bytes used\n" % (num_players, usage))
	f.flush()

	print("%d players, %d bytes used\n" % (num_players, usage))


f.close()



