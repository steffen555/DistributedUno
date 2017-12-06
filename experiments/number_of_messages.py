import spawnlib
import os
import time

MAX_PLAYERS = 30

def average(nums):
	return float(sum(nums)) / len(nums)

def process_logfile(filename):
	for line in open(filename):
		if "Number of messages sent:" in line:
			num = int(line.split(": ")[2])
			num_sent.append(num)
			
		if "Number of messages broadcast:" in line:
			num = int(line.split(": ")[2])
			num_broadcast.append(num)

filename = "number_of_messages_experiment_%d.txt" % int(time.time())
f = open(filename, "w")

for num_players in range(2, MAX_PLAYERS+1):

	os.system("rm *log.txt* 2>/dev/null")

	spawnlib.run_with_n_players(num_players)
	time.sleep(1)

	num_broadcast = []
	num_sent = []

	for filename in os.listdir("."):
		if filename.startswith("log.txt"):
			process_logfile(filename)
	

	sent = average(num_sent)
	print num_players, sent, num_sent
	assert len(num_sent) == num_players
	f.write("%d players, %f messages sent\n" % (num_players, sent))
	f.flush()

	# print "broadcast:", num_broadcast
	# print "sent:     ", num_sent