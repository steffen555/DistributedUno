import spawnlib
import os
import time

os.system("rm *log.txt* 2>/dev/null")

bits = raw_input("How many bits are used for the primes? ")

filename = "crypto_experiment_%s_bits_%d.txt" % (bits, int(time.time()))
f = open(filename, "w")


num_players = 6
spawnlib.run_with_n_players(num_players)


def process_logfile(filename):
	for line in open(filename):
		if "ns to make shuffled deck" in line:
			shuffle_time = int(line.split(" ")[3])
			shuffle_times.append(shuffle_time)
			
		if "Decrypted once in " in line:
			num = int(line.split(" ")[6])
			decrypt_times.append(num)


shuffle_times = []
decrypt_times = []

for filename in os.listdir("."):
	if filename.startswith("timelog.txt"):
		process_logfile(filename)


f.write("Shuffle times: %s\n" % repr(shuffle_times))
f.write("Decrypt times: %s\n" % repr(decrypt_times))
f.flush()
f.close()


