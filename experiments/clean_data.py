import os

outdir = "cleaned_data/"

def write_headers():
	open(outdir + "tpa_shuffle_times.txt", "w").write("filename,num_players,time_in_ns\n")
	open(outdir + "tpa_draw_times.txt", "w").write("filename,num_players,time_in_ns\n")
	open(outdir + "tpa_play_times.txt", "w").write("filename,num_players,time_in_ns\n")

def average(ns):
	return float(sum(ns))/len(ns)


def handle_tpa(filename):
	f1 = open(outdir + "tpa_shuffle_times.txt", "a")
	f2 = open(outdir + "tpa_draw_times.txt", "a")
	f3 = open(outdir + "tpa_play_times.txt", "a")

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
	pass

def handle_num_messages(filename):
	pass

def handle_crypto(filename):
	pass


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

