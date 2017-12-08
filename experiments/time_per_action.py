import spawnlib
import os
import time

MAX_PLAYERS = 15

def average(nums):
	return float(sum(nums)) / len(nums)

def process_logfile(filename):
	for line in open(filename):
		if "ns to make shuffled deck" in line:
			num = int(line.split(" ")[3])
			shuffle_times.append(num)
		if "Time to perform a play move:" in line:
			num = int(line.split(" ")[9])
			play_move_times.append(num)
		if "Time to perform a draw move:" in line:
			num = int(line.split(" ")[9])
			draw_move_times.append(num)
			

filename = "time_per_action_experiment_%d.txt" % int(time.time())
f = open(filename, "w")

for num_players in range(2, MAX_PLAYERS+1):

	while True:
		os.system("rm *log.txt* 2>/dev/null")
		if spawnlib.run_with_n_players(num_players):
			break

	time.sleep(1)

	play_move_times = []
	draw_move_times = []
	shuffle_times = []

	for filename in os.listdir("."):
		if filename.startswith("timelog.txt"):
			process_logfile(filename)
	
	f.write("%d players. Play move times: %s\n" % (num_players, repr(play_move_times)))
	f.write("%d players. Draw move times: %s\n" % (num_players, repr(draw_move_times)))
	f.write("%d players. Shuffle times: %s\n" % (num_players, repr(shuffle_times)))

	print("%d players. Play move times: %s\n" % (num_players, repr(play_move_times)))
	print("%d players. Draw move times: %s\n" % (num_players, repr(draw_move_times)))
	print("%d players. Shuffle times: %s\n" % (num_players, repr(shuffle_times)))

	f.flush()

f.close()

