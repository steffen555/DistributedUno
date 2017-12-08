import os

outdir = "cleaned_data/"

def write_headers():
	open(outdir + "tpa_shuffle_times.csv", "w").write("filename,num_players,time_in_ns\n")
	open(outdir + "tpa_draw_times.csv", "w").write("filename,num_players,time_in_ns\n")
	open(outdir + "tpa_play_times.csv", "w").write("filename,num_players,time_in_ns\n")
	open(outdir + "num_messages_sent.csv", "w").write("filename,num_players,num_messages_sent\n")
	open(outdir + "bandwidth_used.csv", "w").write("filename,num_players,total_bandwidth_used_by_all_players_in_bytes\n")
	open(outdir + "crypto_shuffle_times.csv", "w").write("filename,bits_used_for_primes,time_spent_to_shuffle_in_ns\n")
	open(outdir + "crypto_decrypt_times.csv", "w").write("filename,bits_used_for_primes,time_spent_per_decryption_in_ns\n")

def average(ns):
	return float(sum(ns))/len(ns)


def handle_tpa(filename):
	f1 = open(outdir + "tpa_shuffle_times.csv", "a")
	f2 = open(outdir + "tpa_draw_times.csv", "a")
	f3 = open(outdir + "tpa_play_times.csv", "a")

	for line in open(filename):

		num_players = int(line.split(" ")[0])

		times = eval("[" + line[line.find(" [")+2:])

		if len(times) == 0:
			continue # no data..

		for t in times:


			if "Shuffle times:" in line:
				f1.write("%s,%s,%s\n" % (filename, num_players, t))

			elif "Draw move times:" in line:
				f2.write("%s,%s,%s\n" % (filename, num_players, t))

			elif "Play move times:" in line:
				f3.write("%s,%s,%s\n" % (filename, num_players, t))

			else:
				print "weird line:", line


def handle_bandwidth(filename):
	f1 = open(outdir + "bandwidth_used.csv", "a")
	for line in open(filename):
		num_players = int(line.split(" ")[0])
		total_bw = int(line.split(" ")[2])
		f1.write("%s,%s,%s\n" % (filename, num_players, total_bw))

def handle_num_messages(filename):
	f1 = open(outdir + "num_messages_sent.csv", "a")
	for line in open(filename):
		num_players = int(line.split(" ")[0])
		times = eval("[" + line[line.find(" [")+2:])
		if len(times) == 0:
			continue # no data..
		for t in times:
			if "sent:" in line:
				f1.write("%s,%s,%s\n" % (filename, num_players, t))

def handle_crypto(filename):
	f1 = open(outdir + "crypto_shuffle_times.csv", "a")
	f2 = open(outdir + "crypto_decrypt_times.csv", "a")
	bits = int(filename.split("_")[3])
	for line in open(filename):
		times = eval("[" + line[line.find(" [")+2:])

		for t in times:
			if "Shuffle times: " in line:
				f1.write("%s,%s,%s\n" % (filename, bits, t))
			elif "Decrypt times: " in line:
				f2.write("%s,%s,%s\n" % (filename, bits, t))
			
			else:
				print "weird line:", line




write_headers()

for filename in os.listdir("real_data"):
	filename = "real_data/" + filename

	if "bandwidth" in filename:
		handle_bandwidth(filename)
	
	if "number_of_messages_experiment" in filename:
		handle_num_messages(filename)
	
	if "crypto_experiment" in filename:
		handle_crypto(filename)
	
	if "time_per_action" in filename:
		handle_tpa(filename)

