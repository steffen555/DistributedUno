import spawnlib
import os

os.system("rm *log.txt* 2>/dev/null")

spawnlib.run_with_n_players(2)

num_broadcast = []
num_sent = []

def process_logfile(filename):
	for line in open(filename):
		if "Number of messages sent:" in line:
			num = int(line.split(": ")[2])
			num_sent.append(num)
			
		if "Number of messages broadcast:" in line:
			num = int(line.split(": ")[2])
			num_broadcast.append(num)



for filename in os.listdir("."):
	if filename.startswith("log.txt"):
		process_logfile(filename)


print "broadcast:", num_broadcast
print "sent:     ", num_sent
