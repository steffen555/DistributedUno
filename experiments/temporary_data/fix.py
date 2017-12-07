for line in open("number_of_messages_experiment_1512645680.txt"):
	line = line.strip()

	# skip broadcasts
	if "broadcast" in line:
		continue
	
	assert "sent:" in line

	# grab the number of players
	num_players = int(line.split(" ")[0])

	# grab the list
	data = eval(line.split(" ", 3)[3]) 

	for point in data:
		print "%d, %d" % (num_players, point)

