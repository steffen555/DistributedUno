from subprocess import PoPEpen, PI
import time
import fcntl, os

PORT_BASE = 5000

def set_nonblocking(fd):
    flags = fcntl.fcntl(fd, fcntl.F_GETFL)
    flags = flags | os.O_NONBLOCK
    fcntl.fcntl(fd, fcntl.F_SETFL, flags)

def spawn(i, num_players=2):
	my_port = PORT_BASE + i

	if i == 0:
		# this is the host
		cmd = "UNO_AUTOPLAY=1 java Main %d %d" % (my_port, num_players)
	else:
		# join the host
		host_port = PORT_BASE
		cmd = "UNO_AUTOPLAY=1 java Main %d 127.0.0.1 %d" % (my_port, host_port)
	
	return Popen(cmd, shell=True, stdin = PIPE, stdout = PIPE, stderr = PIPE, bufsize = 1)

class Process:
	 def __init__(self, i, num_players=2):
		self.p = spawn(i, num_players)

		set_nonblocking(self.p.stdout)
		set_nonblocking(self.p.stderr)

		self.read_until("your name")
		self.write("peer #%d\n" % i)

		if i == 0:
			self.read_until("hosting")
		else:
			self.read_until("joining")

	def read(self):
		result = ""
		while True:
			try:
				r = self.p.stdout.read(1)
				result += r
				if r == "":
					break
			except IOError:
				break
		
		return result
	
	def read_until(self, text):
		result = ""
		while True:
			if text in result:
				break

			try:
				r = self.p.stdout.read(1)
				result += r
				if r == "":
					break
			except IOError:
				time.sleep(0.01)

		return result

	
	def write(self, text):
		return self.p.stdin.write(text)


def do_run_with_n_players(num_players):
	os.system("pkill --signal SIGKILL -f 'java Main' 2>/dev/null ") # clean up old experiments..
	time.sleep(1) # wait for them to terminate

	players = []
	for i in range(num_players):
		p = Process(i, num_players=num_players)
		players.append(p)

	done = False
	outputs = [""]*num_players
	while not done:
		time.sleep(0.1)
		for i, p in enumerate(players):
			r = p.read()
			if len(r) != 0:
				print len(r)
			outputs[i] += r
			if all("is the winner" in output for output in outputs):
				done = True


def run_with_n_players(num_players):
	while True:
		try:
			do_run_with_n_players(num_players)
			break
		except KeyboardInterrupt:
			print "Retrying with", num_players, "players"
